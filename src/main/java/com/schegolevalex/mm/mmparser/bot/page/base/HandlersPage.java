package com.schegolevalex.mm.mmparser.bot.page.base;

import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.handler.Handler;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.schegolevalex.mm.mmparser.bot.Constant.DELIMITER;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public abstract class HandlersPage extends BasePage {
    private final List<Handler> handlers;

    @Autowired
    public HandlersPage(ParserBot bot, List<Handler> handlers) {
        super(bot);
        this.handlers = handlers;
    }

    @Override
    public void afterUpdateReceived(Update nextUpdate) {
        handlers.stream()
                .filter(button -> button.isSuitable(nextUpdate))
                .findFirst()
                .ifPresentOrElse(button -> button.handle(nextUpdate), () -> context.putPage(getChatId(nextUpdate), Page.UNEXPECTED));
    }

    protected static @NotNull Map<String, String> extractCallbacksMap(Update update) {
        String[] tokens = update.getCallbackQuery().getData().split(DELIMITER);
        Map<String, String> callbacks = new HashMap<>();
        for (int i = 0; i < tokens.length; i += 2)
            callbacks.put(tokens[i], tokens[i + 1]);
        return callbacks;
    }
}