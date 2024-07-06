package com.schegolevalex.mm.mmparser.bot.page.impl.product;

import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.page.base.BasePage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.service.ProductService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.schegolevalex.mm.mmparser.bot.Constant.DELIMITER;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@Transactional
public class DeleteProductPage extends BasePage {
    private final ProductService productService;

    public DeleteProductPage(@Lazy ParserBot bot, ProductService productService) {
        super(bot);
        this.productService = productService;
    }

    @Override
    public void beforeUpdateReceive(Update prevUpdate) {
        long productId = Long.parseLong(prevUpdate.getCallbackQuery().getData().split(DELIMITER)[1]);
        Product product = productService.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setActive(false);
        bot.getSilent().execute(DeleteMessage.builder()
                .chatId(getChatId(prevUpdate))
                .messageId(prevUpdate.getCallbackQuery().getMessage().getMessageId())
                .build());
    }

    @Override
    public void afterUpdateReceive(Update nextUpdate) {
        context.popPage(getChatId(nextUpdate));
    }

    @Override
    public Page getPage() {
        return Page.DELETE_PRODUCT;
    }
}
