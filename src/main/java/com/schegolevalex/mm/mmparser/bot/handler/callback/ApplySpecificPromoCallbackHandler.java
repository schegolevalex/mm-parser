package com.schegolevalex.mm.mmparser.bot.handler.callback;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Context;
import com.schegolevalex.mm.mmparser.bot.handler.BaseHandler;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.entity.Promo;
import com.schegolevalex.mm.mmparser.service.ProductService;
import com.schegolevalex.mm.mmparser.service.PromoService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.schegolevalex.mm.mmparser.bot.Constant.DELIMITER;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class ApplySpecificPromoCallbackHandler extends BaseHandler {

    private final ProductService productService;
    private final PromoService promoService;

    public ApplySpecificPromoCallbackHandler(Context context,
                                             ProductService productService,
                                             PromoService promoService) {
        super(context);
        this.productService = productService;
        this.promoService = promoService;
    }

    @Override
    @Transactional
    public void handle(Update update) {
        String callback = update.getCallbackQuery().getData();
        long productId = Long.parseLong(callback.split(DELIMITER)[1]);
        long promoId = Long.parseLong(callback.split(DELIMITER)[3]);
        Product product = productService.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        Promo promo = promoService.findById(promoId).orElseThrow(() -> new RuntimeException("Promo not found"));
        if (product.getPromo() == promo)
            product.setPromo(null);
        else
            product.setPromo(promo);
        productService.save(product);
        context.putPage(getChatId(update), Page.APPLY_PROMO);
    }

    @Override
    public boolean isSuitable(Update update) {
        return update.hasCallbackQuery()
               && update.getCallbackQuery().getData().startsWith(Constant.Callback.APPLY_PROMO)
               && update.getCallbackQuery().getData().contains(Constant.Callback.MY_PROMOS);
    }
}
