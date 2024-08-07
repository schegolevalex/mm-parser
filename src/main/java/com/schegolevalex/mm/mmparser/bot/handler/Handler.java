package com.schegolevalex.mm.mmparser.bot.handler;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Handler {
    void handle(Update update);

    boolean isSuitable(Update update);
}
