package com.schegolevalex.mm.mmparser.bot.page.impl.promo;

import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.handler.Handler;
import com.schegolevalex.mm.mmparser.bot.page.base.HandlersPage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.service.PromoService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static com.schegolevalex.mm.mmparser.bot.Constant.DELIMITER;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@Transactional
public class DeletePromoPage extends HandlersPage {

    private final PromoService promoService;

    public DeletePromoPage(ParserBot bot,
                           List<Handler> handlers,
                           PromoService promoService) {
        super(bot, handlers);
        this.promoService = promoService;
    }

    @Override
    public void show(Update prevUpdate) {
        Long chatId = getChatId(prevUpdate);
        long promoId = Long.parseLong(prevUpdate.getCallbackQuery().getData().split(DELIMITER)[1]);
        promoService.deleteById(promoId);
        bot.getSilent().execute(DeleteMessage.builder()
                .chatId(chatId)
                .messageId(prevUpdate.getCallbackQuery().getMessage().getMessageId())
                .build());
    }

    @Override
    public Page getPage() {
        return Page.DELETE_PROMO;
    }
}
