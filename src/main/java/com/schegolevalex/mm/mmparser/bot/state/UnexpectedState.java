package com.schegolevalex.mm.mmparser.bot.state;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class UnexpectedState extends BaseState {
    public UnexpectedState(@Lazy ParserBot bot) {
        super(bot);
    }

    @Override
    public void route(Update update) {
        context.popState(getChatId(update));
    }

    @Override
    public void reply(Update update) {
        if (!update.hasCallbackQuery()) {
            bot.getSilent().execute(DeleteMessage.builder()
                    .chatId(getChatId(update))
                    .messageId(update.getMessage().getMessageId())
                    .build());
        }
//        bot.getSilent().execute(SendMessage.builder()
//                .chatId(getChatId(update))
//                .text(Constant.Message.UNEXPECTED_INPUT)
//                .build());
        this.route(update);
    }

    @Override
    public BotState getType() {
        return BotState.UNEXPECTED;
    }
}
