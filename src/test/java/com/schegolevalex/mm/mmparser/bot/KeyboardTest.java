package com.schegolevalex.mm.mmparser.bot;

import com.schegolevalex.mm.mmparser.entity.*;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;
import java.util.Set;

import static com.schegolevalex.mm.mmparser.bot.Constant.DELIMITER;
import static org.junit.jupiter.api.Assertions.assertEquals;

class KeyboardTest {
    long productId = 123L;

    @Test
    void testWithCashbackLevels_zero() {
        InlineKeyboardMarkup markup = Keyboard.withCashbackLevels(0);

        // Проверяем первую строку
        InlineKeyboardRow row1 = markup.getKeyboard().getFirst();
        assertEquals("✅ 0%", row1.getFirst().getText());
        assertEquals("2%", row1.get(1).getText());
        assertEquals("5%", row1.get(2).getText());

        // Проверяем вторую строку
        InlineKeyboardRow row2 = markup.getKeyboard().get(1);
        assertEquals("7%", row2.getFirst().getText());
        assertEquals("9%", row2.get(1).getText());
        assertEquals("12%", row2.get(2).getText());
    }

    @Test
    void testWithCashbackLevels_two() {
        InlineKeyboardMarkup markup = Keyboard.withCashbackLevels(2);

        // Проверяем первую строку
        InlineKeyboardRow row1 = markup.getKeyboard().getFirst();
        assertEquals("0%", row1.getFirst().getText());
        assertEquals("✅ 2%", row1.get(1).getText());
        assertEquals("5%", row1.get(2).getText());

        // Проверяем вторую строку
        InlineKeyboardRow row2 = markup.getKeyboard().get(1);
        assertEquals("7%", row2.getFirst().getText());
        assertEquals("9%", row2.get(1).getText());
        assertEquals("12%", row2.get(2).getText());
    }

    @Test
    void testWithCashbackLevels_five() {
        InlineKeyboardMarkup markup = Keyboard.withCashbackLevels(5);

        // Проверяем первую строку
        InlineKeyboardRow row1 = markup.getKeyboard().getFirst();
        assertEquals("0%", row1.getFirst().getText());
        assertEquals("2%", row1.get(1).getText());
        assertEquals("✅ 5%", row1.get(2).getText());

        // Проверяем вторую строку
        InlineKeyboardRow row2 = markup.getKeyboard().get(1);
        assertEquals("7%", row2.getFirst().getText());
        assertEquals("9%", row2.get(1).getText());
        assertEquals("12%", row2.get(2).getText());
    }

    @Test
    void testWithCashbackLevels_seven() {
        InlineKeyboardMarkup markup = Keyboard.withCashbackLevels(7);

        // Проверяем первую строку
        InlineKeyboardRow row1 = markup.getKeyboard().getFirst();
        assertEquals("0%", row1.getFirst().getText());
        assertEquals("2%", row1.get(1).getText());
        assertEquals("5%", row1.get(2).getText());

        // Проверяем вторую строку
        InlineKeyboardRow row2 = markup.getKeyboard().get(1);
        assertEquals("✅ 7%", row2.getFirst().getText());
        assertEquals("9%", row2.get(1).getText());
        assertEquals("12%", row2.get(2).getText());
    }

    @Test
    void testWithCashbackLevels_nine() {
        InlineKeyboardMarkup markup = Keyboard.withCashbackLevels(9);

        // Проверяем первую строку
        InlineKeyboardRow row1 = markup.getKeyboard().getFirst();
        assertEquals("0%", row1.getFirst().getText());
        assertEquals("2%", row1.get(1).getText());
        assertEquals("5%", row1.get(2).getText());

        // Проверяем вторую строку
        InlineKeyboardRow row2 = markup.getKeyboard().get(1);
        assertEquals("7%", row2.getFirst().getText());
        assertEquals("✅ 9%", row2.get(1).getText());
        assertEquals("12%", row2.get(2).getText());
    }

    @Test
    void testWithCashbackLevels_twelve() {
        InlineKeyboardMarkup markup = Keyboard.withCashbackLevels(12);

        // Проверяем первую строку
        InlineKeyboardRow row1 = markup.getKeyboard().getFirst();
        assertEquals("0%", row1.getFirst().getText());
        assertEquals("2%", row1.get(1).getText());
        assertEquals("5%", row1.get(2).getText());

        // Проверяем вторую строку
        InlineKeyboardRow row2 = markup.getKeyboard().get(1);
        assertEquals("7%", row2.getFirst().getText());
        assertEquals("9%", row2.get(1).getText());
        assertEquals("✅ 12%", row2.get(2).getText());
    }

    @Test
    void testWithCashbackLevels_invalid() {
        InlineKeyboardMarkup markup = Keyboard.withCashbackLevels(15); // Неизвестный уровень

        // Проверяем первую строку
        InlineKeyboardRow row1 = markup.getKeyboard().getFirst();
        assertEquals("0%", row1.getFirst().getText());
        assertEquals("2%", row1.get(1).getText());
        assertEquals("5%", row1.get(2).getText());

        // Проверяем вторую строку
        InlineKeyboardRow row2 = markup.getKeyboard().get(1);
        assertEquals("7%", row2.getFirst().getText());
        assertEquals("9%", row2.get(1).getText());
        assertEquals("12%", row2.get(2).getText());
    }

    @Test
    void testWithPromosForProduct_whenEmptyPromos_thenGetPromoIsEmptyKeyboard() {
        List<InlineKeyboardRow> keyboard = Keyboard.withPromosForProduct(List.of(), null, productId, 1).getKeyboard();

        assertEquals(2, keyboard.size());
        assertEquals(1, keyboard.getFirst().size());
        InlineKeyboardButton promoIsEmptyButton = keyboard.getFirst().getFirst();
        assertEquals(Constant.Message.PROMOS_IS_EMPTY, promoIsEmptyButton.getText());
        assertEquals(Constant.Callback.EMPTY, promoIsEmptyButton.getCallbackData());
    }

    @Test
    void testWithPromosForProduct_whenOnePromo_thenGetKeyboardWithOnePromo() {
        PromoStep promoStep = PromoStep.builder()
                .priceFrom(10)
                .discount(1)
                .build();
        Promo promo = Promo.builder()
                .id(1L)
                .promoSteps(List.of(promoStep))
                .build();

        int page = 1;

        List<InlineKeyboardRow> keyboard = Keyboard.withPromosForProduct(List.of(promo), null, productId, page).getKeyboard();

        assertEquals(2, keyboard.size());
        assertEquals(1, keyboard.getFirst().size());
        InlineKeyboardButton firstPromoButton = keyboard.getFirst().getFirst();
        assertEquals(promoStep.getDiscount() + "/" + promoStep.getPriceFrom(), firstPromoButton.getText());
        assertEquals(Constant.Callback.APPLY_PROMO + DELIMITER + productId + DELIMITER
                     + Constant.Callback.MY_PROMOS + DELIMITER + promo.getId() + DELIMITER +
                     Constant.Callback.KEYBOARD_PAGES + DELIMITER + page, firstPromoButton.getCallbackData());
    }

    @Test
    void testWithPromosForProduct_whenOnePromoWithSelected_thenGetKeyboardWithOneSelectedPromo() {
        PromoStep promoStep = PromoStep.builder()
                .priceFrom(10)
                .discount(1)
                .build();
        Promo promo = Promo.builder()
                .id(1L)
                .promoSteps(List.of(promoStep))
                .build();

        int page = 1;

        List<InlineKeyboardRow> keyboard = Keyboard.withPromosForProduct(List.of(promo), promo, productId, page).getKeyboard();

        assertEquals(2, keyboard.size());
        assertEquals(1, keyboard.getFirst().size());
        InlineKeyboardButton firstPromoButton = keyboard.getFirst().getFirst();
        assertEquals("✅ " + promoStep.getDiscount() + "/" + promoStep.getPriceFrom(), firstPromoButton.getText());
        assertEquals(Constant.Callback.APPLY_PROMO + DELIMITER + productId + DELIMITER
                     + Constant.Callback.MY_PROMOS + DELIMITER + promo.getId() + DELIMITER +
                     Constant.Callback.KEYBOARD_PAGES + DELIMITER + page, firstPromoButton.getCallbackData());
    }

    @Test
    void testWithPromosForProduct_whenMultiplePromos_thenGetKeyboardWithMultiplePromos() {
        PromoStep promoStep = PromoStep.builder()
                .priceFrom(10)
                .discount(1)
                .build();
        Promo promo1 = Promo.builder()
                .id(1L)
                .promoSteps(List.of(promoStep))
                .build();
        Promo promo2 = Promo.builder()
                .id(2L)
                .promoSteps(List.of(promoStep))
                .build();
        Promo promo3 = Promo.builder()
                .id(3L)
                .promoSteps(List.of(promoStep))
                .build();
        Promo promo4 = Promo.builder()
                .id(4L)
                .promoSteps(List.of(promoStep))
                .build();
        Promo promo5 = Promo.builder()
                .id(5L)
                .promoSteps(List.of(promoStep))
                .build();
        Promo promo6 = Promo.builder()
                .id(6L)
                .promoSteps(List.of(promoStep))
                .build();
        int page = 1;
        List<Promo> promoList = List.of(promo1, promo2, promo3, promo4, promo5, promo6);

        List<InlineKeyboardRow> keyboard = Keyboard.withPromosForProduct(promoList, null, productId, page).getKeyboard();

        assertEquals(6, keyboard.size());
        for (int i = 0; i < keyboard.size() - 1; i++) {
            InlineKeyboardButton promoButton = keyboard.get(i).getFirst();
            assertEquals(promoStep.getDiscount() + "/" + promoStep.getPriceFrom(), promoButton.getText());
            assertEquals(Constant.Callback.APPLY_PROMO + DELIMITER + productId + DELIMITER
                         + Constant.Callback.MY_PROMOS + DELIMITER + promoList.get(i).getId() + DELIMITER +
                         Constant.Callback.KEYBOARD_PAGES + DELIMITER + page, promoButton.getCallbackData());
        }
    }

    @Test
    void testWithProduct_shouldReturnCorrectKeyboardWithId() {
        List<InlineKeyboardRow> keyboard = Keyboard.withProduct(productId).getKeyboard();
        assertEquals(1, keyboard.size());
        InlineKeyboardRow firstRow = keyboard.getFirst();
        assertEquals(2, firstRow.size());

        InlineKeyboardButton button = firstRow.getFirst();
        assertEquals(Constant.Button.PRODUCT_SETTINGS, button.getText());
        assertEquals(Constant.Callback.PRODUCT_SETTINGS + DELIMITER + productId, button.getCallbackData());

        button = firstRow.get(1);
        assertEquals(Constant.Button.DELETE_PRODUCT, button.getText());
        assertEquals(Constant.Callback.DELETE_PRODUCT + DELIMITER + productId, button.getCallbackData());
    }

    @Test
    void testWithConfirmOrDeclineDeleteProductButtons_shouldReturnCorrectKeyboardWithId() {
        List<InlineKeyboardRow> keyboard = Keyboard.withConfirmOrDeclineDeleteProductButtons(productId).getKeyboard();
        assertEquals(1, keyboard.size());
        InlineKeyboardRow firstRow = keyboard.getFirst();
        assertEquals(2, firstRow.size());

        InlineKeyboardButton button = firstRow.getFirst();
        assertEquals(Constant.Button.CONFIRM, button.getText());
        assertEquals(Constant.Callback.CONFIRM_DELETE_PRODUCT + DELIMITER + productId, button.getCallbackData());

        button = firstRow.get(1);
        assertEquals(Constant.Button.DECLINE, button.getText());
        assertEquals(Constant.Callback.DECLINE_DELETE_PRODUCT + DELIMITER + productId, button.getCallbackData());
    }


    @Test
    void testWithFiltersForProduct_whenEmptyFilters_thenGetFilterIsEmptyKeyboard() {
        List<InlineKeyboardRow> keyboard = Keyboard.withFiltersForProduct(List.of(), null, productId, 1).getKeyboard();

        assertEquals(2, keyboard.size());
        assertEquals(1, keyboard.getFirst().size());
        InlineKeyboardButton promoIsEmptyButton = keyboard.getFirst().getFirst();
        assertEquals(Constant.Message.FILTERS_IS_EMPTY, promoIsEmptyButton.getText());
        assertEquals(Constant.Callback.EMPTY, promoIsEmptyButton.getCallbackData());
    }

    @Test
    void testWithFiltersForProduct_whenOneFilter_thenGetKeyboardWithOneFilter() {
        Filter filter = Filter.builder()
                .id(1L)
                .field(FilterField.PRICE)
                .operation(Operation.EQUALS)
                .value(10)
                .build();

        int page = 1;

        List<InlineKeyboardRow> keyboard = Keyboard.withFiltersForProduct(List.of(filter), null, productId, page).getKeyboard();

        assertEquals(2, keyboard.size());
        assertEquals(1, keyboard.getFirst().size());
        InlineKeyboardButton firstPromoButton = keyboard.getFirst().getFirst();
        assertEquals(filter.toString(), firstPromoButton.getText());
        assertEquals(Constant.Callback.APPLY_FILTER + DELIMITER + productId + DELIMITER
                     + Constant.Callback.MY_FILTERS + DELIMITER + filter.getId() + DELIMITER +
                     Constant.Callback.KEYBOARD_PAGES + DELIMITER + page, firstPromoButton.getCallbackData());
    }

    @Test
    void testWithFiltersForProduct_whenOneFilterWithSelected_thenGetKeyboardWithOneSelectedFilter() {
        Filter filter = Filter.builder()
                .id(1L)
                .field(FilterField.PRICE)
                .operation(Operation.EQUALS)
                .value(10)
                .build();

        int page = 1;

        List<InlineKeyboardRow> keyboard = Keyboard.withFiltersForProduct(List.of(filter), Set.of(filter), productId, page).getKeyboard();

        assertEquals(2, keyboard.size());
        assertEquals(1, keyboard.getFirst().size());
        InlineKeyboardButton firstPromoButton = keyboard.getFirst().getFirst();
        assertEquals("✅ " + filter, firstPromoButton.getText());
        assertEquals(Constant.Callback.APPLY_FILTER + DELIMITER + productId + DELIMITER
                     + Constant.Callback.MY_FILTERS + DELIMITER + filter.getId() + DELIMITER +
                     Constant.Callback.KEYBOARD_PAGES + DELIMITER + page, firstPromoButton.getCallbackData());
    }

    @Test
    void testWithFiltersForProduct_whenMultipleFilters_thenGetKeyboardWithMultipleFilters() {
        Filter filter1 = Filter.builder()
                .id(1L)
                .field(FilterField.PRICE)
                .operation(Operation.EQUALS)
                .value(10)
                .build();
        Filter filter2 = Filter.builder()
                .id(1L)
                .field(FilterField.PRICE)
                .operation(Operation.EQUALS)
                .value(10)
                .build();
        Filter filter3 = Filter.builder()
                .id(1L)
                .field(FilterField.PRICE)
                .operation(Operation.EQUALS)
                .value(10)
                .build();
        Filter filter4 = Filter.builder()
                .id(1L)
                .field(FilterField.PRICE)
                .operation(Operation.EQUALS)
                .value(10)
                .build();
        Filter filter5 = Filter.builder()
                .id(1L)
                .field(FilterField.PRICE)
                .operation(Operation.EQUALS)
                .value(10)
                .build();
        Filter filter6 = Filter.builder()
                .id(1L)
                .field(FilterField.PRICE)
                .operation(Operation.EQUALS)
                .value(10)
                .build();
        int page = 1;
        List<Filter> filterList = List.of(filter1, filter2, filter3, filter4, filter5, filter6);

        List<InlineKeyboardRow> keyboard = Keyboard.withFiltersForProduct(filterList, null, productId, page).getKeyboard();

        assertEquals(6, keyboard.size());
        for (int i = 0; i < keyboard.size() - 1; i++) {
            InlineKeyboardButton promoButton = keyboard.get(i).getFirst();
            assertEquals(filterList.get(i).toString(), promoButton.getText());
            assertEquals(Constant.Callback.APPLY_FILTER + DELIMITER + productId + DELIMITER
                         + Constant.Callback.MY_FILTERS + DELIMITER + filterList.get(i).getId() + DELIMITER +
                         Constant.Callback.KEYBOARD_PAGES + DELIMITER + page, promoButton.getCallbackData());
        }
    }
}