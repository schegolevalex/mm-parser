package com.schegolevalex.mm.mmparser.bot.state;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.entity.Promo;
import com.schegolevalex.mm.mmparser.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@Transactional
public class AddPromoStepSuccessful extends BaseState {
    private final UserService userService;

    public AddPromoStepSuccessful(@Lazy ParserBot bot, UserService userService) {
        super(bot);
        this.userService = userService;
    }

    @Override
    public void route(Update update) {
        Long chatId = getChatId(update);
        String text = update.getMessage().getText();
        switch (text) {
            case Constant.Button.YES_ADD_MORE_PROMO_STEPS -> context.putState(chatId, BotState.ADD_PROMO_STEP_DISCOUNT);
            case Constant.Button.NO_SAVE_PROMO -> {
                userService.findByChatId(chatId).orElseThrow(() -> new RuntimeException("User not found"))
                        .addPromo(context.getPromo(chatId));
                context.clearPromo(chatId);
                context.putState(chatId, BotState.PROMOS_SETTINGS);
            }
            case Constant.Button.BACK -> context.popState(chatId);
            default -> context.putState(chatId, BotState.UNEXPECTED);
        }
    }

    @Override
    public void reply(Update update) {
        Promo promo = context.getPromo(getChatId(update));
        Integer discount = promo.getPromoSteps().getLast().getDiscount();
        Integer priceFrom = promo.getPromoSteps().getLast().getPriceFrom();
        bot.getSilent().execute(SendMessage.builder()
                .chatId(getChatId(update))
                .text(String.format(Constant.Message.ADD_PROMO_STEP_SUCCESSFUL, discount, priceFrom))
                .replyMarkup(Keyboard.continueAddOrSavePromoSteps())
                .parseMode("MarkdownV2")
                .build());
    }

    @Override
    public BotState getType() {
        return BotState.ADD_PROMO_STEP_SUCCESSFUL;
    }
}
