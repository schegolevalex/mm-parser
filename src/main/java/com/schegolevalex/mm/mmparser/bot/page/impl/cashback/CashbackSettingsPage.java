package com.schegolevalex.mm.mmparser.bot.page.impl.cashback;

import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.handler.Handler;
import com.schegolevalex.mm.mmparser.bot.page.base.HandlersPage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static com.schegolevalex.mm.mmparser.bot.Constant.Message;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@Transactional
public class CashbackSettingsPage extends HandlersPage {
    private final UserService userService;

    public CashbackSettingsPage(ParserBot bot,
                                List<Handler> handlers,
                                UserService userService) {
        super(bot, handlers);
        this.userService = userService;
    }

    @Override
    public void show(Update prevUpdate) {
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
    public Page getPage() {
        return Page.CASHBACK_SETTINGS;
    }
}