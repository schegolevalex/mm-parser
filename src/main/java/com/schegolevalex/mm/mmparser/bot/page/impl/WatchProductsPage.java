package com.schegolevalex.mm.mmparser.bot.page.impl;

import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.page.base.MainKeyboardPage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.service.ProductService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.LinkPreviewOptions;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.schegolevalex.mm.mmparser.bot.Constant.*;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@Transactional
public class WatchProductsPage extends MainKeyboardPage {
    private final ProductService productService;

    public WatchProductsPage(@Lazy ParserBot bot, ProductService productService) {
        super(bot);
        this.productService = productService;
    }

    @Override
    public void beforeUpdateReceive(Update prevUpdate) {
        Long chatId = getChatId(prevUpdate);

        if (prevUpdate.hasCallbackQuery() && prevUpdate.getCallbackQuery().getData().startsWith(Callback.BACK)) {
            long productId = Long.parseLong(prevUpdate.getCallbackQuery().getData().split(DELIMITER)[1]);
            Product product = productService.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

            bot.getSilent().execute(EditMessageReplyMarkup.builder()
                    .chatId(chatId)
                    .messageId(prevUpdate.getCallbackQuery().getMessage().getMessageId())
                    .replyMarkup(Keyboard.withProduct(product.getId(), product.getUrl()))
                    .build());
        } else {
            List<Product> products = productService.findAllByChatIdAndIsActive(chatId, true);

            if (products.isEmpty()) {
                bot.getSilent().execute(SendMessage.builder()
                        .chatId(chatId)
                        .text(Message.PRODUCTS_IS_EMPTY)
                        .replyMarkup(Keyboard.withMainPageActions())
                        .build());
            } else {
                AtomicInteger num = new AtomicInteger(1);
                products.stream()
                        .sorted(Comparator.comparing(Product::getCreatedAt))
                        .forEach(product -> bot.getSilent().execute(SendMessage.builder()
                                .chatId(chatId)
                                .text(num.getAndIncrement() + ". " + product.getTitle())
                                .replyMarkup(Keyboard.withProduct(product.getId(), product.getUrl()))
                                .linkPreviewOptions(LinkPreviewOptions.builder()
                                        .isDisabled(true)
                                        .build())
                                .build()));
            }
        }
    }

    @Override
    public void afterUpdateReceive(Update nextUpdate) {
        Long chatId = getChatId(nextUpdate);

        if (nextUpdate.hasCallbackQuery()) {
            String callbackData = nextUpdate.getCallbackQuery().getData();
            if (callbackData.startsWith(Callback.PRODUCT_NOTIFICATIONS))
                context.putPage(chatId, Page.PRODUCT_NOTIFICATIONS);
            else if (callbackData.startsWith(Callback.PRODUCT_SETTINGS))
                context.putPage(chatId, Page.PRODUCT_SETTINGS);
            else if (callbackData.startsWith(Callback.PRODUCT_DELETE))
                context.putPage(chatId, Page.DELETE_PRODUCT);
            else if (callbackData.startsWith(Callback.BACK))
                context.putPage(chatId, Page.WATCH_PRODUCTS);
            else
                context.putPage(chatId, Page.UNEXPECTED);
        } else
            resolveMainKeyboard(nextUpdate);
    }

    @Override
    public Page getPage() {
        return Page.WATCH_PRODUCTS;
    }
}