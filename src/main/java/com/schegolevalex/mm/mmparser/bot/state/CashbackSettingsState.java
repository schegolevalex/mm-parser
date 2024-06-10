package com.schegolevalex.mm.mmparser.bot.state;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.entity.User;
import com.schegolevalex.mm.mmparser.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@Transactional
public class CashbackSettingsState extends BaseState {
    private final UserService userService;

    public CashbackSettingsState(@Lazy ParserBot bot, UserService userService) {
        super(bot);
        this.userService = userService;
    }

    @Override
    public void route(Update update) {
        Long chatId = getChatId(update);

        if (update.hasCallbackQuery()) {
            User user = userService.findByChatId(chatId).orElseThrow(() -> new RuntimeException("User not found"));
            user.setCashbackLevel(Long.parseLong(update.getCallbackQuery().getData()));
        } else {
            switch (update.getMessage().getText()) {
                case (Constant.Button.PROMOS_SETTINGS) -> context.putState(chatId, BotState.PROMOS_SETTINGS);
                case (Constant.Button.CASHBACK_SETTINGS) -> context.putState(chatId, BotState.CASHBACK_SETTINGS);
                case (Constant.Button.BACK) -> context.putState(chatId, BotState.SETTINGS);
                default -> context.putState(chatId, BotState.UNEXPECTED);
            }
        }
    }

    @Override
    public void reply(Update update) {
        Long cashbackLevel = userService.findByChatId(getChatId(update))
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getCashbackLevel();

        if (update.hasCallbackQuery())
            bot.getSilent().execute(EditMessageReplyMarkup.builder()
                    .chatId(getChatId(update))
                    .messageId(update.getCallbackQuery().getMessage().getMessageId())
                    .replyMarkup(Keyboard.withCashbackLevels(cashbackLevel))
                    .build());
        else
            bot.getSilent().execute(SendMessage.builder()
                    .chatId(getChatId(update))
                    .text(Constant.Message.CHOOSE_YOUR_CASHBACK_LEVEL)
                    .replyMarkup(Keyboard.withCashbackLevels(cashbackLevel))
                    .parseMode("MarkdownV2")
                    .build());
    }

    @Override
    public BotState getType() {
        return BotState.CASHBACK_SETTINGS;
    }
}
