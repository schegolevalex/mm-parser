package com.schegolevalex.mm.mmparser.bot.state;

import com.schegolevalex.mm.mmparser.bot.ParserBot;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class NewState extends AbstractState {

    public NewState(@Lazy ParserBot bot) {
        super(bot);
    }

    @Override
    public void route(Update update) {
        context.putState(AbilityUtils.getChatId(update), BotState.MAIN_PAGE_ACTION);
    }

    @Override
    public void reply(Update update) {
    }

    @Override
    public BotState getType() {
        return BotState.NEW_STATE;
    }
}
