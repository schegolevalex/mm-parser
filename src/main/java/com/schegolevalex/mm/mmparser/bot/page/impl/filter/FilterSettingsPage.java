package com.schegolevalex.mm.mmparser.bot.page.impl.filter;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.page.base.FilterKeyboardPage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Filter;
import com.schegolevalex.mm.mmparser.service.FilterService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.schegolevalex.mm.mmparser.bot.Constant.DELIMITER;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class FilterSettingsPage extends FilterKeyboardPage {

    private final FilterService filterService;

    public FilterSettingsPage(@Lazy ParserBot bot, FilterService filterService) {
        super(bot);
        this.filterService = filterService;
    }

    @Override
    public void beforeUpdateReceive(Update prevUpdate) {
        Long chatId = getChatId(prevUpdate);

        if (prevUpdate.hasCallbackQuery()) {
            long filterId = Long.parseLong(prevUpdate.getCallbackQuery().getData().split(DELIMITER)[1]);
            bot.getSilent().execute(EditMessageReplyMarkup.builder()
                    .chatId(chatId)
                    .messageId(prevUpdate.getCallbackQuery().getMessage().getMessageId())
                    .replyMarkup(Keyboard.withDeleteFilterButton(filterId))
                    .build());
        } else {
            List<Filter> filters = filterService.findAllByChatId(chatId);

            if (filters.isEmpty()) {
                bot.getSilent().execute(SendMessage.builder()
                        .chatId(chatId)
                        .text(Constant.Message.FILTERS_IS_EMPTY)
                        .build());
            } else {
                AtomicInteger num = new AtomicInteger(1);
                filters.stream()
                        .sorted(Comparator.comparing(Filter::getCreatedAt))
                        .forEach(filter -> bot.getSilent().execute(SendMessage.builder()
                                .chatId(chatId)
                                .text(num.getAndIncrement() + ". " + filter)
                                .replyMarkup(Keyboard.withDeleteFilterButton(filter.getId()))
                                .build()));
            }
        }
        bot.getSilent().execute(SendMessage.builder()
                .chatId(getChatId(prevUpdate))
                .text(Constant.Message.CHOOSE_ACTION)
                .replyMarkup(Keyboard.withFiltersSettingsActions())
                .build());
    }

    @Override
    public void afterUpdateReceive(Update nextUpdate) {
        resolveFiltersKeyboard(nextUpdate);
    }

    @Override
    public Page getPage() {
        return Page.FILTERS_SETTINGS;
    }
}
