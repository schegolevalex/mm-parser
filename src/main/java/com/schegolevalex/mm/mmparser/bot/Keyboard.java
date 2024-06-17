package com.schegolevalex.mm.mmparser.bot;

import com.schegolevalex.mm.mmparser.entity.Promo;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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

    public static ReplyKeyboard withMainPageButton() {
        KeyboardRow row = new KeyboardRow();
        row.add(Button.MAIN_PAGE);
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
                .callbackData(Callback.DELETE_PROMO + DELIMITER + promoId)
                .build());
        return new InlineKeyboardMarkup(List.of(row1));
    }

    public static InlineKeyboardMarkup withProductSettings(Long productId) {
        InlineKeyboardRow row1 = new InlineKeyboardRow();
        row1.add(InlineKeyboardButton.builder()
                .text(Button.NOTIFICATIONS_SETTINGS)
                .callbackData(Callback.NOTIFICATIONS_SETTINGS + DELIMITER + productId)
                .build());
        InlineKeyboardRow row2 = new InlineKeyboardRow();
        row2.add(InlineKeyboardButton.builder()
                .text(Button.APPLY_PROMO)
                .callbackData(Callback.APPLY_PROMO + DELIMITER + productId)
                .build());
        InlineKeyboardRow row3 = new InlineKeyboardRow();
        row3.add(InlineKeyboardButton.builder()
                .text(Button.BACK)
                .callbackData(Callback.BACK + DELIMITER + productId)
                .build());
        return new InlineKeyboardMarkup(List.of(row1, row2, row3));
    }

    public static InlineKeyboardMarkup withPromosForProduct(List<Promo> promos, long productId, Promo selectedPromo, int page) {
        List<InlineKeyboardRow> keyboard = new ArrayList<>();

        int pageSize = 5;
        int totalPages = (int) Math.ceil((double) promos.size() / pageSize);
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, promos.size());

        if (page <= 0 || page > totalPages) {
            log.error("Неверный номер страницы для клавиатуры: {}", page);
            throw new RuntimeException("Неверный номер страницы для клавиатуры: " + page);
        }

        if (promos.isEmpty()) {
            InlineKeyboardRow row = new InlineKeyboardRow();
            row.add(InlineKeyboardButton.builder()
                    .text(Message.PROMOS_IS_EMPTY)
                    .callbackData(Callback.EMPTY)
                    .build());
            keyboard.add(row);
        } else {
            promos.subList(startIndex, endIndex).forEach(promo -> {
                InlineKeyboardRow row = new InlineKeyboardRow();
                row.add(InlineKeyboardButton.builder()
                        .text((selectedPromo == promo ? "✅ " : "") + promo.getPromoSteps().stream()
                                .map(promoStep -> String.format(Message.PROMO, promoStep.getDiscount(), promoStep.getPriceFrom()))
                                .collect(Collectors.joining("; ")))
                        .callbackData(Callback.APPLY_PROMO + DELIMITER + productId + DELIMITER
                                + Callback.MY_PROMOS + DELIMITER + promo.getId() + DELIMITER +
                                Callback.KEYBOARD_PAGES + DELIMITER + page)
                        .build());
                keyboard.add(row);
            });
        }

        InlineKeyboardRow bottomRow = new InlineKeyboardRow();
        if (page == 1) {
            bottomRow.add(InlineKeyboardButton.builder()
                    .text(Button.EMPTY)
                    .callbackData(Callback.EMPTY)
                    .build());
        } else {
            bottomRow.add(InlineKeyboardButton.builder()
                    .text(Button.PREVIOUS_PAGE)
                    .callbackData(Callback.APPLY_PROMO + DELIMITER + productId + DELIMITER
                            + Callback.KEYBOARD_PAGES + DELIMITER + (page - 1))
                    .build());
        }
        bottomRow.add(InlineKeyboardButton.builder()
                .text(Button.BACK_TO_PRODUCT_SETTINGS)
                .callbackData(Callback.BACK_TO_PRODUCT_SETTINGS + DELIMITER + productId)
                .build());
        if (page == totalPages) {
            bottomRow.add(InlineKeyboardButton.builder()
                    .text(Button.EMPTY)
                    .callbackData(Callback.EMPTY)
                    .build());
        } else {
            bottomRow.add(InlineKeyboardButton.builder()
                    .text(Button.NEXT_PAGE)
                    .callbackData(Callback.APPLY_PROMO + DELIMITER + productId + DELIMITER
                            + Callback.KEYBOARD_PAGES + DELIMITER + (page + 1))
                    .build());
        }
        keyboard.add(bottomRow);

        return new InlineKeyboardMarkup(keyboard);
    }

    public static InlineKeyboardMarkup withProduct(Long productId, String url) {
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(InlineKeyboardButton.builder()
                .text(Button.PRODUCT_URL)
                .url(url)
                .build());
        row.add(InlineKeyboardButton.builder()
                .text(Button.PRODUCT_NOTIFICATIONS)
                .callbackData(Callback.PRODUCT_NOTIFICATIONS + DELIMITER + productId)
                .build());
        row.add(InlineKeyboardButton.builder()
                .text(Button.PRODUCT_SETTINGS)
                .callbackData(Callback.PRODUCT_SETTINGS + DELIMITER + productId)
                .build());
        row.add(InlineKeyboardButton.builder()
                .text(Button.PRODUCT_DELETE)
                .callbackData(Callback.PRODUCT_DELETE + DELIMITER + productId)
                .build());
        return new InlineKeyboardMarkup(List.of(row));
    }

    public static InlineKeyboardMarkup withYesNoButtons(long entityId) {
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(InlineKeyboardButton.builder()
                .text(Button.CONFIRM)
                .callbackData(Callback.CONFIRM_DELETE + DELIMITER + entityId)
                .build());
        row.add(InlineKeyboardButton.builder()
                .text(Button.DECLINE)
                .callbackData(Callback.DECLINE_DELETE + DELIMITER + entityId)
                .build());
        return new InlineKeyboardMarkup(List.of(row));
    }
}
