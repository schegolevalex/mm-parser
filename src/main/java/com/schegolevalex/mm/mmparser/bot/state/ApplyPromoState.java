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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@Transactional
public class ApplyPromoState extends BaseState {
    private final ProductService productService;
    private final PromoService promoService;

    public ApplyPromoState(@Lazy ParserBot bot, ProductService productService, PromoService promoService) {
        super(bot);
        this.productService = productService;
        this.promoService = promoService;
    }

    @Override
    public void route(Update update) {
        Long chatId = getChatId(update);

        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            if (callbackData.startsWith(Constant.Button.BACK))
                context.putState(chatId, BotState.WATCH_PRODUCTS);
            else if (callbackData.startsWith(Constant.Button.MY_PRODUCTS)) {
                long productId = Long.parseLong(callbackData.split(Constant.DELIMITER)[1]);
                long promoId = Long.parseLong(callbackData.split(Constant.DELIMITER)[3]);
                Product product = productService.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
                Promo promo = promoService.findById(promoId).orElseThrow(() -> new RuntimeException("Promo not found"));
                promo.addProduct(product);
            }
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

        long productId = Long.parseLong(update.getCallbackQuery().getData().split(Constant.DELIMITER)[1]);

        Product product = productService.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        List<Promo> promos = promoService.findAllByChatId(chatId);

        int page = 1;
        if (update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith(Constant.Button.PAGE))
            page = Integer.parseInt(update.getCallbackQuery().getData().split(Constant.DELIMITER)[1]);


        if (promos.isEmpty()) {
            // todo
        } else {
            bot.getSilent().execute(EditMessageReplyMarkup.builder()
                    .chatId(chatId)
                    .messageId(update.getCallbackQuery().getMessage().getMessageId())
                    .replyMarkup(Keyboard.withPromosForProduct(promos, productId, product.getPromo(), page))
                    .build());
        }
    }

    @Override
    public BotState getType() {
        return BotState.APPLY_PROMO;
    }
}
