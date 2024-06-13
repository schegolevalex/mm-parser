package com.schegolevalex.mm.mmparser.bot.state;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.entity.Promo;
import com.schegolevalex.mm.mmparser.service.ProductService;
import com.schegolevalex.mm.mmparser.service.PromoService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.LinkPreviewOptions;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@Transactional
public class WatchProductsState extends BaseState {
    private final ProductService productService;
    private final PromoService promoService;

    public WatchProductsState(@Lazy ParserBot bot, ProductService productService, PromoService promoService) {
        super(bot);
        this.productService = productService;
        this.promoService = promoService;
    }

    @Override
    public void route(Update update) {
        Long chatId = getChatId(update);
        // обработка inline-кнопок
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();

            // обработка кнопки "Удаление продукта"
            if (callbackData.startsWith(Constant.Button.DELETE_PRODUCT)) {
                productService.findById(Long.parseLong(callbackData.split(Constant.DELIMITER)[1]))
                        .ifPresent(product -> product.setActive(false));
                bot.getSilent().execute(DeleteMessage.builder()
                        .chatId(chatId)
                        .messageId(update.getCallbackQuery().getMessage().getMessageId())
                        .build());

            // обработка кнопки "Применение промокода к продукту"
            } else if (callbackData.startsWith(Constant.Button.APPLY_PROMO)) {
                long productId = Long.parseLong(callbackData.split(Constant.DELIMITER)[1]);
                List<Promo> promos = promoService.findAllByChatId(chatId);
                bot.getSilent().execute(EditMessageReplyMarkup.builder()
                        .chatId(chatId)
                        .messageId(update.getCallbackQuery().getMessage().getMessageId())
                        .replyMarkup(Keyboard.withSelectPromoToProduct(promos, productId))
                        .build());

            // обработка кнопки с конкретным промокодом
            } else if (callbackData.startsWith(Constant.Button.MY_PRODUCTS)) {
                String[] callback = callbackData.split(Constant.DELIMITER);
                long productId = Long.parseLong(callback[1]);
                long promoId = Long.parseLong(callback[3]);
                Product product = productService.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
                Promo promo = promoService.findById(promoId).orElseThrow(() -> new RuntimeException("Promo not found"));
                promo.addProduct(product);
                ////////////////////////////////////
//                bot.getSilent().execute(EditMessageReplyMarkup.builder()
//                        .chatId(chatId)
//                        .messageId(update.getCallbackQuery().getMessage().getMessageId())
//                        .replyMarkup(Keyboard.withSelectPromoToProduct(promos, productId))
//                        .build());
            }
        } else {
            switch (update.getMessage().getText()) {
                case (Constant.Button.ADD_PRODUCT) -> context.putState(chatId, BotState.SUGGESTION_TO_INPUT_LINK);
                case (Constant.Button.MY_PRODUCTS) -> context.putState(chatId, BotState.WATCH_PRODUCTS);
                case (Constant.Button.SETTINGS) -> context.putState(chatId, BotState.SETTINGS);
                default -> context.putState(chatId, BotState.UNEXPECTED);
            }
        }
    }

    @Override
    public void reply(Update update) {
        if (update.hasCallbackQuery())
            return;

        Long chatId = getChatId(update);
        List<Product> products = productService.findAllByChatIdAndIsActive(chatId, true);

        if (products.isEmpty()) {
            bot.getSilent().execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(Constant.Message.PRODUCTS_IS_EMPTY)
                    .build());
        } else {
            AtomicInteger num = new AtomicInteger(1);
            products.stream()
                    .sorted(Comparator.comparing(Product::getCreatedAt))
                    .forEach(product -> bot.getSilent().execute(SendMessage.builder()
                            .chatId(chatId)
                            .text(num.getAndIncrement() + ". " +
                                    product.getTitle() + "\n" +
                                    product.getUrl())
                            .replyMarkup(Keyboard.withProductButton(product.getId()))
                            .linkPreviewOptions(LinkPreviewOptions.builder()
                                    .isDisabled(true)
                                    .build())
                            .build()));
        }
    }

    @Override
    public BotState getType() {
        return BotState.WATCH_PRODUCTS;
    }
}