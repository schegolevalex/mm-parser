package com.schegolevalex.mm.mmparser.bot;

import com.schegolevalex.mm.mmparser.bot.state.AbstractState;
import com.schegolevalex.mm.mmparser.bot.state.BotState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

@Component
@RequiredArgsConstructor
public class Context {
    private final List<AbstractState> possibleStates;
    private final Map<Long, Stack<AbstractState>> chatStates = new HashMap<>();

    public boolean putState(Long chatId, BotState botState) {
        return chatStates.computeIfAbsent(chatId, k -> new Stack<>()).add(findState(botState));
    }

    public AbstractState peekState(Long chatId) {
        return chatStates.get(chatId).peek();
    }

    public AbstractState popState(Long chatId) {
        return chatStates.get(chatId).pop();
    }

    public boolean isActiveUser(Long chatId) {
        return chatStates.containsKey(chatId);
    }

    private AbstractState findState(BotState botState) {
        return possibleStates
                .stream()
                .filter(state -> state.getType().equals(botState))
                .findFirst()
                .orElse(null);
    }
}
