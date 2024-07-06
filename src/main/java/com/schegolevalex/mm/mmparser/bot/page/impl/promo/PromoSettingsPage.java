package com.schegolevalex.mm.mmparser.bot.page.impl.promo;

import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.bot.page.base.PromoKeyboardPage;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.schegolevalex.mm.mmparser.bot.Constant.Message;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class PromoSettingsPage extends PromoKeyboardPage {
    public PromoSettingsPage(@Lazy ParserBot bot) {
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
        resolvePromoKeyboard(nextUpdate);
    }

    @Override
    public Page getPage() {
        return Page.PROMOS_SETTINGS;
    }
}
