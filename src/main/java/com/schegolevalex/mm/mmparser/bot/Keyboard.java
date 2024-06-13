package com.schegolevalex.mm.mmparser.bot;

import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.entity.Promo;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.schegolevalex.mm.mmparser.bot.Constant.*;

public class Keyboard {

    public static ReplyKeyboard withMainPageActions() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(Button.ADD_PRODUCT);
        row1.add(Button.MY_PRODUCTS);
        KeyboardRow row2 = new KeyboardRow();
        row2.add(Button.SETTINGS);
        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row1, row2))
                .resizeKeyboard(true)
                .build();
    }

    public static ReplyKeyboard withBackButton() {
        KeyboardRow row = new KeyboardRow();
        row.add(Button.BACK);
        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row))
                .resizeKeyboard(true)
                .build();
    }

    public static ReplyKeyboard withOkButton() {
        KeyboardRow row = new KeyboardRow();
        row.add(Button.OK);
        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row))
                .resizeKeyboard(true)
                .build();
    }

    public static ReplyKeyboard withMainPageButton() {
        KeyboardRow row = new KeyboardRow();
        row.add(Button.MAIN_PAGE);
        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row))
                .resizeKeyboard(true)
                .build();
    }

    public static ReplyKeyboard withBeginConversationButton() {
        KeyboardRow row = new KeyboardRow();
        row.add(Button.START_CONVERSATION);
        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row))
                .resizeKeyboard(true)
                .build();
    }

    public static ReplyKeyboard withSettings() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(Button.PROMOS_SETTINGS);
        row1.add(Button.CASHBACK_SETTINGS);
        KeyboardRow row2 = new KeyboardRow();
        row2.add(Button.BACK);
        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row1, row2))
                .resizeKeyboard(true)
                .build();
    }

    public static ReplyKeyboard withPromoSettingsActions() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(Button.ADD_PROMO);
        row1.add(Button.MY_PROMOS);
        KeyboardRow row2 = new KeyboardRow();
        row2.add(Button.BACK);
        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row1, row2))
                .resizeKeyboard(true)
                .build();
    }

    public static InlineKeyboardMarkup withCashbackLevels(Long cashbackLevel) {
        String zero = "0";
        String two = "2";
        String five = "5";
        String seven = "7";
        String nine = "9";
        String twelve = "12";

        String s = String.valueOf(cashbackLevel);
        switch (s) {
            case "0" -> zero = "✅ " + zero + "%";
            case "2" -> two = "✅ " + two + "%";
            case "5" -> five = "✅ " + five + "%";
            case "7" -> seven = "✅ " + seven + "%";
            case "9" -> nine = "✅ " + nine + "%";
            case "12" -> twelve = "✅ " + twelve + "%";
        }

        InlineKeyboardRow row1 = new InlineKeyboardRow();
        row1.add(InlineKeyboardButton.builder()
                .text(zero)
                .callbackData("0")
                .build());
        row1.add(InlineKeyboardButton.builder()
                .text(two)
                .callbackData("2")
                .build());
        row1.add(InlineKeyboardButton.builder()
                .text(five)
                .callbackData("5")
                .build());
        InlineKeyboardRow row2 = new InlineKeyboardRow();
        row2.add(InlineKeyboardButton.builder()
                .text(seven)
                .callbackData("7")
                .build());
        row2.add(InlineKeyboardButton.builder()
                .text(nine)
                .callbackData("9")
                .build());
        row2.add(InlineKeyboardButton.builder()
                .text(twelve)
                .callbackData("12")
                .build());
        return new InlineKeyboardMarkup(List.of(row1, row2));
    }

    public static ReplyKeyboard continueAddOrSavePromoSteps() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(Button.YES_ADD_MORE_PROMO_STEPS);
        KeyboardRow row2 = new KeyboardRow();
        row2.add(Button.NO_SAVE_PROMO);
        KeyboardRow row3 = new KeyboardRow();
        row3.add(Button.BACK);
        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row1, row2, row3))
                .resizeKeyboard(true)
                .build();
    }

    public static InlineKeyboardMarkup withDeletePromoButton(Long promoId) {
        InlineKeyboardRow row1 = new InlineKeyboardRow();
        row1.add(InlineKeyboardButton.builder()
                .text(Button.DELETE_PROMO)
                .callbackData(Button.DELETE_PROMO + DELIMITER + promoId)
                .build());
        return new InlineKeyboardMarkup(List.of(row1));
    }

    public static InlineKeyboardMarkup withChoosePromoForProductButton(Long promoId, Long productId) {
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(InlineKeyboardButton.builder()
                .text(Button.CHOOSE_PROMO)
                .callbackData(Button.MY_PRODUCTS + DELIMITER + productId
                        + DELIMITER + Button.CHOOSE_PROMO + DELIMITER + promoId)
                .build());
        return new InlineKeyboardMarkup(List.of(row));
    }

    public static InlineKeyboardMarkup withGoToProductSettingsButton(Long productId) {
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(InlineKeyboardButton.builder()
                .text(Button.PRODUCT_SETTINGS)
                .callbackData(Button.PRODUCT_SETTINGS + DELIMITER + productId)
                .build());
        return new InlineKeyboardMarkup(List.of(row));
    }

    public static ReplyKeyboard withProductSettingsActions() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(Button.NOTIFICATIONS_SETTINGS);
        KeyboardRow row2 = new KeyboardRow();
        row2.add(Button.APPLY_PROMO);
        KeyboardRow row3 = new KeyboardRow();
        row3.add(Button.DELETE_PRODUCT);
        KeyboardRow row4 = new KeyboardRow();
        row4.add(Button.BACK);
        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row1, row2, row3, row4))
                .resizeKeyboard(true)
                .build();
    }


//        InlineKeyboardRow row1 = new InlineKeyboardRow();
//        row1.add(InlineKeyboardButton.builder()
//                .text(Constant.Button.NOTIFICATIONS_SETTINGS)
//                .callbackData(Constant.Button.APPLY_PROMO + Constant.DELIMITER + productId)
//                .build());
//        InlineKeyboardRow row2 = new InlineKeyboardRow();
//        row2.add(InlineKeyboardButton.builder()
//                .text(Constant.Button.APPLY_PROMO)
//                .callbackData(Constant.Button.APPLY_PROMO + Constant.DELIMITER + productId)
//                .build());
//        InlineKeyboardRow row3 = new InlineKeyboardRow();
//        row3.add(InlineKeyboardButton.builder()
//                .text(Constant.Button.DELETE_PRODUCT)
//                .callbackData(Constant.Button.DELETE_PRODUCT + Constant.DELIMITER + productId)
//                .build());
//        return new InlineKeyboardMarkup(List.of(row1, row2, row3));

    public static InlineKeyboardMarkup withSelectPromoToProduct(List<Promo> promos, Product product) {
        List<InlineKeyboardRow> keyboard = new ArrayList<>();

        promos.forEach(promo -> {
            InlineKeyboardRow row = new InlineKeyboardRow();
            row.add(InlineKeyboardButton.builder()
                    .text((product.getPromo() == promo ? "✅ " : "") + promo.getPromoSteps().stream()
                            .map(promoStep -> String.format(Message.PROMO, promoStep.getDiscount(), promoStep.getPriceFrom()))
                            .collect(Collectors.joining("; ")))
                    .callbackData(Button.MY_PRODUCTS + DELIMITER + product.getId() + DELIMITER
                            + Button.MY_PROMOS + DELIMITER + promo.getId())
                    .build());
            keyboard.add(row);
        });

        return new InlineKeyboardMarkup(keyboard);
    }

    public static ReplyKeyboard withBackToProductListButton() {
        KeyboardRow row = new KeyboardRow();
        row.add(Button.BACK_TO_PRODUCTS_LIST);
        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row))
                .resizeKeyboard(true)
                .build();
    }

//    public static ReplyKeyboard withBackToPromoSettingsButton() {
//        KeyboardRow row1 = new KeyboardRow();
//        row1.add(Constant.Button.PROMOS_SETTINGS);
//        row1.add(Constant.Button.MY_PROMOS);
//        KeyboardRow row2 = new KeyboardRow();
//        row2.add(Constant.Button.BACK);
//        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(List.of(row1, row2));
//        replyKeyboardMarkup.setResizeKeyboard(true);
//        return replyKeyboardMarkup;
//    }
}
