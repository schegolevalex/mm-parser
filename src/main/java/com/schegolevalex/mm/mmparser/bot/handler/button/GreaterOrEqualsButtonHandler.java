package com.schegolevalex.mm.mmparser.bot.handler.button;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Context;
import com.schegolevalex.mm.mmparser.bot.handler.BaseHandler;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Filter;
import com.schegolevalex.mm.mmparser.entity.Operation;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class GreaterOrEqualsButtonHandler extends BaseHandler {
    public GreaterOrEqualsButtonHandler(Context context) {
        super(context);
    }

    @Override
    public void handle(Update update) {
        Long chatId = getChatId(update);
        Filter filter = context.getFilter(chatId);
        filter.setOperation(Operation.GREATER_OR_EQUALS);
        context.putPage(chatId, Page.ADD_FILTER_VALUE);
    }

    @Override
    public boolean isSuitable(Update update) {
        return update.hasMessage()
               && update.getMessage().hasText()
               && update.getMessage().getText().equals(Constant.Button.GREATER_OR_EQUALS);
    }
}
