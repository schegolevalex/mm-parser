package com.schegolevalex.mm.mmparser.bot.state;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.service.ProductService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
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
        context.popState(getChatId(update));
    }

    @Override
    public void reply(Update update) {
        long productId = Long.parseLong(update.getCallbackQuery().getData().split(Constant.DELIMITER)[1]);
        Product product = productService.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setActive(false);
        bot.getSilent().execute(DeleteMessage.builder()
                .chatId(getChatId(update))
                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                .build());
    }

    @Override
    public BotState getType() {
        return BotState.DELETE_PRODUCT;
    }
}
