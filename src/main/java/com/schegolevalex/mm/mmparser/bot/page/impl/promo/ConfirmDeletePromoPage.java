package com.schegolevalex.mm.mmparser.bot.page.impl.promo;

import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.handler.Handler;
import com.schegolevalex.mm.mmparser.bot.page.base.HandlersPage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static com.schegolevalex.mm.mmparser.bot.Constant.DELIMITER;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class ConfirmDeletePromoPage extends HandlersPage {

    public ConfirmDeletePromoPage(ParserBot bot, List<Handler> handlers) {
        super(bot, handlers);
    }

    @Override
    public void show(Update prevUpdate) {
        long promoId = Long.parseLong(prevUpdate.getCallbackQuery().getData().split(DELIMITER)[1]);
        bot.getSilent().execute(EditMessageReplyMarkup.builder()
                .chatId(getChatId(prevUpdate))
                .messageId(prevUpdate.getCallbackQuery().getMessage().getMessageId())
                .replyMarkup(Keyboard.withConfirmOrDeclineDeletePromoButtons(promoId))
                .build());
    }

    @Override
    public Page getPage() {
        return Page.CONFIRM_DELETE_PROMO;
    }
}
