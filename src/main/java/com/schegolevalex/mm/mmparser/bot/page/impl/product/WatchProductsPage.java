package com.schegolevalex.mm.mmparser.bot.page.impl.product;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.handler.Handler;
import com.schegolevalex.mm.mmparser.bot.page.base.HandlersPage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.service.ProductService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.LinkPreviewOptions;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.schegolevalex.mm.mmparser.bot.Constant.DELIMITER;
import static com.schegolevalex.mm.mmparser.bot.Constant.Message;
import static com.schegolevalex.mm.mmparser.bot.util.MessageUtil.prepareToMarkdownV2;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@Transactional
public class WatchProductsPage extends HandlersPage {

    private final ProductService productService;

    public WatchProductsPage(ParserBot bot,
                             List<Handler> handlers,
                             ProductService productService) {
        super(bot, handlers);
        this.productService = productService;
    }

    @Override
    public void show(Update prevUpdate) {
        Long chatId = getChatId(prevUpdate);

        if (prevUpdate.hasCallbackQuery()) {
            long productId = Long.parseLong(prevUpdate.getCallbackQuery().getData().split(DELIMITER)[1]);
            Product product = productService.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

            bot.getSilent().execute(EditMessageReplyMarkup.builder()
                    .chatId(chatId)
                    .messageId(prevUpdate.getCallbackQuery().getMessage().getMessageId())
                    .replyMarkup(Keyboard.withProduct(product.getId()))
                    .build());
        } else {
            List<Product> products = productService.findAllByChatIdNotDeletedAndUserIsActive(chatId);

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
                                .text(num.getAndIncrement() + "\\. " + (product.getTitle() != null ?
                                        String.format(Message.PRODUCT_TITLE_WITH_LINK,
                                                prepareToMarkdownV2(product.getTitle()),
                                                prepareToMarkdownV2(product.getUrl()))
                                        : Constant.Message.NO_TITLE))
                                .replyMarkup(Keyboard.withProduct(product.getId()))
                                .parseMode("MarkdownV2")
                                .linkPreviewOptions(LinkPreviewOptions.builder()
                                        .showAboveText(true)
                                        .isDisabled(false)
                                        .build())
                                .build()));
            }
        }
    }

    @Override
    public Page getPage() {
        return Page.WATCH_PRODUCTS;
    }
}