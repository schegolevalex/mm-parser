package com.schegolevalex.mm.mmparser.bot.page.base;

import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.entity.Filter;
import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.entity.Promo;
import com.schegolevalex.mm.mmparser.service.FilterService;
import com.schegolevalex.mm.mmparser.service.ProductService;
import com.schegolevalex.mm.mmparser.service.PromoService;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Lazy;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

import static com.schegolevalex.mm.mmparser.bot.Constant.Callback;
import static com.schegolevalex.mm.mmparser.bot.Constant.DELIMITER;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

public abstract class ProductKeyboardPage extends MainKeyboardPage {

    protected final ProductService productService;
    protected final PromoService promoService;
    protected final FilterService filterService;

    public ProductKeyboardPage(@Lazy ParserBot bot,
                               ProductService productService,
                               PromoService promoService,
                               FilterService filterService) {
        super(bot);
        this.productService = productService;
        this.promoService = promoService;
        this.filterService = filterService;
    }

    protected void resolveProductKeyboardAction(Update nextUpdate) {
        Long chatId = getChatId(nextUpdate);

        if (nextUpdate.hasCallbackQuery()) {
            String callback = nextUpdate.getCallbackQuery().getData();
            if (callback.startsWith(Callback.PRODUCT_SETTINGS))
                context.putPage(chatId, Page.PRODUCT_SETTINGS);
            else if (callback.startsWith(Callback.DELETE_PRODUCT))
                context.putPage(chatId, Page.CONFIRM_DELETE_PRODUCT);
            else if (callback.startsWith(Callback.APPLY_FILTER) && callback.contains(Callback.MY_FILTERS)) {
                long productId = Long.parseLong(callback.split(DELIMITER)[1]);
                long filterId = Long.parseLong(callback.split(DELIMITER)[3]);
                Product product = productService.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
                Filter filter = filterService.findById(filterId).orElseThrow(() -> new RuntimeException("Filter not found"));
                if (product.getFilters().contains(filter))
                    product.removeFilter(filter);
                else
                    product.addFilter(filter);
                context.putPage(chatId, Page.APPLY_FILTER);
            } else if (callback.startsWith(Callback.APPLY_FILTER))
                context.putPage(chatId, Page.APPLY_FILTER);
            else if (callback.startsWith(Callback.BACK))
                context.putPage(chatId, Page.WATCH_PRODUCTS);
            else if (callback.startsWith(Callback.BACK_TO_PRODUCT_SETTINGS))
                context.putPage(chatId, Page.PRODUCT_SETTINGS);
            else if (callback.startsWith(Callback.MY_PRODUCTS) && callback.contains(Callback.KEYBOARD_PAGES))
                context.putPage(chatId, Page.APPLY_PROMO);
            else if (callback.startsWith(Callback.APPLY_PROMO) && callback.contains(Callback.MY_PROMOS)) {
                long productId = Long.parseLong(callback.split(DELIMITER)[1]);
                long promoId = Long.parseLong(callback.split(DELIMITER)[3]);
                Product product = productService.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
                Promo promo = promoService.findById(promoId).orElseThrow(() -> new RuntimeException("Promo not found"));
                if (product.getPromo() == promo)
                    product.setPromo(null);
                else
                    product.setPromo(promo);
                context.putPage(chatId, Page.APPLY_PROMO);
            } else if (callback.startsWith(Callback.APPLY_PROMO))
                context.putPage(chatId, Page.APPLY_PROMO);
            else if (callback.startsWith(Callback.CONFIRM_DELETE))
                context.putPage(chatId, Page.DELETE_PRODUCT);
            else if (callback.startsWith(Callback.DECLINE_DELETE))
                context.putPage(chatId, Page.WATCH_PRODUCTS);
        } else
            resolveMainKeyboard(nextUpdate);
    }

    protected static @NotNull Map<String, String> extractCallbacksMap(Update prevUpdate) {
        String[] tokens = prevUpdate.getCallbackQuery().getData().split(DELIMITER);
        Map<String, String> callbacks = new HashMap<>();
        for (int i = 0; i < tokens.length; i += 2)
            callbacks.put(tokens[i], tokens[i + 1]);
        return callbacks;
    }
}
