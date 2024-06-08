package com.schegolevalex.mm.mmparser.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public class Keyboard {

    public static ReplyKeyboard withMainPageActions() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(Constant.Button.ADD_LINK);
        row1.add(Constant.Button.MY_LINKS);
        KeyboardRow row2 = new KeyboardRow();
        row2.add(Constant.Button.SETTINGS);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(List.of(row1, row2));
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public static ReplyKeyboard withBackButton() {
        KeyboardRow row = new KeyboardRow();
        row.add(Constant.Button.BACK);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(List.of(row));
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public static ReplyKeyboard withOkButton() {
        KeyboardRow row = new KeyboardRow();
        row.add(Constant.Button.OK);
        return new ReplyKeyboardMarkup(List.of(row));
    }

    public static ReplyKeyboard withMainPageButton() {
        KeyboardRow row = new KeyboardRow();
        row.add(Constant.Button.MAIN_PAGE);
        return new ReplyKeyboardMarkup(List.of(row));
    }

    public static ReplyKeyboard withBeginConversationButton() {
        KeyboardRow row = new KeyboardRow();
        row.add(Constant.Button.START_CONVERSATION);
        return new ReplyKeyboardMarkup(List.of(row));
    }

    public static ReplyKeyboard withSettings() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(Constant.Button.PROMOS_SETTINGS);
        row1.add(Constant.Button.CASHBACK_SETTINGS);
        KeyboardRow row2 = new KeyboardRow();
        row2.add(Constant.Button.BACK);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(List.of(row1, row2));
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public static ReplyKeyboard withPromoSettingsActions() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(Constant.Button.ADD_PROMO);
        row1.add(Constant.Button.MY_PROMOS);
        KeyboardRow row2 = new KeyboardRow();
        row2.add(Constant.Button.BACK);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(List.of(row1, row2));
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
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
}
