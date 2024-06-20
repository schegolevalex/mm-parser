package com.schegolevalex.mm.mmparser.bot;

public interface Constant {
    String DELIMITER = "/";

    interface Info {
        String BOT_DESCRIPTION = "mm-bot";
        String START = "начать работу";
        String STOP = "завершить работу";
    }

    interface Message {
        String CHOOSE_ACTION = "Выберите действие 🎯";
        String SUGGESTION_TO_LINK_INPUT = "Введите ссылку на товар 🔗";
        String PRODUCTS_IS_EMPTY = "Вы еще не добавили ни один товар 😦";
        String LINK_IS_ACCEPTED = "Ссылка принята☺️\nМожете добавить еще одну или вернуться на главную🏠";
        //        String UNEXPECTED_INPUT = "не понимаю, повторите еще раз 🤔";
        String BYE = "👋";
        String OFFER = """
                Предложение (~%d рублей):
                - продавец: "%s"
                - цена: %d руб
                - процент бонусов: %d%%
                - количество бонусов: %d
                - ссылка: %s""";
        String CHOOSE_SETTINGS = "Что настроим? 🔧";
        String PROMOS_IS_EMPTY = "нет промокодов 😦";
        String CHOOSE_YOUR_CASHBACK_LEVEL = """
                Выберите Ваш уровень 🟢 Сберпрайм на текущий месяц\\.\


                ||не забывайте обновлять его, так как он учитывается при расчете кэшбэка||""";
        String ADD_PROMO_STEP_PRICE = """
                Введите цену, от которой начинает действовать скидка:\


                ||например, 500 от __*2000*__ рублей||""";
        String ADD_PROMO_STEP_DISCOUNT = """
                Введите сумму скидки:\


                ||например, __*500*__ от 2000 рублей||""";
        String ADD_PROMO_STEP_SUCCESSFUL = """
                Скидка %d рублей от %d рублей добавлена\\. Хотите добавить еще скидку к этому 🔵 промокоду?\


                ||обычно промокод содержит несколько скидок в зависимости от суммы покупки, например, \
                __*"500 от 2000 рублей, 800 от 3000 рублей, 1500 от 10000 рублей"*__\\. Всё это один промокод\\!||""";
        String PROMO = "%d/%d";
        String FILTERS_IS_EMPTY = "нет фильтров 😦";
    }

    interface Button {
        String MY_PRODUCTS = "🛒 мои товары";
        String ADD_PRODUCT = "➕ добавить товар";
        String BACK = "⬅ назад";
        String MAIN_PAGE = "🏠 на главную";
        //        String OK = "👌 OK";
//        String START_CONVERSATION = "🏃 начинаем";
        String SETTINGS = "⚙️ настройки";
        String PROMOS_SETTINGS = "🔵 промокоды";
        String CASHBACK_SETTINGS = "🟢 кэшбэк Сберпрайм";
        String ADD_PROMO = "➕ добавить промокод";
        String MY_PROMOS = "📘 мои промокоды";
        String YES_ADD_MORE_PROMO_STEPS = "⏭ да, добавить еще скидку в промокод";
        String NO_SAVE_PROMO = "💾 нет, сохранить промокод";
        String DELETE_PROMO = "🗑";
        String CONFIRM = "✅";
        String DECLINE = "⛔️";
        String APPLY_PROMO = "🔵 применить промокод";
        String NOTIFICATIONS_SETTINGS = "🔔 настроить уведомления";
        String PREVIOUS_PAGE = "<<";
        String NEXT_PAGE = ">>";
        String EMPTY = " ";
        String PRODUCT_URL = "🔗";
        String PRODUCT_NOTIFICATIONS = "⭐️";
        String PRODUCT_SETTINGS = "🛠";
        String DELETE_PRODUCT = "🗑";
        String BACK_TO_PRODUCT_SETTINGS = "⬅ 🛠";
        String FILTERS_SETTINGS = "🔴 фильтры уведомлений";
        String ADD_FILTER = "➕ добавить фильтр";
        String MY_FILTERS = "📘 мои фильтры";
        String DELETE_FILTER = "🗑";
    }

    interface Callback {
        String MY_PRODUCTS = "products";
        String MY_PROMOS = "promos";
        String APPLY_PROMO = "apply-promo";
        String DELETE_PROMO = "delete-promo";
        String KEYBOARD_PAGES = "pages";
        String NOTIFICATIONS_SETTINGS = "notification-settings";
        String BACK = "back";
        String EMPTY = "empty";
        String BACK_TO_PRODUCT_SETTINGS = "to-product-settings";
        String PRODUCT_NOTIFICATIONS = "product-notifications";
        String PRODUCT_SETTINGS = "product-settings";
        String DELETE_PRODUCT = "delete-product";
        String DELETE_FILTER = "delete-filter";
        String CONFIRM_DELETE = "confirm";
        String DECLINE_DELETE = "decline";
    }
}
