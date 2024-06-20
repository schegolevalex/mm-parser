package com.schegolevalex.mm.mmparser.bot.page.impl;

import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.bot.page.base.PromoKeyboardPage;
import com.schegolevalex.mm.mmparser.service.PromoService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.schegolevalex.mm.mmparser.bot.Constant.DELIMITER;
import static com.schegolevalex.mm.mmparser.bot.Constant.Message;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@Transactional
public class WatchFiltersPage extends PromoKeyboardPage {
    private final FilterService filterService;

    public WatchFiltersPage(@Lazy ParserBot bot, PromoService filterService) {
        super(bot);
        this.filterService = filterService;
    }

    @Override
    public void beforeUpdateReceive(Update prevUpdate) {
        Long chatId = getChatId(prevUpdate);

        if (prevUpdate.hasCallbackQuery()) {
            long filterId = Long.parseLong(prevUpdate.getCallbackQuery().getData().split(DELIMITER)[1]);
            bot.getSilent().execute(EditMessageReplyMarkup.builder()
                    .chatId(chatId)
                    .messageId(prevUpdate.getCallbackQuery().getMessage().getMessageId())
                    .replyMarkup(Keyboard.withDeleteFilterButton(filterId))
                    .build());
        } else {
            List<Filter> filters = filterService.findAllByChatId(chatId);

            if (filters.isEmpty()) {
                bot.getSilent().execute(SendMessage.builder()
                        .chatId(chatId)
                        .text(Message.FILTERS_IS_EMPTY)
                        .build());
            } else {
                AtomicInteger num = new AtomicInteger(1);
                filters.stream()
                        .sorted(Comparator.comparing(Filter::getCreatedAt))
                        .forEach(filter -> bot.getSilent().execute(SendMessage.builder()
                                .chatId(chatId)
                                // todo
//                                .text(num.getAndIncrement() + ". " +
//                                        filter.getPromoSteps().stream()
//                                                .map(promoStep -> String.format(Message.PROMO, promoStep.getDiscount(), promoStep.getPriceFrom()))
//                                                .collect(Collectors.joining("; ")))
                                .replyMarkup(Keyboard.withDeleteFilterButton(filter.getId()))
                                .build()));
            }
        }
    }

    @Override
    public void afterUpdateReceive(Update nextUpdate) {
        resolvePromoKeyboard(nextUpdate);
    }

    @Override
    public Page getPage() {
        return Page.WATCH_FILTERS;
    }
}
