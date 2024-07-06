package com.schegolevalex.mm.mmparser.bot.page.impl.filter;

import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.bot.page.base.PromoKeyboardPage;
import com.schegolevalex.mm.mmparser.service.FilterService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.schegolevalex.mm.mmparser.bot.Constant.DELIMITER;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@Transactional
public class DeleteFilterPage extends PromoKeyboardPage {

    private final FilterService filterService;

    public DeleteFilterPage(@Lazy ParserBot bot, FilterService filterService) {
        super(bot);
        this.filterService = filterService;
    }

    @Override
    public void beforeUpdateReceive(Update prevUpdate) {
        Long chatId = getChatId(prevUpdate);
        long filterId = Long.parseLong(prevUpdate.getCallbackQuery().getData().split(DELIMITER)[1]);
        filterService.deleteById(filterId);
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
        return Page.DELETE_FILTER;
    }
}
