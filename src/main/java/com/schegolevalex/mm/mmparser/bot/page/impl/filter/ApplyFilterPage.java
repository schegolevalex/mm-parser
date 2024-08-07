package com.schegolevalex.mm.mmparser.bot.page.impl.filter;

import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.handler.Handler;
import com.schegolevalex.mm.mmparser.bot.page.base.HandlersPage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Filter;
import com.schegolevalex.mm.mmparser.service.FilterService;
import com.schegolevalex.mm.mmparser.service.ProductService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.schegolevalex.mm.mmparser.bot.Constant.Callback;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@Transactional
public class ApplyFilterPage extends HandlersPage {

    private final ProductService productService;
    private final FilterService filterService;

    public ApplyFilterPage(ParserBot bot,
                           List<Handler> handlers,
                           ProductService productService,
                           FilterService filterService) {
        super(bot, handlers);
        this.productService = productService;
        this.filterService = filterService;
    }

    @Override
    public void show(Update prevUpdate) {
        Long chatId = getChatId(prevUpdate);
        Map<String, String> callbacks = extractCallbacksMap(prevUpdate);
        long productId = Long.parseLong(callbacks.get(Callback.APPLY_FILTER));

        Set<Filter> selectedFilters = productService.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"))
                .getFilters();
        List<Filter> allUserFilters = filterService.findAllByChatId(chatId);

        int page = 1;
        if (callbacks.containsKey(Callback.KEYBOARD_PAGES))
            page = Integer.parseInt(callbacks.get(Callback.KEYBOARD_PAGES));

        bot.getSilent().execute(EditMessageReplyMarkup.builder()
                .chatId(chatId)
                .messageId(prevUpdate.getCallbackQuery().getMessage().getMessageId())
                .replyMarkup(Keyboard.withFiltersForProduct(allUserFilters, selectedFilters, productId, page))
                .build());
    }

    @Override
    public Page getPage() {
        return Page.APPLY_FILTER;
    }
}