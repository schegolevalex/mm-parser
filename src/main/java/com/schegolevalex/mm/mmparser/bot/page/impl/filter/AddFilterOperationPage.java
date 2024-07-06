package com.schegolevalex.mm.mmparser.bot.page.impl.filter;

import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.page.base.BasePage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Filter;
import com.schegolevalex.mm.mmparser.entity.Operation;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.schegolevalex.mm.mmparser.bot.Constant.Button;
import static com.schegolevalex.mm.mmparser.bot.Constant.Message;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class AddFilterOperationPage extends BasePage {

    public AddFilterOperationPage(@Lazy ParserBot bot) {
        super(bot);
    }

    @Override
    public void beforeUpdateReceive(Update prevUpdate) {
        bot.getSilent().execute(SendMessage.builder()
                .chatId(getChatId(prevUpdate))
                .text(Message.ADD_FILTER_OPERATION)
                .replyMarkup(Keyboard.withFilterOperations())
                .build());
    }

    @Override
    public void afterUpdateReceive(Update nextUpdate) {
        Long chatId = getChatId(nextUpdate);
        String text = nextUpdate.getMessage().getText();

        Filter filter = context.getFilter(chatId);

        switch (text) {
            case Button.LESS_OR_EQUALS -> {
                filter.setOperation(Operation.LESS_OR_EQUALS);
                context.putPage(chatId, Page.ADD_FILTER_VALUE);
            }
            case Button.EQUALS -> {
                filter.setOperation(Operation.EQUALS);
                context.putPage(chatId, Page.ADD_FILTER_VALUE);
            }
            case Button.GREATER_OR_EQUALS -> {
                filter.setOperation(Operation.GREATER_OR_EQUALS);
                context.putPage(chatId, Page.ADD_FILTER_VALUE);
            }
            case Button.BACK -> context.popPage(chatId);
            default -> context.putPage(chatId, Page.UNEXPECTED);
        }
    }

    @Override
    public Page getPage() {
        return Page.ADD_FILTER_OPERATION;
    }
}
