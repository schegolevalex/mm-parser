package com.schegolevalex.mm.mmparser.bot;

import com.schegolevalex.mm.mmparser.config.BotConfiguration;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.description.SetMyDescription;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class ParserBot extends AbilityBot {

    private final Long creatorId;
    private final ResponseHandler responseHandler;

    @Autowired
    public ParserBot(BotConfiguration botConfiguration, ResponseHandler responseHandler) {
        super(botConfiguration.getBotToken(), botConfiguration.getBotUsername());
        this.creatorId = botConfiguration.getCreatorId();
        this.responseHandler = responseHandler;
    }

    public Ability start() {
        db.clear();
        return Ability.builder()
                .name("start")
                .info(Constant.Info.START)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> responseHandler.replyToStart(ctx.update()))
                .build();
    }

    public Reply replyToButtons() {
        BiConsumer<BaseAbilityBot, Update> action = (bot, upd) -> responseHandler.replyToButtons(upd);
        return Reply.of(action, Flag.TEXT, upd -> responseHandler.userIsActive(getChatId(upd)));
    }

    @Override
    public long creatorId() {
        return this.creatorId;
    }

    @PostConstruct
    @SneakyThrows
    private void setMyCommands() {
        List<BotCommand> botCommands = new ArrayList<>();
        botCommands.add(new BotCommand("start", Constant.Info.START));
        execute(new SetMyCommands(botCommands, null, null));
    }

    @PostConstruct
    @SneakyThrows
    private void setStartDescription() {
        execute(new SetMyDescription(Constant.Info.BOT_DESCRIPTION, "ru"));
    }
}
