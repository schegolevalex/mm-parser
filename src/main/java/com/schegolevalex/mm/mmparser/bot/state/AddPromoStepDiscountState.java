package com.schegolevalex.mm.mmparser.bot.state;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.entity.Promo;
import com.schegolevalex.mm.mmparser.entity.PromoStep;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class AddPromoStepDiscountState extends BaseState {

    public AddPromoStepDiscountState(@Lazy ParserBot bot) {
        super(bot);
    }

    @Override
    public void route(Update update) {
        Long chatId = getChatId(update);
        String text = update.getMessage().getText();

        try {
            int discount = Integer.parseInt(text);
            Promo promo = context.getPromo(chatId);
            promo.addPromoStep(PromoStep.builder()
                    .discount(discount)
                    .build());
            context.putState(chatId, BotState.ADD_PROMO_STEP_PRICE);
        } catch (NumberFormatException e) {
            switch (text) {
                case Constant.Button.ADD_PROMO -> context.putState(chatId, BotState.ADD_PROMO_STEP_PRICE);
                case Constant.Button.MY_PROMOS -> context.putState(chatId, BotState.WATCH_PROMOS);
                case Constant.Button.BACK -> context.putState(chatId, BotState.SETTINGS);
                default -> context.putState(chatId, BotState.UNEXPECTED);
            }
        }
    }

    @Override
    public void reply(Update update) {
        bot.getSilent().execute(SendMessage.builder()
                .chatId(getChatId(update))
                .text(Constant.Message.ADD_PROMO_STEP_DISCOUNT)
                .replyMarkup(Keyboard.withPromoSettingsActions())
                .parseMode("MarkdownV2")
                .build());
    }

    @Override
    public BotState getType() {
        return BotState.ADD_PROMO_STEP_DISCOUNT;
    }
}
