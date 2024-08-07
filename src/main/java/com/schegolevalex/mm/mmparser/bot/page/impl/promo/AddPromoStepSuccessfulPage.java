package com.schegolevalex.mm.mmparser.bot.page.impl.promo;

import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.handler.Handler;
import com.schegolevalex.mm.mmparser.bot.page.base.HandlersPage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Promo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static com.schegolevalex.mm.mmparser.bot.Constant.Message;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@Transactional
public class AddPromoStepSuccessfulPage extends HandlersPage {

    public AddPromoStepSuccessfulPage(ParserBot bot, List<Handler> handlers) {
        super(bot, handlers);
    }

    @Override
    public void show(Update prevUpdate) {
        Promo promo = context.getPromo(getChatId(prevUpdate));
        Integer discount = promo.getPromoSteps().getLast().getDiscount();
        Integer priceFrom = promo.getPromoSteps().getLast().getPriceFrom();
        bot.getSilent().execute(SendMessage.builder()
                .chatId(getChatId(prevUpdate))
                .text(String.format(Message.ADD_PROMO_STEP_SUCCESSFUL, discount, priceFrom))
                .replyMarkup(Keyboard.continueAddOrSavePromoSteps())
                .parseMode("MarkdownV2")
                .build());
    }

    @Override
    public Page getPage() {
        return Page.ADD_PROMO_STEP_SUCCESSFUL;
    }
}