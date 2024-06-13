package com.schegolevalex.mm.mmparser.bot;

import com.schegolevalex.mm.mmparser.bot.state.BaseState;
import com.schegolevalex.mm.mmparser.bot.state.BotState;
import com.schegolevalex.mm.mmparser.entity.Promo;
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
    private final Map<Long, Promo> tempPromo = new HashMap<>();
    private final Map<Long, Long> configurableProductIds = new HashMap<>();

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

    public void clearState(Long chatId) {
        chatStates.remove(chatId);
    }

    public void putPromo(Long chatId, Promo promo) {
        tempPromo.put(chatId, promo);
    }

    public Promo getPromo(Long chatId) {
        return tempPromo.get(chatId);
    }

    public void clearPromo(Long chatId) {
        tempPromo.remove(chatId);
    }

    public void putConfigurableProductId(Long chatId, Long productId) {
        configurableProductIds.put(chatId, productId);
    }

    public Long getConfigurableProductId(Long chatId) {
        return configurableProductIds.get(chatId);
    }

    public void clearConfigurableProductId(Long chatId) {
        configurableProductIds.remove(chatId);
    }
}
