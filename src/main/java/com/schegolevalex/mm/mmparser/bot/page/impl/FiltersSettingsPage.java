package com.schegolevalex.mm.mmparser.bot.page.impl;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.page.base.BasePage;
import com.schegolevalex.mm.mmparser.bot.page.base.FiltersKeyboardPage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class FiltersSettingsPage extends FiltersKeyboardPage {

    public FiltersSettingsPage(@Lazy ParserBot bot) {
        super(bot);
    }

    @Override
    public void beforeUpdateReceive(Update prevUpdate) {
        bot.getSilent().execute(SendMessage.builder()
                .chatId(getChatId(prevUpdate))
                .text(Constant.Message.CHOOSE_ACTION)
                .replyMarkup(Keyboard.withFiltersSettingsActions())
                .build());
    }

    @Override
    public void afterUpdateReceive(Update nextUpdate) {
        resolveFiltersKeyboard(nextUpdate);
    }

    @Override
    public Page getPage() {
        return Page.FILTERS_SETTINGS;
    }
}
