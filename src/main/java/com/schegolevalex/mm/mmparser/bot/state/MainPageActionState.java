package com.schegolevalex.mm.mmparser.bot.state;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.service.ProductService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class MainPageActionState extends AbstractState {
    private final ProductService productService;

    public MainPageActionState(@Lazy ParserBot bot, ProductService productService) {
        super(bot);
        this.productService = productService;
    }

    @Override
    public void reply(Update update) {
        Long chatId = getChatId(update);

        if (update.getMessage().getText().equalsIgnoreCase(Constant.Button.ADD_LINK)) {
            bot.sendMessageAndPutState(chatId,
                    Constant.Message.SUGGESTION_TO_LINK_INPUT,
                    Keyboard.withBackButton(),
                    BotState.INPUT_LINK);
        } else if (update.getMessage().getText().equalsIgnoreCase(Constant.Button.MY_LINKS)) {
            List<Product> products = productService.findAllByChatId(chatId);

            StringBuilder text = new StringBuilder();
            AtomicInteger num = new AtomicInteger(1);

            if (products.isEmpty())
                text.append(Constant.Message.LINKS_IS_EMPTY);
            else
                products.stream()
                        .sorted((product1, product2) -> product2.getCreatedAt().compareTo(product1.getCreatedAt()))
                        .forEach(product -> text.append(num.getAndIncrement())
                                .append(". ")
                                .append(product.getTitle())
                                .append("\n"));

            bot.sendMessageAndPutState(chatId,
                    String.valueOf(text),
                    Keyboard.withBackButton(),
                    BotState.WATCH_LINKS);
        } else
            bot.unexpectedMessage(chatId);
    }

    @Override
    public BotState getType() {
        return BotState.MAIN_PAGE_ACTION;
    }
}