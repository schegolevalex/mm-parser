package com.schegolevalex.mm.mmparser.bot.page.base;

import com.schegolevalex.mm.mmparser.bot.ParserBot;
import org.springframework.context.annotation.Lazy;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.schegolevalex.mm.mmparser.bot.Constant.Button;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

public abstract class MainKeyboardPage extends BasePage {

    public MainKeyboardPage(@Lazy ParserBot bot) {
        super(bot);
    }

    protected void resolveMainKeyboard(Update nextUpdate) {
        Long chatId = getChatId(nextUpdate);
        switch (nextUpdate.getMessage().getText()) {
            case (Button.ADD_PRODUCT) -> context.putPage(chatId, Page.ADD_PRODUCT);
            case (Button.MY_PRODUCTS) -> context.putPage(chatId, Page.WATCH_PRODUCTS);
            case (Button.SETTINGS) -> context.putPage(chatId, Page.COMMON_SETTINGS);
            default -> context.putPage(chatId, Page.UNEXPECTED);
        }
    }
}
