package com.schegolevalex.mm.mmparser.bot.page.impl.filter;

import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.handler.Handler;
import com.schegolevalex.mm.mmparser.bot.page.base.HandlersPage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Filter;
import com.schegolevalex.mm.mmparser.service.FilterService;
import com.schegolevalex.mm.mmparser.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static com.schegolevalex.mm.mmparser.bot.Constant.Message;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class AddFilterValuePage extends HandlersPage {

    private final FilterService filterService;
    private final UserService userService;

    public AddFilterValuePage(ParserBot bot,
                              List<Handler> handlers,
                              FilterService filterService,
                              UserService userService) {
        super(bot, handlers);
        this.filterService = filterService;
        this.userService = userService;
    }

    @Override
    public void show(Update prevUpdate) {
        bot.getSilent().execute(SendMessage.builder()
                .chatId(getChatId(prevUpdate))
                .text(Message.ADD_FILTER_VALUE)
                .replyMarkup(Keyboard.withBackToFiltersOperationsButton())
                .build());
    }

    @Override
    public void afterUpdateReceived(Update nextUpdate) {
        Long chatId = getChatId(nextUpdate);
        if (nextUpdate.hasMessage() && nextUpdate.getMessage().hasText()) {
            Filter filter = context.getFilter(chatId);
            try {
                int value = Integer.parseInt(nextUpdate.getMessage().getText());
                filter.setValue(value);
                filter.setUser(userService.findByChatId(chatId).orElseThrow(() -> new RuntimeException("User not found")));
                filterService.save(filter);

                bot.getSilent().execute(SendMessage.builder()
                        .chatId(chatId)
                        .text(Message.FILTER_ADDED)
                        .build());

                context.putPage(chatId, Page.FILTERS_SETTINGS);
            } catch (NumberFormatException e) {
                super.afterUpdateReceived(nextUpdate);
            }
        } else
            super.afterUpdateReceived(nextUpdate);
    }

    @Override
    public Page getPage() {
        return Page.ADD_FILTER_VALUE;
    }
}
