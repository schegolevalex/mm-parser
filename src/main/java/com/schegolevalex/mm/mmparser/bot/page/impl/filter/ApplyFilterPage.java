package com.schegolevalex.mm.mmparser.bot.page.impl.filter;

import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.bot.page.base.ProductKeyboardPage;
import com.schegolevalex.mm.mmparser.entity.Filter;
import com.schegolevalex.mm.mmparser.service.FilterService;
import com.schegolevalex.mm.mmparser.service.ProductService;
import com.schegolevalex.mm.mmparser.service.PromoService;
import org.springframework.context.annotation.Lazy;
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
public class ApplyFilterPage extends ProductKeyboardPage {

    public ApplyFilterPage(@Lazy ParserBot bot,
                           ProductService productService,
                           PromoService promoService,
                           FilterService filterService) {
        super(bot, productService, promoService, filterService);
    }

    @Override
    public void beforeUpdateReceive(Update prevUpdate) {
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
    public void afterUpdateReceive(Update nextUpdate) {
        resolveProductKeyboardAction(nextUpdate);
    }

    @Override
    public Page getPage() {
        return Page.APPLY_FILTER;
    }
}