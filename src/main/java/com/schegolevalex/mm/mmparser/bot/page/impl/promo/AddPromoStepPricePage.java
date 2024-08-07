package com.schegolevalex.mm.mmparser.bot.page.impl.promo;

import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.handler.Handler;
import com.schegolevalex.mm.mmparser.bot.page.base.HandlersPage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Promo;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static com.schegolevalex.mm.mmparser.bot.Constant.Message;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class AddPromoStepPricePage extends HandlersPage {

    public AddPromoStepPricePage(ParserBot bot, List<Handler> handlers) {
        super(bot, handlers);
    }

    @Override
    public void show(Update prevUpdate) {
        bot.getSilent().execute(SendMessage.builder()
                .chatId(getChatId(prevUpdate))
                .text(Message.ADD_PROMO_STEP_PRICE)
                .replyMarkup(Keyboard.withBackToPromoStepDiscountButton())
                .parseMode("MarkdownV2")
                .build());
    }

    @Override
    public void afterUpdateReceived(Update nextUpdate) {
        Long chatId = getChatId(nextUpdate);
        if (nextUpdate.hasMessage() && nextUpdate.getMessage().hasText()) {
            String text = nextUpdate.getMessage().getText();
            try {
                int priceFrom = Integer.parseInt(text);
                Promo promo = context.getPromo(chatId);
                promo.getPromoSteps().getLast().setPriceFrom(priceFrom);
                context.putPage(chatId, Page.ADD_PROMO_STEP_SUCCESSFUL);
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
        return Page.ADD_PROMO_STEP_PRICE;
    }
}
