package com.schegolevalex.mm.mmparser.bot;

import com.schegolevalex.mm.mmparser.config.BotConfiguration;
import com.schegolevalex.mm.mmparser.entity.Offer;
import com.schegolevalex.mm.mmparser.entity.User;
import com.schegolevalex.mm.mmparser.parser.Parser;
import com.schegolevalex.mm.mmparser.service.OfferService;
import com.schegolevalex.mm.mmparser.service.ProductService;
import com.schegolevalex.mm.mmparser.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.util.ArrayList;
import java.util.List;

import static org.telegram.telegrambots.abilitybots.api.objects.Locality.ALL;
import static org.telegram.telegrambots.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@Transactional
public class ParserBot extends AbilityBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final BotConfiguration botConfiguration;
    @Getter
    private final Context context;
    private final Parser parser;
    private final ProductService productService;
    private final OfferService offerService;
    private final UserService userService;

    @Autowired
    public ParserBot(BotConfiguration botConfiguration,
                     Context context,
                     Parser parser,
                     ProductService productService,
                     OfferService offerService, UserService userService) {
        super(new OkHttpTelegramClient(botConfiguration.getBotToken()), botConfiguration.getBotUsername());
        this.botConfiguration = botConfiguration;
        this.context = context;
        this.parser = parser;
        this.productService = productService;
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

                    if (userService.findByChatId(chatId).isEmpty()) {
                        userService.save(User.builder()
                                .chatId(chatId)
                                .nickname((ctx.user().getUserName() != null) ? ctx.user().getUserName() : null)
                                .firstName(ctx.user().getFirstName())
                                .lastName((ctx.user().getLastName() != null) ? ctx.user().getLastName() : null)
                                .isPremium((ctx.user().getIsPremium() != null) ? ctx.user().getIsPremium() : false)
                                .build());
                    }
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
                    context.clearPages(chatId);
                    context.clearPromo(chatId);
                    silent.execute(SendMessage.builder()
                            .chatId(chatId)
                            .text(Constant.Message.BYE)
                            .replyMarkup(new ReplyKeyboardRemove(true))
                            .build());
                })
                .build();
    }

    public Reply replyToButtons() {
        return Reply.of((bot, upd) -> {
                    context.peekPage(getChatId(upd)).afterUpdateReceive(upd);
                    context.peekPage(getChatId(upd)).beforeUpdateReceive(upd);
                },
                Flag.TEXT.or(Flag.CALLBACK_QUERY),
                update -> context.isActiveUser(getChatId(update))/*,
                update -> (!update.getMessage().getText().startsWith("/stop"))*/); // todo если включить, то работает /stop, но не работают callback кнопок
    }

    private void sendNotifies(List<Offer> offers, Long chatId) {
        if (!offers.isEmpty())
            offers.forEach(offer -> {
                Integer priceBefore = offer.getPrice();
                double bonusPercent = offer.getBonusPercent() / 100.0;
                int promo = priceBefore > 110_000 ? 20_000 : 10_000;
                double sberprime = priceBefore * bonusPercent > 2_000 ? 2_000 : (priceBefore * bonusPercent);
                int totalPrice = (int) Math.round(priceBefore - promo - (priceBefore - promo) * bonusPercent - sberprime);

                String message = String.format(Constant.Message.OFFER,
                        totalPrice,
                        offer.getSeller().getName(),
                        offer.getPrice(),
                        offer.getBonusPercent() + 2,
                        offer.getBonus(),
                        offer.getProduct().getUrl());
                silent.send(message, chatId);
            });
    }

    @Scheduled(cron = "0 */10 * * * *", zone = "Europe/Moscow")
    protected void parseAndNotify() {
        productService.findAllByIsActive(true).forEach(product -> {
            List<Offer> parsedOffers = parser.parseProduct(product);
            List<Offer> newOffers = parsedOffers.stream()
                    .filter(offer -> !offerService.isPresent(product, offer))
                    .toList();
            newOffers.forEach(product::addOffer);
            List<Offer> filteredOffers = offerService.filterOffersWithDefaultParameters(newOffers);
            if (!filteredOffers.isEmpty())
                sendNotifies(filteredOffers, product.getUser().getChatId());
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
