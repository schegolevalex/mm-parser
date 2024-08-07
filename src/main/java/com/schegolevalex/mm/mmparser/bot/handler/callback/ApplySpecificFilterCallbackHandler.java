package com.schegolevalex.mm.mmparser.bot.handler.callback;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Context;
import com.schegolevalex.mm.mmparser.bot.handler.BaseHandler;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Filter;
import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.service.FilterService;
import com.schegolevalex.mm.mmparser.service.ProductService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.schegolevalex.mm.mmparser.bot.Constant.DELIMITER;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class ApplySpecificFilterCallbackHandler extends BaseHandler {

    private final ProductService productService;
    private final FilterService filterService;

    public ApplySpecificFilterCallbackHandler(Context context,
                                              ProductService productService,
                                              FilterService filterService) {
        super(context);
        this.productService = productService;
        this.filterService = filterService;
    }

    @Override
    public void handle(Update update) {
        String callback = update.getCallbackQuery().getData();
        long productId = Long.parseLong(callback.split(DELIMITER)[1]);
        long filterId = Long.parseLong(callback.split(DELIMITER)[3]);
        Product product = productService.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        Filter filter = filterService.findById(filterId).orElseThrow(() -> new RuntimeException("Filter not found"));
        if (product.getFilters().contains(filter))
            product.removeFilter(filter);
        else
            product.addFilter(filter);
        context.putPage(getChatId(update), Page.APPLY_FILTER);
    }

    @Override
    public boolean isSuitable(Update update) {
        return update.hasCallbackQuery()
               && update.getCallbackQuery().getData().startsWith(Constant.Callback.APPLY_FILTER)
               && update.getCallbackQuery().getData().contains(Constant.Callback.MY_FILTERS);
    }
}
