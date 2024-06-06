package com.schegolevalex.mm.mmparser.bot.state;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.service.ProductService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class InputLinkState extends AbstractState {
    private static final String URL_REGEXP = "(http(s)?://.)?(www\\.)?megamarket\\.ru\\b([-a-zA-Z0-9@:%_+.~#?&/=]*)";
    private final ProductService productService;

    public InputLinkState(@Lazy ParserBot bot, ProductService productService) {
        super(bot);
        this.productService = productService;
    }

    @Override
    public void route(Update update) {
        context.putState(AbilityUtils.getChatId(update), BotState.MAIN_PAGE_ACTION);
    }

    @Override
    public void reply(Update update) {
        Long chatId = AbilityUtils.getChatId(update);
        String userText = update.getMessage().getText();

        Pattern pattern = Pattern.compile(URL_REGEXP);
        Matcher matcher = pattern.matcher(userText);
        if (matcher.find()) {
            String productUrl = matcher.group();

            if (!productUrl.endsWith("/"))
                productUrl += "/";

            productService.save(Product.builder()
                    .url(productUrl)
                    .chatId(chatId)
                    .build());

            bot.getSilent().execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(Constant.Message.LINK_IS_ACCEPTED)
                    .build());
        }
    }

    @Override
    public BotState getType() {
        return BotState.INPUT_LINK;
    }
}
