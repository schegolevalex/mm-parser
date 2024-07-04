package com.schegolevalex.mm.mmparser.bot.page.impl;

import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.page.base.BasePage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
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

import static com.schegolevalex.mm.mmparser.bot.Constant.Button;
import static com.schegolevalex.mm.mmparser.bot.Constant.Message;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@Transactional
public class AddProductPage extends BasePage {
    private static final String MESSAGE_WITH_URL_REGEXP = ".*(http(s)?://)?(www\\.)?megamarket\\.ru\\b([-a-zA-Z0-9@:%_+.~#?&/=]*)";
    private static final String URL_REGEXP = "(http(s)?://)?(www\\.)?megamarket\\.ru\\b([-a-zA-Z0-9@:%_+.~#?&/=]*)";
    private final ProductService productService;
    private final UserService userService;

    public AddProductPage(@Lazy ParserBot bot, ProductService productService, UserService userService) {
        super(bot);
        this.productService = productService;
        this.userService = userService;
    }

    @Override
    public void beforeUpdateReceive(Update prevUpdate) {
        Long chatId = AbilityUtils.getChatId(prevUpdate);
        String userText = prevUpdate.getMessage().getText();

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
                    .text(Message.LINK_IS_ACCEPTED)
                    .build());
        }
    }

    @Override
    public void afterUpdateReceive(Update nextUpdate) {
        Long chatId = getChatId(nextUpdate);
        String userText = nextUpdate.getMessage().getText();

        if (userText.equalsIgnoreCase(Button.MAIN_PAGE)) {
            context.putPage(chatId, Page.MAIN);
        } else if (userText.matches(MESSAGE_WITH_URL_REGEXP)) {
            context.putPage(chatId, Page.ADD_PRODUCT);
        } else
            context.putPage(chatId, Page.UNEXPECTED);
    }

    @Override
    public Page getPage() {
        return Page.ADD_PRODUCT;
    }
}
