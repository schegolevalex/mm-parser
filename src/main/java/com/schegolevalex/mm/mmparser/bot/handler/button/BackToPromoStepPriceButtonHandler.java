package com.schegolevalex.mm.mmparser.bot.handler.button;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Context;
import com.schegolevalex.mm.mmparser.bot.handler.BaseHandler;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class BackToPromoStepPriceButtonHandler extends BaseHandler {

    public BackToPromoStepPriceButtonHandler(Context context) {
        super(context);
    }

    @Override
    public void handle(Update update) {
        context.putPage(getChatId(update), Page.ADD_PROMO_STEP_PRICE);
    }

    @Override
    public boolean isSuitable(Update update) {
        return update.hasMessage()
               && update.getMessage().hasText()
               && update.getMessage().getText().equals(Constant.Button.BACK_TO_PROMO_STEP_PRICE);
    }
}
