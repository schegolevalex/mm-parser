package com.schegolevalex.mm.mmparser.bot.page.impl.promo;

import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.page.base.BasePage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Promo;
import com.schegolevalex.mm.mmparser.entity.PromoStep;
import com.schegolevalex.mm.mmparser.entity.User;
import com.schegolevalex.mm.mmparser.service.PromoService;
import com.schegolevalex.mm.mmparser.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Comparator;

import static com.schegolevalex.mm.mmparser.bot.Constant.Button;
import static com.schegolevalex.mm.mmparser.bot.Constant.Message;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@Transactional
public class AddPromoStepSuccessfulPage extends BasePage {
    private final UserService userService;
    private final PromoService promoService;

    public AddPromoStepSuccessfulPage(@Lazy ParserBot bot, UserService userService, PromoService promoService) {
        super(bot);
        this.userService = userService;
        this.promoService = promoService;
    }

    @Override
    public void beforeUpdateReceive(Update prevUpdate) {
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
    public void afterUpdateReceive(Update nextUpdate) {
        Long chatId = getChatId(nextUpdate);
        String text = nextUpdate.getMessage().getText();
        switch (text) {
            case Button.YES_ADD_MORE_PROMO_STEPS -> context.putPage(chatId, Page.ADD_PROMO_STEP_DISCOUNT);
            case Button.NO_SAVE_PROMO -> {
                Promo promo = context.getPromo(chatId);
                promo.getPromoSteps().sort(Comparator.comparing(PromoStep::getPriceFrom));
                User user = userService.findByChatId(chatId).orElseThrow(() -> new RuntimeException("User not found"));
                promo.setUser(user);
                promoService.save(promo);
                context.clearPromo(chatId);
                context.putPage(chatId, Page.PROMOS_SETTINGS);
            }
            case Button.BACK -> context.popPage(chatId);
            default -> context.putPage(chatId, Page.UNEXPECTED);
        }
    }

    @Override
    public Page getPage() {
        return Page.ADD_PROMO_STEP_SUCCESSFUL;
    }
}
