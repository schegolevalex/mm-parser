package com.schegolevalex.mm.mmparser.bot;

import com.schegolevalex.mm.mmparser.entity.Link;
import com.schegolevalex.mm.mmparser.entity.Offer;
import com.schegolevalex.mm.mmparser.parser.Parser;
import com.schegolevalex.mm.mmparser.repository.LinkRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.schegolevalex.mm.mmparser.bot.Constant.Button;
import static com.schegolevalex.mm.mmparser.bot.Constant.Message;

@Component
@Slf4j
public class ResponseHandler {
    private final SilentSender silent;
    private final LinkRepository linkRepository;
    private final Context context;
    private final Parser parser;

    public ResponseHandler(@Lazy SilentSender silent,
                           LinkRepository linkRepository,
                           Context context,
                           Parser parser) {
        this.silent = silent;
        this.linkRepository = linkRepository;
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
                    UserState.AWAITING_BEGIN_CONVERSATION);
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
            List<Link> links = linkRepository.findAllByChatId(chatId);

            StringBuilder text = new StringBuilder();
            AtomicInteger num = new AtomicInteger(1);

            if (links.isEmpty())
                text.append(Message.LINKS_IS_EMPTY);
            else
                links.stream()
                        .sorted((link1, link2) -> link2.getCreatedAt().compareTo(link1.getCreatedAt()))
                        .forEach(link -> text.append(num.getAndIncrement())
                                .append(". ")
                                .append(link.getTitle())
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

        if (update.getMessage().getText().equalsIgnoreCase(Button.BACK)) {
            context.popState(chatId);
            sendMessageAndPutState(chatId,
                    Message.CHOOSE_MAIN_PAGE_ACTION,
                    Keyboard.withMainPageActions(),
                    UserState.AWAITING_MAIN_PAGE_ACTION);
        } else if (update.getMessage().hasText()
                && update.getMessage().getText().matches(".*(http(s)?://.)?(www\\.)?[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_+.~#?&/=]*)")) {
            String url = update.getMessage().getText();
            Link link = linkRepository.saveAndFlush(Link.builder()
                    .url(url)
                    .chatId(chatId)
                    .build());
            List<Offer> offers = parser.parseLink(link);
            sendMessageAndPutState(chatId,
                    Message.LINK_IS_ACCEPTED,
                    Keyboard.withMainPageActions(),
                    UserState.AWAITING_MAIN_PAGE_ACTION);
            sendOffers(offers, chatId);
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
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(Message.WRONG_INPUT);
        silent.execute(sendMessage);
    }

    private void stopChat(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("👋");
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        silent.execute(sendMessage);
    }

    public boolean userIsActive(Long chatId) {
        return context.userIsActive(chatId);
    }

    private void sendOffers(List<Offer> offers, Long chatId) {
        if (!offers.isEmpty())
            offers.forEach(offer -> silent.send(offer.toString(), chatId));
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

    @Scheduled(cron = "0 */5 * * * *", zone = "Europe/Moscow")
    private void parseAndNotify() {
        linkRepository.findAll().forEach(productLink -> {
            List<Offer> offers = parser.parseLink(productLink);
            sendOffers(offers, productLink.getChatId());
        });
    }
}