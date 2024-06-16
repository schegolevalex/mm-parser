package com.schegolevalex.mm.mmparser.bot;

import com.schegolevalex.mm.mmparser.bot.page.base.BasePage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Promo;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

@Component
public class Context {
    private final List<BasePage> allPossiblePages;
    private final Map<Long, Stack<BasePage>> chatPages = new HashMap<>();
    private final Map<Long, Promo> tempPromo = new HashMap<>();
    private final Map<Long, Long> configurableProductIds = new HashMap<>();

    public Context(@Lazy List<BasePage> allPossiblePages) {
        this.allPossiblePages = allPossiblePages;
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
        return allPossiblePages
                .stream()
                .filter(state -> state.getPage().equals(page))
                .findFirst()
                .orElse(null);
    }

    private Stack<BasePage> getPageStack(Long chatId) {
        Stack<BasePage> stack;
        if (!chatPages.containsKey(chatId)) {
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
