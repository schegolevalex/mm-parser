package com.schegolevalex.mm.mmparser.bot.handler.callback;

import com.schegolevalex.mm.mmparser.bot.Context;
import com.schegolevalex.mm.mmparser.bot.handler.BaseHandler;
import com.schegolevalex.mm.mmparser.entity.User;
import com.schegolevalex.mm.mmparser.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class CashbackLevelCallbackHandler extends BaseHandler {

    private final static String CASHBACK_LEVEL_REGEXP = "\\b(?:0|2|5|7|9|12)\\b";
    private final UserService userService;

    public CashbackLevelCallbackHandler(Context context, UserService userService) {
        super(context);
        this.userService = userService;
    }

    @Override
    public void handle(Update update) {
        User user = userService.findByChatId(getChatId(update))
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setCashbackLevel(Integer.parseInt(update.getCallbackQuery().getData()));
        userService.save(user);
    }

    @Override
    public boolean isSuitable(Update update) {
        return update.hasCallbackQuery()
               && update.getCallbackQuery().getData().matches(CASHBACK_LEVEL_REGEXP);
    }
}
