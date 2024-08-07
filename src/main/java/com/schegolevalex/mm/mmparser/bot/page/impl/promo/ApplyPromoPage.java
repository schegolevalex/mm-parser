package com.schegolevalex.mm.mmparser.bot.page.impl.promo;

import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.handler.Handler;
import com.schegolevalex.mm.mmparser.bot.page.base.HandlersPage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Promo;
import com.schegolevalex.mm.mmparser.service.ProductService;
import com.schegolevalex.mm.mmparser.service.PromoService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;

import static com.schegolevalex.mm.mmparser.bot.Constant.Callback;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@Transactional
public class ApplyPromoPage extends HandlersPage {

    private final ProductService productService;
    private final PromoService promoService;

    public ApplyPromoPage(ParserBot bot,
                          List<Handler> handlers,
                          ProductService productService,
                          PromoService promoService) {
        super(bot, handlers);
        this.productService = productService;
        this.promoService = promoService;
    }

    @Override
    public void show(Update prevUpdate) {
        Long chatId = getChatId(prevUpdate);
        Map<String, String> callbacks = extractCallbacksMap(prevUpdate);
        long productId = Long.parseLong(callbacks.get(Callback.APPLY_PROMO));

        Promo selectedPromo = productService.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"))
                .getPromo();
        List<Promo> promos = promoService.findAllByChatId(chatId);

        int page = 1;
        if (callbacks.containsKey(Callback.KEYBOARD_PAGES))
            page = Integer.parseInt(callbacks.get(Callback.KEYBOARD_PAGES));

        bot.getSilent().execute(EditMessageReplyMarkup.builder()
                .chatId(chatId)
                .messageId(prevUpdate.getCallbackQuery().getMessage().getMessageId())
                .replyMarkup(Keyboard.withPromosForProduct(promos, selectedPromo, productId, page))
                .build());
    }

    @Override
    public Page getPage() {
        return Page.APPLY_PROMO;
    }
}
