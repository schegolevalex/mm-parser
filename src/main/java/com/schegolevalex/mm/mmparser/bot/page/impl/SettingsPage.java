package com.schegolevalex.mm.mmparser.bot.page.impl;

import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.page.base.BasePage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.schegolevalex.mm.mmparser.bot.Constant.Button;
import static com.schegolevalex.mm.mmparser.bot.Constant.Message;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class SettingsPage extends BasePage {
    public SettingsPage(@Lazy ParserBot bot) {
        super(bot);
    }

    @Override
    public void beforeUpdateReceive(Update prevUpdate) {
        bot.getSilent().execute(SendMessage.builder()
                .chatId(getChatId(prevUpdate))
                .text(Message.CHOOSE_SETTINGS)
                .replyMarkup(Keyboard.withSettings())
                .build());
    }

    @Override
    public void afterUpdateReceive(Update nextUpdate) {
        Long chatId = getChatId(nextUpdate);
        switch (nextUpdate.getMessage().getText()) {
            case (Button.PROMOS_SETTINGS) -> context.putPage(chatId, Page.PROMOS_SETTINGS);
            case (Button.CASHBACK_SETTINGS) -> context.putPage(chatId, Page.CASHBACK_SETTINGS);
            case (Button.BACK) -> context.putPage(chatId, Page.MAIN);
            default -> context.putPage(chatId, Page.UNEXPECTED);
        }
    }

    @Override
    public Page getPage() {
        return Page.COMMON_SETTINGS;
    }
}
