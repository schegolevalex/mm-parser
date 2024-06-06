package com.schegolevalex.mm.mmparser.bot;

import com.schegolevalex.mm.mmparser.bot.state.BotState;
import com.schegolevalex.mm.mmparser.config.BotConfiguration;
import com.schegolevalex.mm.mmparser.entity.Offer;
import com.schegolevalex.mm.mmparser.parser.Parser;
import com.schegolevalex.mm.mmparser.service.OfferService;
import com.schegolevalex.mm.mmparser.service.ProductService;
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
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.description.SetMyDescription;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
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

    @Autowired
    public ParserBot(BotConfiguration botConfiguration,
                     Context context,
                     Parser parser,
                     ProductService productService,
                     OfferService offerService) {
        super(new OkHttpTelegramClient(botConfiguration.getBotToken()), botConfiguration.getBotUsername());
        this.botConfiguration = botConfiguration;
        this.context = context;
        this.parser = parser;
        this.productService = productService;
        this.offerService = offerService;
    }

    public Ability start() {
        return Ability.builder()
                .name("start")
                .info(Constant.Info.START)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> sendMessageAndPutState(getChatId(ctx.update()),
                        Constant.Message.CHOOSE_MAIN_PAGE_ACTION,
                        Keyboard.withMainPageActions(),
                        BotState.MAIN_PAGE_ACTION))
                .build();
    }

    public Ability stop() {
        return Ability.builder()
                .name("stop")
                .info(Constant.Info.STOP)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> silent.execute(SendMessage.builder()
                        .chatId(getChatId(ctx.update()))
                        .text(Constant.Message.BYE)
                        .replyMarkup(new ReplyKeyboardRemove(true))
                        .build()))
                .build();
    }

    public Reply replyToButtons() {
        return Reply.of((bot, upd) -> context.peekState(getChatId(upd)).reply(upd),
                Flag.TEXT,
                update -> context.isActiveUser(getChatId(update)),
                update -> !update.getMessage().getText().startsWith("/"));
    }

    public void unexpectedMessage(long chatId) {
        silent.execute(SendMessage.builder()
                .chatId(chatId)
                .text(Constant.Message.WRONG_INPUT)
                .build());
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

    public void sendMessageAndPutState(Long chatId, String message, ReplyKeyboard keyboard, BotState botState) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(message)
                .build();
        if (keyboard != null) sendMessage.setReplyMarkup(keyboard);
        silent.execute(sendMessage);
        context.putState(chatId, botState);
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
                sendNotifies(filteredOffers, product.getChatId());
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
        silent.execute(new SetMyCommands(botCommands, null, null));
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
