package com.schegolevalex.mm.mmparser.bot.state;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class SuggestionToInputLinkState extends BaseState {
    private static final String MESSAGE_WITH_URL_REGEXP = ".*(http(s)?://)?(www\\.)?megamarket\\.ru\\b([-a-zA-Z0-9@:%_+.~#?&/=]*)";

    public SuggestionToInputLinkState(@Lazy ParserBot bot) {
        super(bot);
    }

    @Override
    public void route(Update update) {
        Long chatId = AbilityUtils.getChatId(update);
        String userText = update.getMessage().getText();

        if (userText.equalsIgnoreCase(Constant.Button.BACK)) {
            context.popState(chatId);
        } else if (userText.matches(MESSAGE_WITH_URL_REGEXP)) {
            context.putState(chatId, BotState.INPUT_LINK);
        } else
            context.putState(chatId, BotState.UNEXPECTED);
    }

    @Override
    public void reply(Update update) {
        bot.getSilent().execute(SendMessage.builder()
                .chatId(getChatId(update))
                .text(Constant.Message.SUGGESTION_TO_LINK_INPUT)
                .replyMarkup(Keyboard.withBackButton())
                .build());
    }

    @Override
    public BotState getType() {
        return BotState.SUGGESTION_TO_INPUT_LINK;
    }
}