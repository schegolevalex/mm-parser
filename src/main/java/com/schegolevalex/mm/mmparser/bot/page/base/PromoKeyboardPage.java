package com.schegolevalex.mm.mmparser.bot.page.base;

import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.entity.Promo;
import org.springframework.context.annotation.Lazy;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.schegolevalex.mm.mmparser.bot.Constant.Button;
import static com.schegolevalex.mm.mmparser.bot.Constant.Callback;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

public abstract class PromoKeyboardPage extends BasePage {
    public PromoKeyboardPage(@Lazy ParserBot bot) {
        super(bot);
    }

    protected void resolvePromoKeyboard(Update nextUpdate) {
        Long chatId = getChatId(nextUpdate);
        if (nextUpdate.hasCallbackQuery()) {
            String callback = nextUpdate.getCallbackQuery().getData();
            if (callback.startsWith(Callback.DELETE_PROMO))
                context.putPage(chatId, Page.CONFIRM_DELETE_PROMO);
            else if (callback.startsWith(Callback.CONFIRM_DELETE))
                context.putPage(chatId, Page.DELETE_PROMO);
            else if (callback.startsWith(Callback.DECLINE_DELETE))
                context.putPage(chatId, Page.WATCH_PROMOS);
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
}
