package com.schegolevalex.mm.mmparser.bot.page.impl.filter;

import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.page.base.BasePage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Filter;
import com.schegolevalex.mm.mmparser.service.FilterService;
import com.schegolevalex.mm.mmparser.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.schegolevalex.mm.mmparser.bot.Constant.Button;
import static com.schegolevalex.mm.mmparser.bot.Constant.Message;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class AddFilterValuePage extends BasePage {

    private final FilterService filterService;
    private final UserService userService;

    public AddFilterValuePage(@Lazy ParserBot bot, FilterService filterService, UserService userService) {
        super(bot);
        this.filterService = filterService;
        this.userService = userService;
    }

    @Override
    public void beforeUpdateReceive(Update prevUpdate) {
        bot.getSilent().execute(SendMessage.builder()
                .chatId(getChatId(prevUpdate))
                .text(Message.ADD_FILTER_VALUE)
                .replyMarkup(Keyboard.withBackButton())
                .build());
    }

    @Override
    public void afterUpdateReceive(Update nextUpdate) {
        Long chatId = getChatId(nextUpdate);
        String text = nextUpdate.getMessage().getText();

        Filter filter = context.getFilter(chatId);

        try {
            int value = Integer.parseInt(text);
            filter.setValue(value);
            filter.setUser(userService.findByChatId(chatId).orElseThrow(() -> new RuntimeException("User not found")));
            filterService.save(filter);

            bot.getSilent().execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(Message.FILTER_ADDED)
                    .build());

            context.putPage(chatId, Page.FILTERS_SETTINGS);
        } catch (NumberFormatException e) {
            switch (text) {
                case Button.BACK -> context.popPage(chatId);
                default -> context.putPage(chatId, Page.UNEXPECTED);
            }
        }
    }

    @Override
    public Page getPage() {
        return Page.ADD_FILTER_VALUE;
    }
}
