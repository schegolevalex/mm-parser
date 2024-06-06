package com.schegolevalex.mm.mmparser.bot.state;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class WatchLinksState extends AbstractState {
    public WatchLinksState(@Lazy ParserBot bot) {
        super(bot);
    }

    @Override
    public void reply(Update update) {
        Long chatId = getChatId(update);

        if (update.getMessage().getText().equalsIgnoreCase(Constant.Button.BACK)) {
            bot.getContext().popState(chatId);
            bot.sendMessageAndPutState(chatId,
                    Constant.Message.CHOOSE_MAIN_PAGE_ACTION,
                    Keyboard.withMainPageActions(),
                    BotState.MAIN_PAGE_ACTION);
        } else
            bot.unexpectedMessage(chatId);
    }

    @Override
    public BotState getType() {
        return BotState.WATCH_LINKS;
    }
}