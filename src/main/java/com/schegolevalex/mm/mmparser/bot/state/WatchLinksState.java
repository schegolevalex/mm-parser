package com.schegolevalex.mm.mmparser.bot.state;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.service.ProductService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class WatchLinksState extends BaseState {
    private final ProductService productService;

    public WatchLinksState(@Lazy ParserBot bot, ProductService productService) {
        super(bot);
        this.productService = productService;
    }

    @Override
    public void route(Update update) {
        Long chatId = getChatId(update);
        switch (update.getMessage().getText()) {
            case (Constant.Button.BACK) -> context.putState(chatId, BotState.MAIN_PAGE_ACTION);
            default -> context.putState(chatId, BotState.UNEXPECTED);
        }
    }

    @Override
    public void reply(Update update) {
        Long chatId = getChatId(update);
        List<Product> products = productService.findAllByChatId(chatId);

        StringBuilder text = new StringBuilder();
        AtomicInteger num = new AtomicInteger(1);

        if (products.isEmpty())
            text.append(Constant.Message.LINKS_IS_EMPTY);
        else
            products.stream()
                    .sorted(Comparator.comparing(Product::getCreatedAt))
                    .forEach(product -> text.append(num.getAndIncrement())
                            .append(". ")
                            .append(product.getTitle())
                            .append("\n"));
        bot.getSilent().execute(SendMessage.builder()
                .chatId(chatId)
                .text(text.toString())
                .replyMarkup(Keyboard.withBackButton())
                .build());
    }

    @Override
    public BotState getType() {
        return BotState.WATCH_LINKS;
    }
}