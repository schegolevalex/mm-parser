package com.schegolevalex.mm.mmparser.bot.state;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.service.ProductService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.LinkPreviewOptions;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@Transactional
public class WatchProductsState extends BaseState {
    private final ProductService productService;

    public WatchProductsState(@Lazy ParserBot bot, ProductService productService) {
        super(bot);
        this.productService = productService;
    }

    @Override
    public void route(Update update) {
        Long chatId = getChatId(update);

        if (update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith(Constant.Button.PRODUCT_SETTINGS)) {
            context.putState(chatId, BotState.PRODUCT_SETTINGS);
        } else {
            switch (update.getMessage().getText()) {
                case (Constant.Button.ADD_PRODUCT) -> context.putState(chatId, BotState.SUGGESTION_TO_INPUT_LINK);
                case (Constant.Button.MY_PRODUCTS) -> context.putState(chatId, BotState.WATCH_PRODUCTS);
                case (Constant.Button.SETTINGS) -> context.putState(chatId, BotState.SETTINGS);
                default -> context.putState(chatId, BotState.UNEXPECTED);
            }
        }
    }

    @Override
    public void reply(Update update) {
        if (update.hasCallbackQuery())
            return;

        Long chatId = getChatId(update);
        List<Product> products = productService.findAllByChatIdAndIsActive(chatId, true);

        if (products.isEmpty()) {
            bot.getSilent().execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(Constant.Message.PRODUCTS_IS_EMPTY)
                    .replyMarkup(Keyboard.withMainPageActions())
                    .build());
        } else {
            AtomicInteger num = new AtomicInteger(1);
            products.stream()
                    .sorted(Comparator.comparing(Product::getCreatedAt))
                    .forEach(product -> bot.getSilent().execute(SendMessage.builder()
                            .chatId(chatId)
                            .text(num.getAndIncrement() + ". " +
                                    product.getTitle() + "\n" +
                                    product.getUrl())
                            .replyMarkup(Keyboard.withGoToProductSettingsButton(product.getId()))
                            .linkPreviewOptions(LinkPreviewOptions.builder()
                                    .isDisabled(true)
                                    .build())
                            .build()));
            bot.getSilent().execute(SendMessage.builder()
                    .chatId(getChatId(update))
                    .text(Constant.Message.CHOOSE_ACTION)
                    .replyMarkup(Keyboard.withMainPageActions())
                    .build());
        }
    }

    @Override
    public BotState getType() {
        return BotState.WATCH_PRODUCTS;
    }
}