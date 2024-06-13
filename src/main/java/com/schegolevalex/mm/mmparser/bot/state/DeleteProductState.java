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
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@Transactional
public class DeleteProductState extends BaseState {
    private final ProductService productService;

    public DeleteProductState(@Lazy ParserBot bot, ProductService productService) {
        super(bot);
        this.productService = productService;
    }

    @Override
    public void route(Update update) {
        Long chatId = getChatId(update);
        switch (update.getMessage().getText()) {
            case (Constant.Button.BACK_TO_PRODUCTS_LIST) -> context.putState(chatId, BotState.WATCH_PRODUCTS);
            default -> context.putState(chatId, BotState.UNEXPECTED);
        }
    }

    @Override
    public void reply(Update update) {
        Long chatId = getChatId(update);
        Long configurableProductId = context.getConfigurableProductId(chatId);
        Product product = productService.findById(configurableProductId).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setActive(false);
        bot.getSilent().execute(SendMessage.builder()
                .chatId(chatId)
                .text(String.format(Constant.Message.PRODUCT_IS_DELETED, product.getTitle()))
                .replyMarkup(Keyboard.withBackToProductListButton())
                .build());
    }

    @Override
    public BotState getType() {
        return BotState.DELETE_PRODUCT;
    }
}
