package com.schegolevalex.mm.mmparser.bot.page.impl.promo;

import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.bot.page.base.PromoKeyboardPage;
import com.schegolevalex.mm.mmparser.service.PromoService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.schegolevalex.mm.mmparser.bot.Constant.DELIMITER;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@Transactional
public class DeletePromoPage extends PromoKeyboardPage {

    private final PromoService promoService;

    public DeletePromoPage(@Lazy ParserBot bot, PromoService promoService) {
        super(bot);
        this.promoService = promoService;
    }

    @Override
    public void beforeUpdateReceive(Update prevUpdate) {
        Long chatId = getChatId(prevUpdate);
        long promoId = Long.parseLong(prevUpdate.getCallbackQuery().getData().split(DELIMITER)[1]);
        promoService.deleteById(promoId);
        bot.getSilent().execute(DeleteMessage.builder()
                .chatId(chatId)
                .messageId(prevUpdate.getCallbackQuery().getMessage().getMessageId())
                .build());
    }

    @Override
    public void afterUpdateReceive(Update nextUpdate) {
        resolvePromoKeyboard(nextUpdate);
    }

    @Override
    public Page getPage() {
        return Page.DELETE_PROMO;
    }
}
