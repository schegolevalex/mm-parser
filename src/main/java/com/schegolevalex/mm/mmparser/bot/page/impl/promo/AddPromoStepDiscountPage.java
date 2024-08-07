package com.schegolevalex.mm.mmparser.bot.page.impl.promo;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.handler.Handler;
import com.schegolevalex.mm.mmparser.bot.page.base.HandlersPage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Promo;
import com.schegolevalex.mm.mmparser.entity.PromoStep;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static com.schegolevalex.mm.mmparser.bot.Constant.Message;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class AddPromoStepDiscountPage extends HandlersPage {

    public AddPromoStepDiscountPage(ParserBot bot, List<Handler> handlers) {
        super(bot, handlers);
    }

    @Override
    public void show(Update prevUpdate) {
        if (prevUpdate.hasMessage()
            && prevUpdate.getMessage().hasText()
            && prevUpdate.getMessage().getText().equals(Constant.Button.BACK_TO_PROMO_STEP_DISCOUNT)) {
            context.getPromo(getChatId(prevUpdate)).getPromoSteps().removeLast();
        }
        bot.getSilent().execute(SendMessage.builder()
                .chatId(getChatId(prevUpdate))
                .text(Message.ADD_PROMO_STEP_DISCOUNT)
                .replyMarkup(Keyboard.withBackToPromoSettingsButton())
                .parseMode("MarkdownV2")
                .build());
    }

    @Override
    public void afterUpdateReceived(Update nextUpdate) {
        Long chatId = getChatId(nextUpdate);
        if (nextUpdate.hasMessage() && nextUpdate.getMessage().hasText()) {
            String text = nextUpdate.getMessage().getText();
            try {
                int discount = Integer.parseInt(text);
                Promo promo = context.getPromo(chatId);
                PromoStep promoStep = new PromoStep();
                promoStep.setDiscount(discount);
                promo.addPromoStep(promoStep);
                context.putPage(chatId, Page.ADD_PROMO_STEP_PRICE);
            } catch (NumberFormatException e) {
                super.afterUpdateReceived(nextUpdate);
            } catch (IllegalArgumentException e) {
                bot.getSilent().execute(SendMessage.builder()
                        .chatId(chatId)
                        .text(e.getMessage())
                        .build());
            }
        } else
            super.afterUpdateReceived(nextUpdate);
    }

    @Override
    public Page getPage() {
        return Page.ADD_PROMO_STEP_DISCOUNT;
    }
}
