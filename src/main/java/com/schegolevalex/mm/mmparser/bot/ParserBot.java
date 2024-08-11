package com.schegolevalex.mm.mmparser.bot;

import com.schegolevalex.mm.mmparser.config.BotConfiguration;
import com.schegolevalex.mm.mmparser.entity.Notification;
import com.schegolevalex.mm.mmparser.entity.User;
import com.schegolevalex.mm.mmparser.service.OfferService;
import com.schegolevalex.mm.mmparser.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.abilitybots.api.objects.Flag;
import org.telegram.telegrambots.abilitybots.api.objects.Reply;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.description.SetMyDescription;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import static org.telegram.telegrambots.abilitybots.api.objects.Locality.ALL;
import static org.telegram.telegrambots.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@Transactional
@Slf4j
public class ParserBot extends AbilityBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final BotConfiguration botConfiguration;
    @Getter
    private final Context context;
    private final OfferService offerService;
    private final UserService userService;

    @Autowired
    public ParserBot(BotConfiguration botConfiguration,
                     @Lazy Context context,
                     OfferService offerService,
                     UserService userService) {
        super(new OkHttpTelegramClient(botConfiguration.getBotToken()), botConfiguration.getBotUsername());
        this.botConfiguration = botConfiguration;
        this.context = context;
        this.offerService = offerService;
        this.userService = userService;
    }

    public Ability start() {
        return Ability.builder()
                .name("start")
                .info(Constant.Info.START)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> {
                    Long chatId = getChatId(ctx.update());
                    this.context.clearPages(chatId);
                    this.context.clearPromo(chatId);

                    Optional<User> maybeUser = userService.findByChatId(chatId);
                    if (maybeUser.isEmpty()) {
                        User user = User.builder()
                                .chatId(chatId)
                                .nickname((ctx.user().getUserName() != null) ? ctx.user().getUserName() : null)
                                .firstName(ctx.user().getFirstName())
                                .lastName((ctx.user().getLastName() != null) ? ctx.user().getLastName() : null)
                                .isPremium((ctx.user().getIsPremium() != null) ? ctx.user().getIsPremium() : false)
                                .build();
                        userService.save(user);
                        log.info("Присоединился новый пользователь: {}", user);
                    } else
                        maybeUser.get().setActive(true);
                    this.context.peekPage(chatId).afterUpdateReceived(ctx.update());
                    this.context.peekPage(chatId).show(ctx.update());
                })
                .build();
    }

    public Ability stop() {
        return Ability.builder()
                .name("stop")
                .info(Constant.Info.STOP)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> {
                    Long chatId = getChatId(ctx.update());
                    context.clear(chatId);
                    userService.findByChatId(chatId).ifPresent(user -> user.setActive(false));
                    silent.execute(SendMessage.builder()
                            .chatId(chatId)
                            .text(Constant.Message.BYE)
                            .replyMarkup(new ReplyKeyboardRemove(true))
                            .build());
                })
                .build();
    }

    public Ability help() {
        return Ability.builder()
                .name("help")
                .info(Constant.Info.HELP)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> {
                    Long chatId = getChatId(ctx.update());
                    silent.execute(SendMessage.builder()
                            .chatId(chatId)
                            .text(Constant.Message.HELP)
                            .build());
                })
                .build();
    }

    public Reply replyToButtons() {
        return Reply.of((_, upd) -> {
                    context.peekPage(getChatId(upd)).afterUpdateReceived(upd);
                    context.peekPage(getChatId(upd)).show(upd);
                },
                Flag.TEXT
                        .and(Predicate.not(hasMessageWith("/stop")))
                        .and(Predicate.not(hasMessageWith("/help")))
                        .or(Flag.CALLBACK_QUERY),
                update -> context.isActiveUser(getChatId(update)));
    }

    private Predicate<Update> hasMessageWith(String text) {
        return update -> update.getMessage().getText().contains(text);
    }

    public void sendNotify(Notification notification) {
        silent.execute(SendMessage.builder()
                .chatId(notification.getUser().getChatId())
                .text(offerService.getOfferMessage(notification.getOffer()))
                .parseMode("MarkdownV2")
                .build());
    }

    public void sendNotifies(List<Notification> notifications) {
        UUID parseId = notifications.stream().findAny().get().getOffer().getParseId();
        silent.execute(SendMessage.builder()
                .chatId(notifications.stream().findAny().get().getUser().getChatId())
                .text(offerService.getOfferMessage(notifications.stream().findFirst().get().getOffer()))
                .replyMarkup(Keyboard.withNotifications(parseId, notifications.size(), 1))
                .parseMode("MarkdownV2")
                .build());
    }

    @Override
    public long creatorId() {
        return botConfiguration.getCreatorId();
    }

    @Override
    public String getBotToken() {
        return botConfiguration.getBotToken();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @PostConstruct
    public void afterRegistration() {
        this.onRegister();
    }

    @PostConstruct
    private void setMyCommands() {
        List<BotCommand> botCommands = new ArrayList<>();
        botCommands.add(new BotCommand("start", Constant.Info.START));
        botCommands.add(new BotCommand("stop", Constant.Info.STOP));
        botCommands.add(new BotCommand("help", Constant.Info.HELP));
        silent.execute(new SetMyCommands(botCommands));
    }

    @PostConstruct
    private void setStartDescription() {
        silent.execute(new SetMyDescription(Constant.Info.BOT_DESCRIPTION, "ru"));
    }

    @PreDestroy
    private void clearDb() {
//        db.clear();
    }
}