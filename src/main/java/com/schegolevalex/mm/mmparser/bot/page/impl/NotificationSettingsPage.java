package com.schegolevalex.mm.mmparser.bot.page.impl;

import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.page.base.BasePage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class NotificationSettingsPage extends BasePage {
    public NotificationSettingsPage(@Lazy ParserBot bot) {
        super(bot);
    }

    @Override
    public void beforeUpdateReceive(Update prevUpdate) {

    }

    @Override
    public void afterUpdateReceive(Update nextUpdate) {

    }

    @Override
    public Page getPage() {
        return Page.NOTIFICATIONS_SETTINGS;
    }
}