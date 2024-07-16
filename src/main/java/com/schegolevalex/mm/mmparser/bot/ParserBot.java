package com.schegolevalex.mm.mmparser.bot;

import com.schegolevalex.mm.mmparser.config.BotConfiguration;
import com.schegolevalex.mm.mmparser.entity.Notify;
import com.schegolevalex.mm.mmparser.entity.Offer;
import com.schegolevalex.mm.mmparser.entity.User;
import com.schegolevalex.mm.mmparser.service.NotifyService;
import com.schegolevalex.mm.mmparser.service.OfferService;
import com.schegolevalex.mm.mmparser.service.ProductService;
import com.schegolevalex.mm.mmparser.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
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

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private final ProductService productService;
    private final OfferService offerService;
    private final UserService userService;
    private final NotifyService notifyService;

    @Autowired
    public ParserBot(BotConfiguration botConfiguration,
                     Context context,
                     ProductService productService,
                     OfferService offerService, UserService userService, NotifyService notifyService) {
        super(new OkHttpTelegramClient(botConfiguration.getBotToken()), botConfiguration.getBotUsername());
        this.botConfiguration = botConfiguration;
        this.context = context;
        this.productService = productService;
        this.offerService = offerService;
        this.userService = userService;
        this.notifyService = notifyService;
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
                    this.context.peekPage(chatId).afterUpdateReceive(ctx.update());
                    this.context.peekPage(chatId).beforeUpdateReceive(ctx.update());
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
                    context.peekPage(getChatId(upd)).afterUpdateReceive(upd);
                    context.peekPage(getChatId(upd)).beforeUpdateReceive(upd);
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

    private void sendNotifies(List<Notify> notifies) {
        if (!notifies.isEmpty())
            notifies.forEach(notify ->
                    silent.execute(SendMessage.builder()
                            .chatId(notify.getUser().getChatId())
                            .text(offerService.getOfferMessage(notify.getOffer()))
                            .parseMode("MarkdownV2")
                            .build()));
    }

    @Async
    @Scheduled(cron = "*/30 * * * * *", zone = "Europe/Moscow")
    protected void notifyJob() {
        log.info("Запуск процесса уведомления пользователей");
        productService.findAllNotDeletedAndUserIsActive().forEach(product -> {
            List<Offer> parsedOffers = offerService.findAllForSpecifiedTime(product, 1, ChronoUnit.MINUTES);
            List<Offer> filteredOffers = offerService.filterOffers(parsedOffers);
            List<Notify> notifies = filteredOffers.stream()
                    .map(offer -> {
                        Notify notify = new Notify();
                        notify.setOffer(offer);
                        notify.setUser(product.getUser());
                        notify.addFilters(product.getFilters());
                        notify.setPromo(product.getPromo());
                        notify.setCashbackLevel(product.getUser().getCashbackLevel());
                        return notify;
                    })
                    .filter(Predicate.not(notifyService::isPresent))
                    .toList();

            if (!notifies.isEmpty()) {
                notifyService.saveAll(notifies);
                sendNotifies(notifies);
            }
        });
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
        db.clear();
    }
}
