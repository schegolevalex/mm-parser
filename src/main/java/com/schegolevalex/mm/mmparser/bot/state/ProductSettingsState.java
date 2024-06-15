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

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class ProductSettingsState extends BaseState {
    private final ProductService productService;

    public ProductSettingsState(@Lazy ParserBot bot, ProductService productService) {
        super(bot);
        this.productService = productService;
    }

    @Override
    public void route(Update update) {
        Long chatId = getChatId(update);
        switch (update.getMessage().getText()) {
            case (Constant.Button.NOTIFICATIONS_SETTINGS) -> context.putState(chatId, BotState.NOTIFICATIONS_SETTINGS);
            case (Constant.Button.APPLY_PROMO) -> context.putState(chatId, BotState.APPLY_PROMO);
            case (Constant.Button.DELETE_PRODUCT) -> context.putState(chatId, BotState.DELETE_PRODUCT);
            case (Constant.Button.BACK) -> context.putState(chatId, BotState.WATCH_PRODUCTS);
            default -> context.putState(chatId, BotState.UNEXPECTED);
        }
    }

    @Override
    public void reply(Update update) {
        long productId = Long.parseLong(update.getCallbackQuery().getData().split(Constant.DELIMITER)[1]);
        Long chatId = getChatId(update);
        Product product = productService.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        context.putConfigurableProductId(chatId, productId);
        bot.getSilent().execute(SendMessage.builder()
                .text("Настраиваем продукт:\n" + product.getTitle() + "\n" + product.getUrl())
                .chatId(chatId)
                .build());
        bot.getSilent().execute(SendMessage.builder()
                .text(Constant.Message.CHOOSE_ACTION)
                .chatId(chatId)
                .replyMarkup(Keyboard.withProductSettingsActions())
                .build());
    }

    @Override
    public BotState getType() {
        return BotState.PRODUCT_SETTINGS;
    }
}
