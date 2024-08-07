package com.schegolevalex.mm.mmparser.bot.handler.button;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Context;
import com.schegolevalex.mm.mmparser.bot.handler.BaseHandler;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Promo;
import com.schegolevalex.mm.mmparser.entity.PromoStep;
import com.schegolevalex.mm.mmparser.entity.User;
import com.schegolevalex.mm.mmparser.service.PromoService;
import com.schegolevalex.mm.mmparser.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Comparator;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class NoSavePromoButtonHandler extends BaseHandler {
    private final UserService userService;
    private final PromoService promoService;

    public NoSavePromoButtonHandler(Context context,
                                    UserService userService,
                                    PromoService promoService) {
        super(context);
        this.userService = userService;
        this.promoService = promoService;
    }

    @Override
    public void handle(Update update) {
        Long chatId = getChatId(update);
        Promo promo = context.getPromo(chatId);
        promo.getPromoSteps().sort(Comparator.comparing(PromoStep::getPriceFrom));
        User user = userService.findByChatId(chatId).orElseThrow(() -> new RuntimeException("User not found"));
        promo.setUser(user);
        promoService.save(promo);
        context.clearPromo(chatId);
        context.putPage(chatId, Page.PROMOS_SETTINGS);
    }

    @Override
    public boolean isSuitable(Update update) {
        return update.hasMessage()
               && update.getMessage().hasText()
               && update.getMessage().getText().equals(Constant.Button.NO_SAVE_PROMO);
    }
}
