package com.schegolevalex.mm.mmparser.bot.page.impl;

import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.page.base.MainKeyboardPage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.entity.Promo;
import com.schegolevalex.mm.mmparser.service.ProductService;
import com.schegolevalex.mm.mmparser.service.PromoService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.schegolevalex.mm.mmparser.bot.Constant.Callback;
import static com.schegolevalex.mm.mmparser.bot.Constant.DELIMITER;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class ProductSettingsPage extends MainKeyboardPage {

    private final ProductService productService;
    private final PromoService promoService;

    public ProductSettingsPage(@Lazy ParserBot bot, ProductService productService, PromoService promoService) {
        super(bot);
        this.productService = productService;
        this.promoService = promoService;
    }

    @Override
    public void beforeUpdateReceive(Update prevUpdate) {
        bot.getSilent().execute(EditMessageReplyMarkup.builder()
                .chatId(getChatId(prevUpdate))
                .messageId(prevUpdate.getCallbackQuery().getMessage().getMessageId())
                .replyMarkup(Keyboard.withProductSettings(Long.parseLong(prevUpdate.getCallbackQuery().getData().split(DELIMITER)[1])))
                .build());
    }

    @Override
    public void afterUpdateReceive(Update nextUpdate) {
        Long chatId = getChatId(nextUpdate);

        if (nextUpdate.hasCallbackQuery()) {
            String callbackData = nextUpdate.getCallbackQuery().getData();
            if (callbackData.startsWith(Callback.PRODUCT_NOTIFICATIONS))
                context.putPage(chatId, Page.PRODUCT_NOTIFICATIONS);
            else if (callbackData.startsWith(Callback.PRODUCT_SETTINGS))
                context.putPage(chatId, Page.PRODUCT_SETTINGS);
            else if (callbackData.startsWith(Callback.PRODUCT_DELETE))
                context.putPage(chatId, Page.DELETE_PRODUCT);
            else if (callbackData.startsWith(Callback.NOTIFICATIONS_SETTINGS))
                context.putPage(chatId, Page.NOTIFICATIONS_SETTINGS);
            else if (callbackData.startsWith(Callback.BACK))
                context.putPage(chatId, Page.WATCH_PRODUCTS);
            else if (callbackData.startsWith(Callback.BACK_TO_PRODUCT_SETTINGS))
                context.putPage(chatId, Page.PRODUCT_SETTINGS);
            else if (callbackData.startsWith(Callback.MY_PRODUCTS) && callbackData.contains(Callback.KEYBOARD_PAGES))
                context.putPage(chatId, Page.APPLY_PROMO);
            else if (callbackData.startsWith(Callback.APPLY_PROMO) && callbackData.contains(Callback.MY_PROMOS)) {
                long productId = Long.parseLong(callbackData.split(DELIMITER)[1]);
                long promoId = Long.parseLong(callbackData.split(DELIMITER)[3]);
                Product product = productService.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
                Promo promo = promoService.findById(promoId).orElseThrow(() -> new RuntimeException("Promo not found"));
                promo.addProduct(product);
            } else if (callbackData.startsWith(Callback.APPLY_PROMO))
                context.putPage(chatId, Page.APPLY_PROMO);
        } else
            resolveMainKeyboard(nextUpdate);
    }

    @Override
    public Page getPage() {
        return Page.PRODUCT_SETTINGS;
    }
}
