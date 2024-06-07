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
public class CashbackSettingsState extends AbstractState {
    public CashbackSettingsState(@Lazy ParserBot bot) {
        super(bot);
    }

    @Override
    public void route(Update update) {
        Long chatId = getChatId(update);
        switch (update.getMessage().getText()) {
            // todo записать проценты в бд
//            case (Constant.Button.PROMOS_SETTINGS) -> context.putState(chatId, BotState.PROMOS_SETTINGS);
//            case (Constant.Button.CASHBACK_SETTINGS) -> context.putState(chatId, BotState.CASHBACK_SETTINGS);
            case (Constant.Button.BACK) -> context.popState(chatId);
            default -> context.putState(chatId, BotState.UNEXPECTED);
        }
    }

    @Override
    public void reply(Update update) {
        bot.getSilent().execute(SendMessage.builder()
                .chatId(getChatId(update))
                .text(Constant.Message.CHOOSE_YOUR_SBERPRIME_LEVEL)
                .replyMarkup(Keyboard.withCashbackLevels())
                .build());
    }

    @Override
    public BotState getType() {
        return BotState.CASHBACK_SETTINGS;
    }
}
