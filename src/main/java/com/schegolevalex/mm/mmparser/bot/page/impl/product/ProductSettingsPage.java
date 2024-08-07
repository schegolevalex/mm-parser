package com.schegolevalex.mm.mmparser.bot.page.impl.product;

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
public class ProductSettingsPage extends HandlersPage {

    public ProductSettingsPage(ParserBot bot, List<Handler> handlers) {
        super(bot, handlers);
    }

    @Override
    public void show(Update prevUpdate) {
        bot.getSilent().execute(EditMessageReplyMarkup.builder()
                .chatId(getChatId(prevUpdate))
                .messageId(prevUpdate.getCallbackQuery().getMessage().getMessageId())
                .replyMarkup(Keyboard.withProductSettings(Long.parseLong(prevUpdate.getCallbackQuery().getData().split(DELIMITER)[1])))
                .build());
    }

    @Override
    public Page getPage() {
        return Page.PRODUCT_SETTINGS;
    }
}
