package com.schegolevalex.mm.mmparser.bot;

import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.abilitybots.api.util.AbilityUtils;

public class AddLinkReplyFlow extends CustomReplyFlow {
    public AddLinkReplyFlow(DBContext db) {
        super(db);
    }

    @Override
    public Reply reply() {
        return Reply.of((baseAbilityBot, update) -> {
            Long chatId = AbilityUtils.getChatId(update);
            baseAbilityBot.silent().send(Constant.Message.SUGGESTION_TO_LINK_INPUT, chatId);
        }, Flag.TEXT);
    }
}
