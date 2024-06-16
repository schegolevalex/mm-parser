package com.schegolevalex.mm.mmparser.bot.page.impl;

import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.bot.page.base.ProductKeyboardPage;
import com.schegolevalex.mm.mmparser.service.ProductService;
import com.schegolevalex.mm.mmparser.service.PromoService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.schegolevalex.mm.mmparser.bot.Constant.DELIMITER;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class ProductSettingsPage extends ProductKeyboardPage {

    public ProductSettingsPage(@Lazy ParserBot bot, ProductService productService, PromoService promoService) {
        super(bot, productService, promoService);
    }

    @Override
    public void beforeUpdateReceive(Update prevUpdate) {
        bot.getSilent().execute(EditMessageReplyMarkup.builder()
                .chatId(getChatId(prevUpdate))
                .messageId(prevUpdate.getCallbackQuery().getMessage().getMessageId())
                .replyMarkup(Keyboard.withProductSettings(Long.parseLong(prevUpdate.getCallbackQuery().getData().split(DELIMITER)[1])))
                .build());
    }

    @Override
    public void afterUpdateReceive(Update nextUpdate) {
        resolveProductKeyboardAction(nextUpdate);
    }

    @Override
    public Page getPage() {
        return Page.PRODUCT_SETTINGS;
    }
}
