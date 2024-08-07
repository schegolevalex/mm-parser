package com.schegolevalex.mm.mmparser.bot.page.impl.product;

import com.github.sonus21.rqueue.core.RqueueMessageEnqueuer;
import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.handler.Handler;
import com.schegolevalex.mm.mmparser.bot.page.base.HandlersPage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.service.ProductService;
import com.schegolevalex.mm.mmparser.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.LinkPreviewOptions;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.schegolevalex.mm.mmparser.bot.Constant.Message;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class AddProductPage extends HandlersPage {
    private static final String MESSAGE_WITH_URL_REGEXP = ".*(http(s)?://)?(www\\.)?.+\\.[-a-zA-Z]+.*";
    private static final String VALID_MEGAMARKET_URL_REGEXP = "(http(s)?://)?(www\\.)?megamarket\\.ru/catalog/details/([-a-zA-Z0-9@:%_+.~#?&=]+)";
    private final ProductService productService;
    private final RqueueMessageEnqueuer rqueueMessageEnqueuer;
    private final UserService userService;
    @Value("${mm.rqueue.product-queue}")
    private String productQueue;

    public AddProductPage(ParserBot bot,
                          List<Handler> handlers,
                          ProductService productService,
                          RqueueMessageEnqueuer rqueueMessageEnqueuer, UserService userService) {
        super(bot, handlers);
        this.productService = productService;
        this.rqueueMessageEnqueuer = rqueueMessageEnqueuer;
        this.userService = userService;
    }

    @Override
    public void show(Update prevUpdate) {
        Long chatId = AbilityUtils.getChatId(prevUpdate);

        if (prevUpdate.hasMessage() && prevUpdate.getMessage().hasText()) {
            String userText = prevUpdate.getMessage().getText();
            if (userText.equalsIgnoreCase(Constant.Button.ADD_PRODUCT)) {
                bot.getSilent().execute(SendMessage.builder()
                        .chatId(getChatId(prevUpdate))
                        .text(Message.SUGGESTION_TO_LINK_INPUT)
                        .replyMarkup(Keyboard.withMainPageButton())
                        .build());
            } else if (userText.matches(MESSAGE_WITH_URL_REGEXP)) {
                Pattern pattern = Pattern.compile(VALID_MEGAMARKET_URL_REGEXP);
                Matcher matcher = pattern.matcher(userText);
                if (matcher.find()) {
                    String productUrl = matcher.group();

                    if (!productUrl.endsWith("/"))
                        productUrl += "/";

                    Product savedProduct = productService.save(Product.builder()
                            .url(productUrl)
                            .user(userService.findByChatId(chatId)
                                    .orElseThrow(() -> new RuntimeException("User not found")))
                            .build());

                    rqueueMessageEnqueuer.enqueue(productQueue, savedProduct.getId());

                    bot.getSilent().execute(SendMessage.builder()
                            .chatId(chatId)
                            .replyMarkup(Keyboard.withMainPageButton())
                            .text(Message.LINK_IS_ACCEPTED)
                            .build());
                } else {
                    bot.getSilent().execute(SendMessage.builder()
                            .chatId(chatId)
                            .replyMarkup(Keyboard.withMainPageButton())
                            .text(Message.LINK_IS_NOT_VALID)
                            .parseMode("MarkdownV2")
                            .linkPreviewOptions(LinkPreviewOptions.builder()
                                    .isDisabled(true)
                                    .build())
                            .build());
                }
            } else {
                context.putPage(chatId, Page.UNEXPECTED);
            }
        }
    }

    @Override
    public Page getPage() {
        return Page.ADD_PRODUCT;
    }
}