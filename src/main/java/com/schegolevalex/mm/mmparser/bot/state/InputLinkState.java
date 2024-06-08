package com.schegolevalex.mm.mmparser.bot.state;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.service.ProductService;
import com.schegolevalex.mm.mmparser.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class InputLinkState extends BaseState {
    private static final String MESSAGE_WITH_URL_REGEXP = ".*(http(s)?://)?(www\\.)?megamarket\\.ru\\b([-a-zA-Z0-9@:%_+.~#?&/=]*)";
    private static final String URL_REGEXP = "(http(s)?://)?(www\\.)?megamarket\\.ru\\b([-a-zA-Z0-9@:%_+.~#?&/=]*)";
    private final ProductService productService;
    private final UserService userService;

    public InputLinkState(@Lazy ParserBot bot, ProductService productService, UserService userService) {
        super(bot);
        this.productService = productService;
        this.userService = userService;
    }

    @Override
    public void route(Update update) {
        Long chatId = getChatId(update);
        String userText = update.getMessage().getText();

        if (userText.equalsIgnoreCase(Constant.Button.MAIN_PAGE)) {
            context.putState(chatId, BotState.MAIN_PAGE_ACTION);
        } else if (userText.matches(MESSAGE_WITH_URL_REGEXP)) {
            context.putState(chatId, BotState.INPUT_LINK);
        } else
            context.putState(chatId, BotState.UNEXPECTED);
    }

    @Override
    @Transactional
    public void reply(Update update) {
        Long chatId = AbilityUtils.getChatId(update);
        String userText = update.getMessage().getText();

        Pattern pattern = Pattern.compile(URL_REGEXP);
        Matcher matcher = pattern.matcher(userText);
        if (matcher.find()) {
            String productUrl = matcher.group();

            if (!productUrl.endsWith("/"))
                productUrl += "/";

            productService.save(Product.builder()
                    .url(productUrl)
                    .user(userService.findByChatId(chatId)
                            .orElseThrow(() -> new RuntimeException("User not found")))
                    .build());

            bot.getSilent().execute(SendMessage.builder()
                    .chatId(chatId)
                    .replyMarkup(Keyboard.withMainPageButton())
                    .text(Constant.Message.LINK_IS_ACCEPTED)
                    .build());
        }
    }

    @Override
    public BotState getType() {
        return BotState.INPUT_LINK;
    }
}
