package com.schegolevalex.mm.mmparser.bot.page.impl.product;

import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.page.base.BasePage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.schegolevalex.mm.mmparser.bot.Constant.Button;
import static com.schegolevalex.mm.mmparser.bot.Constant.Message;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class SuggestionToInputLinkPage extends BasePage {
    private static final String MESSAGE_WITH_URL_REGEXP = ".*(http(s)?://)?(www\\.)?megamarket\\.ru\\b([-a-zA-Z0-9@:%_+.~#?&/=]*)";

    public SuggestionToInputLinkPage(@Lazy ParserBot bot) {
        super(bot);
    }

    @Override
    public void beforeUpdateReceive(Update prevUpdate) {
        bot.getSilent().execute(SendMessage.builder()
                .chatId(getChatId(prevUpdate))
                .text(Message.SUGGESTION_TO_LINK_INPUT)
                .replyMarkup(Keyboard.withBackButton())
                .build());
    }

    @Override
    public void afterUpdateReceive(Update nextUpdate) {
        Long chatId = AbilityUtils.getChatId(nextUpdate);
        String userText = nextUpdate.getMessage().getText();

        if (userText.equalsIgnoreCase(Button.BACK)) {
            context.putPage(chatId, Page.MAIN);
        } else if (userText.matches(MESSAGE_WITH_URL_REGEXP)) {
            // todo можно объединить SuggestionToInputLinkPage и AddProductPage в один класс, чтобы было аналогично другому коду
            context.putPage(chatId, Page.ADD_PRODUCT);
        } else
            context.putPage(chatId, Page.UNEXPECTED);
    }

    @Override
    public Page getPage() {
        return Page.SUGGESTION_TO_INPUT_LINK;
    }
}