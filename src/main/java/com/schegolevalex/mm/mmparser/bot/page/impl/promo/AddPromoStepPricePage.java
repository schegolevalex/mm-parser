package com.schegolevalex.mm.mmparser.bot.page.impl.promo;

import com.schegolevalex.mm.mmparser.bot.Keyboard;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.bot.page.base.BasePage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Promo;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.schegolevalex.mm.mmparser.bot.Constant.Button;
import static com.schegolevalex.mm.mmparser.bot.Constant.Message;
import static com.schegolevalex.mm.mmparser.bot.util.MessageUtil.prepareToMarkdownV2;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class AddPromoStepPricePage extends BasePage {

    public AddPromoStepPricePage(@Lazy ParserBot bot) {
        super(bot);
    }

    @Override
    public void beforeUpdateReceive(Update prevUpdate) {
        bot.getSilent().execute(SendMessage.builder()
                .chatId(getChatId(prevUpdate))
                .text(prepareToMarkdownV2(Message.ADD_PROMO_STEP_PRICE))
                .replyMarkup(Keyboard.withPromoSettingsActions())
                .parseMode("MarkdownV2")
                .build());
    }

    @Override
    public void afterUpdateReceive(Update nextUpdate) {
        Long chatId = getChatId(nextUpdate);
        String text = nextUpdate.getMessage().getText();

        try {
            int priceFrom = Integer.parseInt(text);
            Promo promo = context.getPromo(chatId);
            promo.getPromoSteps().getLast().setPriceFrom(priceFrom);
            context.putPage(chatId, Page.ADD_PROMO_STEP_SUCCESSFUL);
        } catch (NumberFormatException e) {
            switch (text) {
                case Button.ADD_PROMO -> context.putPage(chatId, Page.ADD_PROMO_STEP_PRICE);
                case Button.MY_PROMOS -> context.putPage(chatId, Page.WATCH_PROMOS);
                case Button.BACK -> context.popPage(chatId);
                default -> context.putPage(chatId, Page.UNEXPECTED);
            }
        } catch (IllegalArgumentException e) {
            bot.getSilent().execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(e.getMessage())
                    .build());
        }
    }

    @Override
    public Page getPage() {
        return Page.ADD_PROMO_STEP_PRICE;
    }
}
