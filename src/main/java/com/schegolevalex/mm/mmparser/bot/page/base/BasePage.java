package com.schegolevalex.mm.mmparser.bot.page.base;


import com.schegolevalex.mm.mmparser.bot.Context;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class BasePage {
    protected final ParserBot bot;
    protected final Context context;

    public BasePage(ParserBot bot) {
        this.bot = bot;
        this.context = bot.getContext();
    }

    public abstract void show(Update prevUpdate);

    public abstract void afterUpdateReceived(Update nextUpdate);

    public abstract Page getPage();
}
