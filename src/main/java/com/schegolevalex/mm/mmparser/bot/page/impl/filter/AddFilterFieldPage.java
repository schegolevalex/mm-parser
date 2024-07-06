package com.schegolevalex.mm.mmparser.bot.page.impl.filter;

import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.page.base.MainKeyboardPage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Filter;
import com.schegolevalex.mm.mmparser.entity.FilterField;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.schegolevalex.mm.mmparser.bot.Constant.Button;
import static com.schegolevalex.mm.mmparser.bot.Constant.Message;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class AddFilterFieldPage extends MainKeyboardPage {

    public AddFilterFieldPage(@Lazy ParserBot bot) {
        super(bot);
    }

    @Override
    public void beforeUpdateReceive(Update prevUpdate) {
        bot.getSilent().execute(SendMessage.builder()
                .chatId(getChatId(prevUpdate))
                .text(Message.ADD_FILTER_FIELD)
                .replyMarkup(Keyboard.withFilterFields())
                .build());
    }

    @Override
    public void afterUpdateReceive(Update nextUpdate) {
        Long chatId = getChatId(nextUpdate);
        String text = nextUpdate.getMessage().getText();

        Filter filter = context.getFilter(chatId);

        switch (text) {
            case Button.PRICE -> {
                filter.setField(FilterField.PRICE);
                context.putPage(chatId, Page.ADD_FILTER_OPERATION);
            }
            case Button.BONUS -> {
                filter.setField(FilterField.BONUS);
                context.putPage(chatId, Page.ADD_FILTER_OPERATION);
            }
            case Button.BONUS_PERCENT -> {
                filter.setField(FilterField.BONUS_PERCENT);
                context.putPage(chatId, Page.ADD_FILTER_OPERATION);
            }
            case Button.BACK -> context.putPage(chatId, Page.FILTERS_SETTINGS);
            default -> context.putPage(chatId, Page.UNEXPECTED);
        }
    }

    @Override
    public Page getPage() {
        return Page.ADD_FILTER_FIELD;
    }
}
