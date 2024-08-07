package com.schegolevalex.mm.mmparser.bot.handler;

import com.schegolevalex.mm.mmparser.bot.Context;

public abstract class BaseHandler implements Handler {
    protected final Context context;

    public BaseHandler(Context context) {
        this.context = context;
    }
}