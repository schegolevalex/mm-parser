package com.schegolevalex.mm.mmparser.bot.page.impl.promo;

import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.handler.Handler;
import com.schegolevalex.mm.mmparser.bot.page.base.HandlersPage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Promo;
import com.schegolevalex.mm.mmparser.service.PromoService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.schegolevalex.mm.mmparser.bot.Constant.DELIMITER;
import static com.schegolevalex.mm.mmparser.bot.Constant.Message;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@Transactional
public class PromoSettingsPage extends HandlersPage {

    private final PromoService promoService;

    public PromoSettingsPage(ParserBot bot,
                             List<Handler> handlers,
                             PromoService promoService) {
        super(bot, handlers);
        this.promoService = promoService;
    }

    @Override
    public void show(Update prevUpdate) {
        Long chatId = getChatId(prevUpdate);

        if (prevUpdate.hasCallbackQuery()) {
            long promoId = Long.parseLong(prevUpdate.getCallbackQuery().getData().split(DELIMITER)[1]);
            bot.getSilent().execute(EditMessageReplyMarkup.builder()
                    .chatId(chatId)
                    .messageId(prevUpdate.getCallbackQuery().getMessage().getMessageId())
                    .replyMarkup(Keyboard.withDeletePromoButton(promoId))
                    .build());
        } else {
            List<Promo> promos = promoService.findAllByChatId(chatId);

            if (promos.isEmpty()) {
                bot.getSilent().execute(SendMessage.builder()
                        .chatId(chatId)
                        .text(Message.PROMOS_IS_EMPTY)
                        .build());
            } else {
                AtomicInteger num = new AtomicInteger(1);
                promos.stream()
                        .sorted(Comparator.comparing(Promo::getCreatedAt))
                        .forEach(promo -> bot.getSilent().execute(SendMessage.builder()
                                .chatId(chatId)
                                .text(num.getAndIncrement() + ". " +
                                      promo.getPromoSteps().stream()
                                              .map(promoStep -> String.format(Message.PROMO, promoStep.getDiscount(), promoStep.getPriceFrom()))
                                              .collect(Collectors.joining("; ")))
                                .replyMarkup(Keyboard.withDeletePromoButton(promo.getId()))
                                .build()));
            }
            bot.getSilent().execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(Message.CHOOSE_ACTION)
                    .replyMarkup(Keyboard.withPromoSettingsActions())
                    .build());
        }
    }

    @Override
    public Page getPage() {
        return Page.PROMOS_SETTINGS;
    }
}
