package com.schegolevalex.mm.mmparser.bot.page.impl;

import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.page.base.BasePage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Promo;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.schegolevalex.mm.mmparser.bot.Constant.Button;
import static com.schegolevalex.mm.mmparser.bot.Constant.Message;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class PromoSettingPage extends BasePage {
    public PromoSettingPage(@Lazy ParserBot bot) {
        super(bot);
    }

    @Override
    public void beforeUpdateReceive(Update prevUpdate) {
        bot.getSilent().execute(SendMessage.builder()
                .chatId(getChatId(prevUpdate))
                .text(Message.CHOOSE_ACTION)
                .replyMarkup(Keyboard.withPromoSettingsActions())
                .build());
    }

    @Override
    public void afterUpdateReceive(Update nextUpdate) {
        Long chatId = getChatId(nextUpdate);
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

    @Override
    public Page getPage() {
        return Page.PROMOS_SETTINGS;
    }
}
