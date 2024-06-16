package com.schegolevalex.mm.mmparser.bot.page.impl;

import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.page.base.BasePage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class UnexpectedPage extends BasePage {
    public UnexpectedPage(@Lazy ParserBot bot) {
        super(bot);
    }

    @Override
    public void beforeUpdateReceive(Update prevUpdate) {
        if (!prevUpdate.hasCallbackQuery()) {
            bot.getSilent().execute(DeleteMessage.builder()
                    .chatId(getChatId(prevUpdate))
                    .messageId(prevUpdate.getMessage().getMessageId())
                    .build());
        }
        this.afterUpdateReceive(prevUpdate);
    }

    @Override
    public void afterUpdateReceive(Update nextUpdate) {
        context.popPage(getChatId(nextUpdate));
    }

    @Override
    public Page getPage() {
        return Page.UNEXPECTED;
    }
}
