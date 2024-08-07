package com.schegolevalex.mm.mmparser.bot.handler.action;

import com.schegolevalex.mm.mmparser.bot.Context;
import com.schegolevalex.mm.mmparser.bot.handler.BaseHandler;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class MessageWithUrlHandler extends BaseHandler {
    private static final String MESSAGE_WITH_URL_REGEXP = ".*(http(s)?://)?(www\\.)?.+\\.[-a-zA-Z]+.*";

    public MessageWithUrlHandler(Context context) {
        super(context);
    }

    @Override
    public void handle(Update update) {
        context.putPage(getChatId(update), Page.ADD_PRODUCT);
    }

    @Override
    public boolean isSuitable(Update update) {
        return update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().matches(MESSAGE_WITH_URL_REGEXP);
    }
}
