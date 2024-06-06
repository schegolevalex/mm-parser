package com.schegolevalex.mm.mmparser.bot.state;


import com.schegolevalex.mm.mmparser.bot.ParserBot;
import org.springframework.context.annotation.Lazy;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class AbstractState {
    protected final ParserBot bot;

    public AbstractState(@Lazy ParserBot bot) {
        this.bot = bot;
    }

    public abstract void reply(Update update);

    public abstract BotState getType();
}
