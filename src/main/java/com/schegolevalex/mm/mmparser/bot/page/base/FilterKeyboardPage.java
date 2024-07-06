package com.schegolevalex.mm.mmparser.bot.page.base;

import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.entity.Filter;
import org.springframework.context.annotation.Lazy;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.schegolevalex.mm.mmparser.bot.Constant.Button;
import static com.schegolevalex.mm.mmparser.bot.Constant.Callback;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

public abstract class FilterKeyboardPage extends BasePage {
    public FilterKeyboardPage(@Lazy ParserBot bot) {
        super(bot);
    }

    protected void resolveFiltersKeyboard(Update nextUpdate) {
        Long chatId = getChatId(nextUpdate);
        if (nextUpdate.hasCallbackQuery()) {
            String callback = nextUpdate.getCallbackQuery().getData();
            if (callback.startsWith(Callback.DELETE_FILTER))
                context.putPage(chatId, Page.CONFIRM_DELETE_FILTER);
            else if (callback.startsWith(Callback.CONFIRM_DELETE))
                context.putPage(chatId, Page.DELETE_FILTER);
            else if (callback.startsWith(Callback.DECLINE_DELETE))
                context.putPage(chatId, Page.WATCH_FILTERS);
        } else {
            switch (nextUpdate.getMessage().getText()) {
                case (Button.ADD_FILTER) -> {
                    context.putFilter(chatId, new Filter());
                    context.putPage(chatId, Page.ADD_FILTER_FIELD);
                }
                case (Button.MY_FILTERS) -> context.putPage(chatId, Page.WATCH_FILTERS);
                case (Button.BACK) -> context.putPage(chatId, Page.COMMON_SETTINGS);
                default -> context.putPage(chatId, Page.UNEXPECTED);
            }
        }
    }
}
