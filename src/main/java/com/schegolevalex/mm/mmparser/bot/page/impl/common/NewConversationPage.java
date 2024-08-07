package com.schegolevalex.mm.mmparser.bot.page.impl.common;

import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.page.base.BasePage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class NewConversationPage extends BasePage {

    public NewConversationPage(ParserBot bot) {
        super(bot);
    }

    @Override
    public void show(Update prevUpdate) {
    }

    @Override
    public void afterUpdateReceived(Update nextUpdate) {
        context.putPage(AbilityUtils.getChatId(nextUpdate), Page.MAIN);
    }

    @Override
    public Page getPage() {
        return Page.NEW_CONVERSATION;
    }
}
