package com.schegolevalex.mm.mmparser.bot.page.impl.cashback;

import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.page.base.BasePage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.User;
import com.schegolevalex.mm.mmparser.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.schegolevalex.mm.mmparser.bot.Constant.Button;
import static com.schegolevalex.mm.mmparser.bot.Constant.Message;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@Transactional
public class CashbackSettingsPage extends BasePage {
    private final UserService userService;

    public CashbackSettingsPage(@Lazy ParserBot bot, UserService userService) {
        super(bot);
        this.userService = userService;
    }

    @Override
    public void beforeUpdateReceive(Update prevUpdate) {
        Integer cashbackLevel = userService.findByChatId(getChatId(prevUpdate))
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getCashbackLevel();

        if (prevUpdate.hasCallbackQuery())
            bot.getSilent().execute(EditMessageReplyMarkup.builder()
                    .chatId(getChatId(prevUpdate))
                    .messageId(prevUpdate.getCallbackQuery().getMessage().getMessageId())
                    .replyMarkup(Keyboard.withCashbackLevels(cashbackLevel))
                    .build());
        else
            bot.getSilent().execute(SendMessage.builder()
                    .chatId(getChatId(prevUpdate))
                    .text(Message.CHOOSE_YOUR_CASHBACK_LEVEL)
                    .replyMarkup(Keyboard.withCashbackLevels(cashbackLevel))
                    .parseMode("MarkdownV2")
                    .build());
    }

    @Override
    public void afterUpdateReceive(Update nextUpdate) {
        Long chatId = getChatId(nextUpdate);

        if (nextUpdate.hasCallbackQuery()) {
            User user = userService.findByChatId(chatId).orElseThrow(() -> new RuntimeException("User not found"));
            user.setCashbackLevel(Integer.parseInt(nextUpdate.getCallbackQuery().getData()));
        } else {
            switch (nextUpdate.getMessage().getText()) {
                case (Button.PROMOS_SETTINGS) -> context.putPage(chatId, Page.PROMOS_SETTINGS);
                case (Button.CASHBACK_SETTINGS) -> context.putPage(chatId, Page.CASHBACK_SETTINGS);
                case (Button.FILTERS_SETTINGS) -> context.putPage(chatId, Page.FILTERS_SETTINGS);
                case (Button.BACK) -> context.putPage(chatId, Page.COMMON_SETTINGS);
                default -> context.putPage(chatId, Page.UNEXPECTED);
            }
        }
    }

    @Override
    public Page getPage() {
        return Page.CASHBACK_SETTINGS;
    }
}
