package com.schegolevalex.mm.mmparser.bot.handler.button;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Context;
import com.schegolevalex.mm.mmparser.bot.handler.BaseHandler;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Filter;
import com.schegolevalex.mm.mmparser.entity.FilterField;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class BonusButtonHandler extends BaseHandler {

    public BonusButtonHandler(Context context) {
        super(context);
    }

    @Override
    public void handle(Update update) {
        Long chatId = getChatId(update);
        Filter filter = context.getFilter(chatId);
        filter.setField(FilterField.BONUS);
        context.putPage(chatId, Page.ADD_FILTER_OPERATION);
    }

    @Override
    public boolean isSuitable(Update update) {
        return update.hasMessage()
               && update.getMessage().hasText()
               && update.getMessage().getText().equals(Constant.Button.BONUS);
    }
}
