package com.schegolevalex.mm.mmparser.bot;

import com.schegolevalex.mm.mmparser.bot.state.BaseState;
import com.schegolevalex.mm.mmparser.bot.state.BotState;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

@Component
public class Context {
    private final List<BaseState> possibleStates;
    private final Map<Long, Stack<BaseState>> chatStates = new HashMap<>();

    public Context(@Lazy List<BaseState> possibleStates) {
        this.possibleStates = possibleStates;
    }

    public void putState(Long chatId, BotState botState) {
        Stack<BaseState> stack = getStateStack(chatId);
        if (stack.peek().getType() != botState)
            stack.push(findState(botState));
    }

    public BaseState peekState(Long chatId) {
        return getStateStack(chatId).peek();
    }

    public BaseState popState(Long chatId) {
        return getStateStack(chatId).pop();
    }

    public boolean isActiveUser(Long chatId) {
        return chatStates.containsKey(chatId);
    }

    private BaseState findState(BotState botState) {
        return possibleStates
                .stream()
                .filter(state -> state.getType().equals(botState))
                .findFirst()
                .orElse(null);
    }

    private Stack<BaseState> getStateStack(Long chatId) {
        Stack<BaseState> stack;
        if (!chatStates.containsKey(chatId)) {
            stack = new Stack<>();
            stack.push(findState(BotState.NEW_STATE));
            chatStates.put(chatId, stack);
        } else
            stack = chatStates.get(chatId);
        return stack;
    }
}
