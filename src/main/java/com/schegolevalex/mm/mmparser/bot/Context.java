package com.schegolevalex.mm.mmparser.bot;

import com.schegolevalex.mm.mmparser.bot.state.AbstractState;
import com.schegolevalex.mm.mmparser.bot.state.BotState;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

@Component
public class Context {
    private final List<AbstractState> possibleStates;
    private final Map<Long, Stack<AbstractState>> chatStates = new HashMap<>();

    public Context(@Lazy List<AbstractState> possibleStates) {
        this.possibleStates = possibleStates;
    }

    public void putState(Long chatId, BotState botState) {
        Stack<AbstractState> stack = getStateStack(chatId);
        if (stack.peek().getType() != botState)
            stack.push(findState(botState));
    }

    public AbstractState peekState(Long chatId) {
        return getStateStack(chatId).peek();
    }

    public AbstractState popState(Long chatId) {
        return getStateStack(chatId).pop();
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

    private Stack<AbstractState> getStateStack(Long chatId) {
        Stack<AbstractState> stack;
        if (!chatStates.containsKey(chatId)) {
            stack = new Stack<>();
            stack.push(findState(BotState.NEW_STATE));
            chatStates.put(chatId, stack);
        } else
            stack = chatStates.get(chatId);
        return stack;
    }
}
