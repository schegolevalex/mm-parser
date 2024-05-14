package com.schegolevalex.mm.mmparser.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public class Keyboard {

    public static ReplyKeyboard withMainPageActions() {
        KeyboardRow row = new KeyboardRow();
        row.add(Constant.Button.ADD_LINK);
        row.add(Constant.Button.MY_LINKS);
        return new ReplyKeyboardMarkup(List.of(row));
    }

    public static ReplyKeyboard withBackButton() {
        KeyboardRow row = new KeyboardRow();
        row.add(Constant.Button.BACK);
        return new ReplyKeyboardMarkup(List.of(row));
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
}
