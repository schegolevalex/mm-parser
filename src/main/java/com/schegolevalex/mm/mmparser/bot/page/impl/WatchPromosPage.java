package com.schegolevalex.mm.mmparser.bot.page.impl;

import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.page.base.BasePage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Promo;
import com.schegolevalex.mm.mmparser.service.PromoService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.schegolevalex.mm.mmparser.bot.Constant.*;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@Transactional
public class WatchPromosPage extends BasePage {
    private final PromoService promoService;

    public WatchPromosPage(@Lazy ParserBot bot, PromoService promoService) {
        super(bot);
        this.promoService = promoService;
    }

    @Override
    public void beforeUpdateReceive(Update prevUpdate) {
        if (prevUpdate.hasCallbackQuery())
            return;

        Long chatId = getChatId(prevUpdate);
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
    }

    @Override
    public void afterUpdateReceive(Update nextUpdate) {
        Long chatId = getChatId(nextUpdate);
        if (nextUpdate.hasCallbackQuery() && nextUpdate.getCallbackQuery().getData().startsWith(Callback.DELETE_PROMO)) {
            promoService.deleteById(Long.parseLong(nextUpdate.getCallbackQuery().getData().split(DELIMITER)[1]));
            bot.getSilent().execute(DeleteMessage.builder()
                    .chatId(chatId)
                    .messageId(nextUpdate.getCallbackQuery().getMessage().getMessageId())
                    .build());
        } else {
            switch (nextUpdate.getMessage().getText()) {
                case (Button.ADD_PROMO) -> {
                    context.putPromo(chatId, new Promo());
                    context.putPage(chatId, Page.ADD_PROMO_STEP_DISCOUNT);
                }
                case (Button.MY_PROMOS) -> context.putPage(chatId, Page.WATCH_PROMOS);
                case (Button.BACK) -> context.putPage(chatId, Page.COMMON_SETTINGS);
                default -> context.putPage(chatId, Page.UNEXPECTED);
            }
        }
    }

    @Override
    public Page getPage() {
        return Page.WATCH_PROMOS;
    }
}
