package com.schegolevalex.mm.mmparser.bot.state;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.entity.Promo;
import com.schegolevalex.mm.mmparser.service.ProductService;
import com.schegolevalex.mm.mmparser.service.PromoService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
@Transactional
public class ApplyPromoState extends BaseState {
    private final ProductService productService;
    private final PromoService promoService;

    public ApplyPromoState(@Lazy ParserBot bot, ProductService productService, PromoService promoService) {
        super(bot);
        this.productService = productService;
        this.promoService = promoService;
    }

    @Override
    public void route(Update update) {
        Long chatId = getChatId(update);

        if (update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith(Constant.Button.MY_PRODUCTS)) {
            long productId = Long.parseLong(update.getCallbackQuery().getData().split(Constant.DELIMITER)[1]);
            Product product = productService.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
            long promoId = Long.parseLong(update.getCallbackQuery().getData().split(Constant.DELIMITER)[3]);
            Promo promo = promoService.findById(promoId).orElseThrow(() -> new RuntimeException("Promo not found"));
            promo.addProduct(product);
            context.putState(chatId, BotState.WATCH_PRODUCTS);
        } else {
            switch (update.getMessage().getText()) {
                case (Constant.Button.NOTIFICATIONS_SETTINGS) -> context.putState(chatId, BotState.NOTIFICATIONS_SETTINGS);
                case (Constant.Button.APPLY_PROMO) -> context.putState(chatId, BotState.APPLY_PROMO_SETTINGS);
                case (Constant.Button.DELETE_PRODUCT) -> context.putState(chatId, BotState.DELETE_PRODUCT);
                case (Constant.Button.BACK) -> context.putState(chatId, BotState.WATCH_PRODUCTS);
                default -> context.putState(chatId, BotState.UNEXPECTED);
            }
        }
    }

    @Override
    public void reply(Update update) {
        Long chatId = getChatId(update);
        Long configurableProductId = context.getConfigurableProductId(chatId);
        Product product = productService.findById(configurableProductId).orElseThrow(() -> new RuntimeException("Product not found"));

        List<Promo> promos = promoService.findAllByChatId(chatId);
        if (promos.isEmpty()) {
            bot.getSilent().execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(Constant.Message.PROMOS_IS_EMPTY)
                    .build());
        } else {
            AtomicInteger num = new AtomicInteger(1);
            promos.stream()
                    .sorted(Comparator.comparing(Promo::getCreatedAt))
                    .forEach(promo -> bot.getSilent().execute(SendMessage.builder()
                            .chatId(chatId)
                            .text((product.getPromo() == promo ? "✅ " : "") + num.getAndIncrement() + ". " +
                                    promo.getPromoSteps().stream()
                                            .map(promoStep -> String.format(Constant.Message.PROMO, promoStep.getDiscount(), promoStep.getPriceFrom()))
                                            .collect(Collectors.joining("; ")))
                            .replyMarkup(Keyboard.withChoosePromoForProductButton(promo.getId(), configurableProductId))
                            .build()));
        }
    }

    @Override
    public BotState getType() {
        return BotState.APPLY_PROMO_SETTINGS;
    }
}
