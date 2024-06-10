package com.schegolevalex.mm.mmparser.bot.state;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
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

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@Transactional
public class WatchPromosState extends BaseState {
    private final PromoService promoService;

    public WatchPromosState(@Lazy ParserBot bot, PromoService promoService) {
        super(bot);
        this.promoService = promoService;
    }

    @Override
    public void route(Update update) {
        Long chatId = getChatId(update);
        if (update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith(Constant.Button.DELETE_PROMO)) {
            promoService.delete(Long.parseLong(update.getCallbackQuery().getData().split(Constant.DELIMITER)[1]));
            bot.getSilent().execute(DeleteMessage.builder()
                    .chatId(chatId)
                    .messageId(update.getCallbackQuery().getMessage().getMessageId())
                    .build());
        } else {
            switch (update.getMessage().getText()) {
                case (Constant.Button.ADD_PROMO) -> {
                    context.putPromo(chatId, new Promo());
                    context.putState(chatId, BotState.ADD_PROMO_STEP_DISCOUNT);
                }
                case (Constant.Button.MY_PROMOS) -> context.putState(chatId, BotState.WATCH_PROMOS);
                case (Constant.Button.BACK) -> context.putState(chatId, BotState.SETTINGS);
                default -> context.putState(chatId, BotState.UNEXPECTED);
            }
        }
    }

    @Override
    public void reply(Update update) {
        if (update.hasCallbackQuery())
            return;

        Long chatId = getChatId(update);
        List<Promo> promos = promoService.findAllByChatId(chatId);

        if (promos.isEmpty()) {
            bot.getSilent().execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(Constant.Message.PROMOS_IS_EMPTY)
                    .build());
        } else {
            AtomicInteger num = new AtomicInteger(1);
            promos.stream()
                    .sorted(Comparator.comparing(Promo::getCreatedAt))
                    .forEach(promo -> bot.getSilent().execute(SendMessage.builder()
                            .chatId(chatId)
                            .text(num.getAndIncrement() + ". " +
                                    promo.getPromoSteps().stream()
                                            .map(promoStep -> String.format(Constant.Message.PROMO, promoStep.getDiscount(), promoStep.getPriceFrom()))
                                            .collect(Collectors.joining(", ")))
                            .replyMarkup(Keyboard.withDeletePromoButton(promo.getId()))
                            .build()));
        }
    }

    @Override
    public BotState getType() {
        return BotState.WATCH_PROMOS;
    }
}
