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
public class PromoSettingState extends BaseState {
    public PromoSettingState(@Lazy ParserBot bot) {
        super(bot);
    }

    @Override
    public void route(Update update) {
        Long chatId = getChatId(update);
        switch (update.getMessage().getText()) {
            case (Constant.Button.ADD_PROMO) -> context.putState(chatId, BotState.ADD_PROMO);
            case (Constant.Button.MY_PROMOS) -> context.putState(chatId, BotState.WATCH_PROMOS);
            case (Constant.Button.BACK) -> context.putState(chatId, BotState.SETTINGS);
            default -> context.putState(chatId, BotState.UNEXPECTED);
        }
    }

    @Override
    public void reply(Update update) {
        bot.getSilent().execute(SendMessage.builder()
                .chatId(getChatId(update))
                .text(Constant.Message.CHOOSE_ACTION)
                .replyMarkup(Keyboard.withPromoSettingsActions())
                .build());
    }

    @Override
    public BotState getType() {
        return BotState.PROMOS_SETTINGS;
    }
}
