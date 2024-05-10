package com.schegolevalex.mm.mmparser.bot;

import lombok.AllArgsConstructor;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.objects.Reply;

@AllArgsConstructor
public abstract class CustomReplyFlow {
    DBContext db;

    public abstract Reply reply();
}
