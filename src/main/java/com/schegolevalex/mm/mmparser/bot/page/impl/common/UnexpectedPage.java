package com.schegolevalex.mm.mmparser.bot.page.impl.common;

import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.page.base.BasePage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class UnexpectedPage extends BasePage {
    public UnexpectedPage(ParserBot bot) {
        super(bot);
    }

    @Override
    public void show(Update prevUpdate) {
        if (!prevUpdate.hasCallbackQuery()) {
            bot.getSilent().execute(DeleteMessage.builder()
                    .chatId(getChatId(prevUpdate))
                    .messageId(prevUpdate.getMessage().getMessageId())
                    .build());
        }
        this.afterUpdateReceived(prevUpdate);
    }

    @Override
    public void afterUpdateReceived(Update nextUpdate) {
        context.popPage(getChatId(nextUpdate));
    }

    @Override
    public Page getPage() {
        return Page.UNEXPECTED;
    }
}
