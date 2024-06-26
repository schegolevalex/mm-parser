package com.schegolevalex.mm.mmparser.bot.page.base;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.entity.Promo;
import com.schegolevalex.mm.mmparser.service.ProductService;
import com.schegolevalex.mm.mmparser.service.PromoService;
import org.springframework.context.annotation.Lazy;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.schegolevalex.mm.mmparser.bot.Constant.DELIMITER;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

public abstract class ProductKeyboardPage extends MainKeyboardPage {

    protected final ProductService productService;
    protected final PromoService promoService;

    public ProductKeyboardPage(@Lazy ParserBot bot, ProductService productService, PromoService promoService) {
        super(bot);
        this.productService = productService;
        this.promoService = promoService;
    }

    protected void resolveProductKeyboardAction(Update nextUpdate) {
        Long chatId = getChatId(nextUpdate);

        if (nextUpdate.hasCallbackQuery()) {
            String callback = nextUpdate.getCallbackQuery().getData();
            if (callback.startsWith(Constant.Callback.PRODUCT_NOTIFICATIONS))
                context.putPage(chatId, Page.PRODUCT_NOTIFICATIONS);
            else if (callback.startsWith(Constant.Callback.PRODUCT_SETTINGS))
                context.putPage(chatId, Page.PRODUCT_SETTINGS);
            else if (callback.startsWith(Constant.Callback.PRODUCT_DELETE))
                context.putPage(chatId, Page.CONFIRM_DELETE_PRODUCT);
            else if (callback.startsWith(Constant.Callback.NOTIFICATIONS_SETTINGS))
                context.putPage(chatId, Page.NOTIFICATIONS_SETTINGS);
            else if (callback.startsWith(Constant.Callback.BACK))
                context.putPage(chatId, Page.WATCH_PRODUCTS);
            else if (callback.startsWith(Constant.Callback.BACK_TO_PRODUCT_SETTINGS))
                context.putPage(chatId, Page.PRODUCT_SETTINGS);
            else if (callback.startsWith(Constant.Callback.MY_PRODUCTS) && callback.contains(Constant.Callback.KEYBOARD_PAGES))
                context.putPage(chatId, Page.APPLY_PROMO);
            else if (callback.startsWith(Constant.Callback.APPLY_PROMO) && callback.contains(Constant.Callback.MY_PROMOS)) {
                long productId = Long.parseLong(callback.split(DELIMITER)[1]);
                long promoId = Long.parseLong(callback.split(DELIMITER)[3]);
                Product product = productService.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
                Promo promo = promoService.findById(promoId).orElseThrow(() -> new RuntimeException("Promo not found"));
                product.setPromo(promo);
                context.putPage(chatId, Page.APPLY_PROMO);
            } else if (callback.startsWith(Constant.Callback.APPLY_PROMO))
                context.putPage(chatId, Page.APPLY_PROMO);
            else if (callback.startsWith(Constant.Callback.CONFIRM_DELETE))
                context.putPage(chatId, Page.DELETE_PRODUCT);
            else if (callback.startsWith(Constant.Callback.DECLINE_DELETE))
                context.putPage(chatId, Page.WATCH_PRODUCTS);
        } else
            resolveMainKeyboard(nextUpdate);
    }
}
