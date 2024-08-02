package com.schegolevalex.mm.mmparser.bot.page.impl.product;

import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.bot.page.base.ProductKeyboardPage;
import com.schegolevalex.mm.mmparser.service.FilterService;
import com.schegolevalex.mm.mmparser.service.ProductService;
import com.schegolevalex.mm.mmparser.service.PromoService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.schegolevalex.mm.mmparser.bot.Constant.DELIMITER;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class ConfirmDeleteProductPage extends ProductKeyboardPage {

    public ConfirmDeleteProductPage(@Lazy ParserBot bot,
                                    ProductService productService,
                                    PromoService promoService,
                                    FilterService filterService) {
        super(bot, productService, promoService, filterService);
    }

    @Override
    public void beforeUpdateReceive(Update prevUpdate) {
        long productId = Long.parseLong(prevUpdate.getCallbackQuery().getData().split(DELIMITER)[1]);
        bot.getSilent().execute(EditMessageReplyMarkup.builder()
                .chatId(getChatId(prevUpdate))
                .messageId(prevUpdate.getCallbackQuery().getMessage().getMessageId())
                .replyMarkup(Keyboard.withConfirmOrDeclineButtons(productId))
                .build());
    }

    @Override
    public void afterUpdateReceive(Update nextUpdate) {
        resolveProductKeyboardAction(nextUpdate);
    }

    @Override
    public Page getPage() {
        return Page.CONFIRM_DELETE_PRODUCT;
    }
}
