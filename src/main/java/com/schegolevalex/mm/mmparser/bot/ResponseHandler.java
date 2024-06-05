package com.schegolevalex.mm.mmparser.bot;

import com.schegolevalex.mm.mmparser.entity.Offer;
import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.parser.Parser;
import com.schegolevalex.mm.mmparser.service.OfferService;
import com.schegolevalex.mm.mmparser.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.schegolevalex.mm.mmparser.bot.Constant.Button;
import static com.schegolevalex.mm.mmparser.bot.Constant.Message;

@Component
@Slf4j
@Transactional
public class ResponseHandler {
    private final SilentSender silent;
    private final OfferService offerService;
    private final ProductService productService;
    private final Context context;
    private final Parser parser;

    public ResponseHandler(@Lazy SilentSender silent
            , OfferService offerService
            , ProductService productService
            , Context context
            , Parser parser) {
        this.silent = silent;
        this.offerService = offerService;
        this.productService = productService;
        this.context = context;
        this.parser = parser;
    }

    public void replyToStart(Update update) {
        Long chatId = AbilityUtils.getChatId(update);
        sendMessageAndPutState(chatId,
                Message.WELCOME,
                Keyboard.withBeginConversationButton(),
                UserState.AWAITING_BEGIN_CONVERSATION);
    }

    public void replyToButtons(Update update) {
        Long chatId = AbilityUtils.getChatId(update);

        if (update.getMessage().getText().equalsIgnoreCase("/start"))
            replyToStart(update);

        if (update.getMessage().getText().equalsIgnoreCase("/stop"))
            stopChat(chatId);

        switch (context.peekState(chatId)) {
            case AWAITING_BEGIN_CONVERSATION -> replyToBeginConversation(update);
            case AWAITING_MAIN_PAGE_ACTION -> replyToMainPageAction(update);
            case AWAITING_LINK_INPUT -> replyToLinkInput(update);
            case WATCH_LINKS -> replyToWatchLinks(update);
            default -> unexpectedMessage(chatId);
        }
    }

    private void replyToBeginConversation(Update update) {
        Long chatId = AbilityUtils.getChatId(update);

        if (update.getMessage().getText().equalsIgnoreCase(Button.START_CONVERSATION)) {
            sendMessageAndPutState(chatId,
                    Message.CHOOSE_MAIN_PAGE_ACTION,
                    Keyboard.withMainPageActions(),
                    UserState.AWAITING_MAIN_PAGE_ACTION);
        } else
            unexpectedMessage(chatId);
    }

    private void replyToMainPageAction(Update update) {
        Long chatId = AbilityUtils.getChatId(update);

        if (update.getMessage().getText().equalsIgnoreCase(Button.ADD_LINK)) {
            sendMessageAndPutState(chatId,
                    Message.SUGGESTION_TO_LINK_INPUT,
                    Keyboard.withBackButton(),
                    UserState.AWAITING_LINK_INPUT);
        } else if (update.getMessage().getText().equalsIgnoreCase(Button.MY_LINKS)) {
            List<Product> products = productService.findAllByChatId(chatId);

            StringBuilder text = new StringBuilder();
            AtomicInteger num = new AtomicInteger(1);

            if (products.isEmpty())
                text.append(Message.LINKS_IS_EMPTY);
            else
                products.stream()
                        .sorted((product1, product2) -> product2.getCreatedAt().compareTo(product1.getCreatedAt()))
                        .forEach(product -> text.append(num.getAndIncrement())
                                .append(". ")
                                .append(product.getTitle())
                                .append("\n"));

            sendMessageAndPutState(chatId,
                    String.valueOf(text),
                    Keyboard.withBackButton(),
                    UserState.WATCH_LINKS);
        } else
            unexpectedMessage(chatId);
    }

    private void replyToLinkInput(Update update) {
        Long chatId = AbilityUtils.getChatId(update);
        String messageWithUrlRegexp = ".*(http(s)?://.)?(www\\.)?[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_+.~#?&/=]*)";

        if (update.getMessage().getText().equalsIgnoreCase(Button.BACK)) {
            context.popState(chatId);
            sendMessageAndPutState(chatId,
                    Message.CHOOSE_MAIN_PAGE_ACTION,
                    Keyboard.withMainPageActions(),
                    UserState.AWAITING_MAIN_PAGE_ACTION);
        } else if (update.getMessage().hasText()
                && update.getMessage().getText().matches(messageWithUrlRegexp)) {
            String urlRegexp = "(http(s)?://.)?(www\\.)?[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_+.~#?&/=]*)";
            Pattern pattern = Pattern.compile(urlRegexp);
            String userText = update.getMessage().getText();
            Matcher matcher = pattern.matcher(userText);
            if (matcher.find()) {
                String productUrl = matcher.group();
                if (!productUrl.endsWith("/")) {
                    productUrl += "/";
                }
                productService.save(Product.builder()
                        .url(productUrl)
                        .chatId(chatId)
                        .build());
                sendMessageAndPutState(chatId,
                        Message.LINK_IS_ACCEPTED,
                        Keyboard.withMainPageActions(),
                        UserState.AWAITING_MAIN_PAGE_ACTION);
            } else
                unexpectedMessage(chatId);
        } else
            unexpectedMessage(chatId);
    }

    private void replyToWatchLinks(Update update) {
        Long chatId = AbilityUtils.getChatId(update);

        if (update.getMessage().getText().equalsIgnoreCase(Button.BACK)) {
            context.popState(chatId);
            sendMessageAndPutState(chatId,
                    Message.CHOOSE_MAIN_PAGE_ACTION,
                    Keyboard.withMainPageActions(),
                    UserState.AWAITING_MAIN_PAGE_ACTION);
        } else
            unexpectedMessage(chatId);
    }

    private void unexpectedMessage(long chatId) {
        silent.execute(SendMessage.builder()
                .chatId(chatId)
                .text(Message.WRONG_INPUT)
                .build());
    }

    private void stopChat(long chatId) {
        silent.execute(SendMessage.builder()
                .chatId(chatId)
                .text(Message.BYE)
                .replyMarkup(new ReplyKeyboardRemove(true))
                .build());
    }

    public boolean isActiveUser(Long chatId) {
        return context.isActiveUser(chatId);
    }

    protected void sendNotifies(List<Offer> offers, Long chatId) {
        if (!offers.isEmpty())
            offers.forEach(offer -> {
                Integer priceBefore = offer.getPrice();
                double bonusPercent = offer.getBonusPercent() / 100.0;
                int promo = priceBefore > 110_000 ? 20_000 : 10_000;
                double sberprime = priceBefore * bonusPercent > 2_000 ? 2_000 : (priceBefore * bonusPercent);
                int totalPrice = (int) Math.round(priceBefore - promo - (priceBefore - promo) * bonusPercent - sberprime);

                String message = String.format(Message.OFFER,
                        totalPrice,
                        offer.getSeller().getName(),
                        offer.getPrice(),
                        offer.getBonusPercent() + 2,
                        offer.getBonus(),
                        offer.getProduct().getUrl());
                silent.send(message, chatId);
            });
    }

    private void sendMessageAndPutState(Long chatId, String message, ReplyKeyboard keyboard, UserState userState) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(message)
                .build();
        if (keyboard != null) sendMessage.setReplyMarkup(keyboard);
        silent.execute(sendMessage);
        context.putState(chatId, userState);
    }

    @Scheduled(cron = "0 */1 * * * *", zone = "Europe/Moscow")
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
}