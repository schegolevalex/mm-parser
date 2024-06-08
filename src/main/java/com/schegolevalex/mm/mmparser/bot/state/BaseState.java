package com.schegolevalex.mm.mmparser.bot.state;


import com.schegolevalex.mm.mmparser.bot.Context;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import org.springframework.context.annotation.Lazy;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class BaseState {
    protected final ParserBot bot;
    protected final Context context;

    public BaseState(@Lazy ParserBot bot) {
        this.bot = bot;
        this.context = bot.getContext();
    }

    public abstract void route(Update update);

    public abstract void reply(Update update);

    public abstract BotState getType();
}
