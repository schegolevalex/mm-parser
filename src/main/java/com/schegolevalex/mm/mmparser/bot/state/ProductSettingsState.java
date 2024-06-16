package com.schegolevalex.mm.mmparser.bot.state;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class ProductSettingsState extends BaseState {

    public ProductSettingsState(@Lazy ParserBot bot) {
        super(bot);
    }

    @Override
    public void route(Update update) {
        Long chatId = getChatId(update);

        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            if (callbackData.startsWith(Constant.Button.NOTIFICATIONS_SETTINGS))
                context.putState(chatId, BotState.NOTIFICATIONS_SETTINGS);
            else if (callbackData.startsWith(Constant.Button.APPLY_PROMO))
                context.putState(chatId, BotState.APPLY_PROMO);
            else if (callbackData.startsWith(Constant.Button.BACK))
                context.putState(chatId, BotState.WATCH_PRODUCTS);
            else
                context.putState(chatId, BotState.UNEXPECTED);
        } else
            switch (update.getMessage().getText()) {
                case (Constant.Button.ADD_PRODUCT) -> context.putState(chatId, BotState.SUGGESTION_TO_INPUT_LINK);
                case (Constant.Button.MY_PRODUCTS) -> context.putState(chatId, BotState.WATCH_PRODUCTS);
                case (Constant.Button.SETTINGS) -> context.putState(chatId, BotState.SETTINGS);
                default -> context.putState(chatId, BotState.UNEXPECTED);
            }
    }

    @Override
    public void reply(Update update) {
        bot.getSilent().execute(EditMessageReplyMarkup.builder()
                .chatId(getChatId(update))
                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                .replyMarkup(Keyboard.withProductSettings(Long.parseLong(update.getCallbackQuery().getData().split(Constant.DELIMITER)[1])))
                .build());
    }

    @Override
    public BotState getType() {
        return BotState.PRODUCT_SETTINGS;
    }
}
