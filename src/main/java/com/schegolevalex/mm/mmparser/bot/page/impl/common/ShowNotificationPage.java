package com.schegolevalex.mm.mmparser.bot.page.impl.common;

import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.handler.Handler;
import com.schegolevalex.mm.mmparser.bot.page.base.HandlersPage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Notification;
import com.schegolevalex.mm.mmparser.service.NotificationService;
import com.schegolevalex.mm.mmparser.service.OfferService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.schegolevalex.mm.mmparser.bot.Constant.Callback;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@Transactional
public class ShowNotificationPage extends HandlersPage {

    private final NotificationService notificationService;
    private final OfferService offerService;

    public ShowNotificationPage(ParserBot bot, List<Handler> handlers, NotificationService notificationService, OfferService offerService) {
        super(bot, handlers);
        this.notificationService = notificationService;
        this.offerService = offerService;
    }

    @Override
    public void show(Update prevUpdate) {
        Long chatId = getChatId(prevUpdate);
        Integer messageId = prevUpdate.getCallbackQuery().getMessage().getMessageId();

        Map<String, String> callbacks = extractCallbacksMap(prevUpdate);
        UUID parseId = UUID.fromString(callbacks.get(Callback.PARSE_ID));
        int notificationNumber = Integer.parseInt(callbacks.get(Callback.KEYBOARD_PAGES));

        List<Notification> notifications = notificationService.findByParseId(parseId);
        Notification notification = notifications.get(notificationNumber - 1);

        bot.getSilent().execute(EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(offerService.getOfferMessage(notification.getOffer()))
                .replyMarkup(Keyboard.withNotifications(parseId, notifications.size(), notificationNumber))
                .parseMode("MarkdownV2")
                .build());
    }

    @Override
    public Page getPage() {
        return Page.SHOW_NOTIFICATION;
    }
}
