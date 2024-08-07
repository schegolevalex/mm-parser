package com.schegolevalex.mm.mmparser.bot.handler.callback;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Context;
import com.schegolevalex.mm.mmparser.bot.handler.BaseHandler;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class ApplyFilterCallbackHandler extends BaseHandler {

    public ApplyFilterCallbackHandler(Context context) {
        super(context);
    }

    @Override
    public void handle(Update update) {
        context.putPage(getChatId(update), Page.APPLY_FILTER);
    }

    @Override
    public boolean isSuitable(Update update) {
        return update.hasCallbackQuery()
               && update.getCallbackQuery().getData().startsWith(Constant.Callback.APPLY_FILTER)
               && !update.getCallbackQuery().getData().contains(Constant.Callback.MY_FILTERS);
    }
}
