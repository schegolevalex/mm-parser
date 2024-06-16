package com.schegolevalex.mm.mmparser.bot.state;

import com.schegolevalex.mm.mmparser.bot.ParserBot;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class NotificationSettingsState extends BaseState {
    public NotificationSettingsState(@Lazy ParserBot bot) {
        super(bot);
    }

    @Override
    public void route(Update update) {

    }

    @Override
    public void reply(Update update) {

    }

    @Override
    public BotState getType() {
        return BotState.NOTIFICATIONS_SETTINGS;
    }
}