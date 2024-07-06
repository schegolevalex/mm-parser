package com.schegolevalex.mm.mmparser.bot.page.impl.filter;

import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.bot.page.base.PromoKeyboardPage;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.schegolevalex.mm.mmparser.bot.Constant.DELIMITER;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class ConfirmDeleteFilterPage extends PromoKeyboardPage {

    public ConfirmDeleteFilterPage(@Lazy ParserBot bot) {
        super(bot);
    }

    @Override
    public void beforeUpdateReceive(Update prevUpdate) {
        long filterId = Long.parseLong(prevUpdate.getCallbackQuery().getData().split(DELIMITER)[1]);
        bot.getSilent().execute(EditMessageReplyMarkup.builder()
                .chatId(getChatId(prevUpdate))
                .messageId(prevUpdate.getCallbackQuery().getMessage().getMessageId())
                .replyMarkup(Keyboard.withYesNoButtons(filterId))
                .build());
    }

    @Override
    public void afterUpdateReceive(Update nextUpdate) {
        resolvePromoKeyboard(nextUpdate);
    }

    @Override
    public Page getPage() {
        return Page.CONFIRM_DELETE_FILTER;
    }
}
