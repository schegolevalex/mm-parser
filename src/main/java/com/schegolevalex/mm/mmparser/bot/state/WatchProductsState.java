package com.schegolevalex.mm.mmparser.bot.state;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.service.ProductService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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

    public WatchProductsState(@Lazy ParserBot bot, ProductService productService) {
        super(bot);
        this.productService = productService;
    }

    @Override
    public void route(Update update) {
        Long chatId = getChatId(update);

        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            if (callbackData.startsWith(Constant.Button.PRODUCT_NOTIFICATIONS))
                context.putState(chatId, BotState.PRODUCT_NOTIFICATIONS);
            else if (callbackData.startsWith(Constant.Button.PRODUCT_SETTINGS))
                context.putState(chatId, BotState.PRODUCT_SETTINGS);
            else if (callbackData.startsWith(Constant.Button.PRODUCT_DELETE))
                context.putState(chatId, BotState.DELETE_PRODUCT);
            else if (callbackData.startsWith(Constant.Button.BACK))
                context.putState(chatId, BotState.WATCH_PRODUCTS);
            else
                context.putState(chatId, BotState.UNEXPECTED);
        } else
            switch (update.getMessage().getText()) {
                case (Constant.Button.ADD_PRODUCT) -> context.putState(chatId, BotState.SUGGESTION_TO_INPUT_LINK);
                case (Constant.Button.MY_PRODUCTS) -> context.putState(chatId, BotState.WATCH_PRODUCTS);
                case (Constant.Button.SETTINGS) -> context.putState(chatId, BotState.SETTINGS);
                default -> context.putState(chatId, BotState.UNEXPECTED);
            }
    }

    @Override
    public void reply(Update update) {
        Long chatId = getChatId(update);

        if (update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith(Constant.Button.BACK)) {
            long productId = Long.parseLong(update.getCallbackQuery().getData().split(Constant.DELIMITER)[1]);
            Product product = productService.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

            bot.getSilent().execute(EditMessageReplyMarkup.builder()
                    .chatId(chatId)
                    .messageId(update.getCallbackQuery().getMessage().getMessageId())
                    .replyMarkup(Keyboard.withProduct(product.getId(), product.getUrl()))
                    .build());
        } else {
            List<Product> products = productService.findAllByChatIdAndIsActive(chatId, true);

            if (products.isEmpty()) {
                bot.getSilent().execute(SendMessage.builder()
                        .chatId(chatId)
                        .text(Constant.Message.PRODUCTS_IS_EMPTY)
                        .replyMarkup(Keyboard.withMainPageActions())
                        .build());
            } else {
                AtomicInteger num = new AtomicInteger(1);
                products.stream()
                        .sorted(Comparator.comparing(Product::getCreatedAt))
                        .forEach(product -> bot.getSilent().execute(SendMessage.builder()
                                .chatId(chatId)
                                .text(num.getAndIncrement() + ". " + product.getTitle())
                                .replyMarkup(Keyboard.withProduct(product.getId(), product.getUrl()))
                                .linkPreviewOptions(LinkPreviewOptions.builder()
                                        .isDisabled(true)
                                        .build())
                                .build()));
            }
        }
    }

    @Override
    public BotState getType() {
        return BotState.WATCH_PRODUCTS;
    }
}