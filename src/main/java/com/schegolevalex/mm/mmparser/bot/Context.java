package com.schegolevalex.mm.mmparser.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

@Component
@RequiredArgsConstructor
public class Context {
    private final Map<Long, Stack<UserState>> chatStates = new HashMap<>();

    public boolean putState(Long chatId, UserState userState) {
        return chatStates.computeIfAbsent(chatId, k -> new Stack<>()).add(userState);
    }

    public UserState peekState(Long chatId) {
        return chatStates.get(chatId).peek();
    }

    public UserState popState(Long chatId) {
        return chatStates.get(chatId).pop();
    }

    public boolean userIsActive(Long chatId) {
        return chatStates.containsKey(chatId);
    }
}
