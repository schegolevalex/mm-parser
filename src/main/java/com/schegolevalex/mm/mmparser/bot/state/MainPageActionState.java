package com.schegolevalex.mm.mmparser.bot.state;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class MainPageActionState extends BaseState {

    public MainPageActionState(@Lazy ParserBot bot) {
        super(bot);
    }

    @Override
    public void route(Update update) {
        Long chatId = getChatId(update);
        switch (update.getMessage().getText()) {
            case (Constant.Button.ADD_LINK) -> context.putState(chatId, BotState.SUGGESTION_TO_INPUT_LINK);
            case (Constant.Button.MY_LINKS) -> context.putState(chatId, BotState.WATCH_LINKS);
            case (Constant.Button.SETTINGS) -> context.putState(chatId, BotState.SETTINGS);
            default -> context.putState(chatId, BotState.UNEXPECTED);
        }
    }

    @Override
    public void reply(Update update) {
        bot.getSilent().execute(SendMessage.builder()
                .chatId(getChatId(update))
                .text(Constant.Message.CHOOSE_ACTION)
                .replyMarkup(Keyboard.withMainPageActions())
                .build());
    }

    @Override
    public BotState getType() {
        return BotState.MAIN_PAGE_ACTION;
    }
}