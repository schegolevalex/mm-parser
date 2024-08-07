package com.schegolevalex.mm.mmparser.bot.handler.button;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Context;
import com.schegolevalex.mm.mmparser.bot.handler.BaseHandler;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class YesAddMorePromoStepsButtonHandler extends BaseHandler {
    public YesAddMorePromoStepsButtonHandler(Context context) {
        super(context);
    }

    @Override
    public void handle(Update update) {
        context.putPage(getChatId(update), Page.ADD_PROMO_STEP_DISCOUNT);
    }

    @Override
    public boolean isSuitable(Update update) {
        return update.hasMessage()
               && update.getMessage().hasText()
               && update.getMessage().getText().equals(Constant.Button.YES_ADD_MORE_PROMO_STEPS);
    }
}
