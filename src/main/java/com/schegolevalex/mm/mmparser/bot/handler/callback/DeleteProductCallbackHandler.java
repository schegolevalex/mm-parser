package com.schegolevalex.mm.mmparser.bot.handler.callback;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Context;
import com.schegolevalex.mm.mmparser.bot.handler.BaseHandler;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class DeleteProductCallbackHandler extends BaseHandler {

    public DeleteProductCallbackHandler(Context context) {
        super(context);
    }

    @Override
    public void handle(Update update) {
        context.putPage(getChatId(update), Page.CONFIRM_DELETE_PRODUCT);
    }

    @Override
    public boolean isSuitable(Update update) {
        return update.hasCallbackQuery()
               && update.getCallbackQuery().getData().startsWith(Constant.Callback.DELETE_PRODUCT);
    }
}
