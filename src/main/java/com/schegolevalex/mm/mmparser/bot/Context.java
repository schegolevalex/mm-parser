package com.schegolevalex.mm.mmparser.bot;

import com.schegolevalex.mm.mmparser.bot.page.base.BasePage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Filter;
import com.schegolevalex.mm.mmparser.entity.Promo;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

@Component
public class Context {
    private final List<BasePage> allPages;
    private final Map<Long, Stack<BasePage>> chatPages = new HashMap<>();
    private final Map<Long, Promo> tempPromo = new HashMap<>();
    private final Map<Long, Filter> tempFilter = new HashMap<>();

    public Context(@Lazy List<BasePage> allPages) {
        this.allPages = allPages;
    }

    public void putPage(Long chatId, Page page) {
        Stack<BasePage> pageStack = getPageStack(chatId);
        if (pageStack.peek().getPage() != page)
            pageStack.push(findPage(page));
    }

    public BasePage peekPage(Long chatId) {
        return getPageStack(chatId).peek();
    }

    public BasePage popPage(Long chatId) {
        return getPageStack(chatId).pop();
    }

    public boolean isActiveUser(Long chatId) {
        return chatPages.containsKey(chatId);
    }

    private BasePage findPage(Page page) {
        return allPages.stream()
                .filter(state -> state.getPage().equals(page))
                .findFirst()
                .orElse(null);
    }

    private Stack<BasePage> getPageStack(Long chatId) {
        Stack<BasePage> stack;
        if (!chatPages.containsKey(chatId) || chatPages.get(chatId).isEmpty()) {
            stack = new Stack<>();
            stack.push(findPage(Page.NEW_CONVERSATION));
            chatPages.put(chatId, stack);
        } else
            stack = chatPages.get(chatId);
        return stack;
    }

    public void clearPages(Long chatId) {
        chatPages.remove(chatId);
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

    public void putFilter(Long chatId, Filter filter) {
        tempFilter.put(chatId, filter);
    }

    public Filter getFilter(Long chatId) {
        return tempFilter.get(chatId);
    }

    public void clearFilter(Long chatId) {
        tempFilter.remove(chatId);
    }

    public void clear(Long chatId) {
        clearPages(chatId);
        clearPromo(chatId);
        clearFilter(chatId);
    }
}
