package com.schegolevalex.mm.mmparser.bot;

public interface Constant {
    String DELIMITER = "/";

    interface Info {
        String BOT_DESCRIPTION = """
                Помогу подобрать самый выгодный вариант для покупки на сайте megamarket.ru
                                
                Для вызова справки воспользуйтесь командой /help""";
        String START = "начать работу";
        String STOP = "завершить работу";
        String HELP = "справка по боту";
    }

    interface Message {
        String CHOOSE_ACTION = "Выберите действие 🎯";
        String SUGGESTION_TO_LINK_INPUT = "Введите ссылку на товар 🔗";
        String PRODUCTS_IS_EMPTY = "Вы еще не добавили ни один товар 😦";
        String PRODUCT_TITLE_WITH_LINK = "[%s](%s)";
        String LINK_IS_ACCEPTED = "Ссылка принята☺️\nМожете добавить еще одну или вернуться на главную🏠";
        String LINK_IS_NOT_VALID = """
                Такой тип ссылок не поддерживается😦
                Можете ввести другую ссылку или вернуться на главную🏠
                                
                ||ссылка должна начинаться с https://megamarket.ru/catalog/details/...||""";
        String BYE = "👋";
        String OFFER = """
                ❗️❗️❗️Новое предложение товара ["%s"](%s)\\.
                Цена с учетом бонусов, промокода и Вашего уровня Сберпрайм: __*%d*__ руб
                                
                \\- продавец: "%s"
                \\- цена: %d руб
                \\- процент бонусов: %d%%
                \\- количество бонусов: %d""";
        String CHOOSE_SETTINGS = "Что настроим? 🔧";
        String PROMOS_IS_EMPTY = "нет промокодов 😦";
        String CHOOSE_YOUR_CASHBACK_LEVEL = """
                Выберите Ваш уровень 🟢 Сберпрайм на текущий месяц\\.


                ||не забывайте обновлять его, так как он учитывается при расчете кэшбэка||""";
        String ADD_PROMO_STEP_PRICE = """
                Введите цену, от которой начинает действовать скидка:


                ||например, 500 от __*2000*__ рублей||""";
        String ADD_PROMO_STEP_DISCOUNT = """
                Введите сумму скидки:\


                ||например, __*500*__ от 2000 рублей||""";
        String ADD_PROMO_STEP_SUCCESSFUL = """
                Скидка %d от %d руб добавлена\\. Хотите добавить еще скидку к этому 🔵 промокоду?\


                ||обычно промокод содержит несколько скидок в зависимости от суммы покупки, например, \
                __*"500 от 2000 рублей, 800 от 3000 рублей, 1500 от 10000 рублей"*__\\. Всё это один промокод\\!||""";
        String PROMO = "%d/%d";
        String FILTERS_IS_EMPTY = "нет фильтров 😦";
        String ADD_FILTER_FIELD = "По чему будем фильтровать?";
        String ADD_FILTER_OPERATION = "Выберите условие фильтрации:";
        String ADD_FILTER_VALUE = "Введите значение фильтра:";
        String FILTER_ADDED = "Фильтр успешно добавлен";
        String NO_TITLE = "Название товара появится позже";
        String HELP = """
                Бот позволяет подобрать самый выгодный вариант для покупки на сайте megamarket.ru

                Как пользоваться ботом:

                1. Чтобы начать пользоваться ботом, выберите команду /start

                2. В настройках бота укажите Ваш текущий уровень 🟢 Сберпрайм (можно посмотреть в личном кабинете на сайте megamarket.ru)

                3. В настройках бота добавьте 🔵 промокод, если он у Вас имеется (можно посмотреть в личном кабинете на сайте megamarket.ru)

                4. В настройках бота добавьте 🔴 фильтры для последующего отсеивания ненужных предложений по товарам

                5. Добавьте ссылку на товар. При нажатии на кнопку "🛒 мои товары" можно посмотреть список добавленных товаров, а так же применить к товару фильтры и промокоды, добавленные раннее

                Бот будет присылать уведомления с предложениями, подходящими под Ваши параметры.

                Чтобы остановить бота, выберите команду /stop
                """;
    }

    interface Button {
        String MY_PRODUCTS = "🛒 мои товары";
        String ADD_PRODUCT = "➕ добавить товар";
        String BACK = "⬅ назад";
        String MAIN_PAGE = "🏠 на главную";
        String SETTINGS = "📚 настройки";
        String PROMOS_SETTINGS = "🔵 промокоды";
        String CASHBACK_SETTINGS = "🟢 кэшбэк Сберпрайм";
        String ADD_PROMO = "➕ добавить промокод";
        String YES_ADD_MORE_PROMO_STEPS = "⏭ да, добавить еще скидку в промокод";
        String NO_SAVE_PROMO = "💾 нет, сохранить промокод";
        String DELETE_PROMO = "🗑";
        String CONFIRM = "✅";
        String DECLINE = "⛔️";
        String APPLY_PROMO = "🔵 применить промокод";
        String APPLY_FILTER = "🔴 применить фильтр";
        String PREVIOUS_PAGE = "<<";
        String NEXT_PAGE = ">>";
        String EMPTY = " ";
        String PRODUCT_NOTIFICATIONS = "⭐️";
        String PRODUCT_SETTINGS = "⚙️";
        String DELETE_PRODUCT = "🗑";
        String BACK_TO_PRODUCT_SETTINGS = "⬅ ⚙️";
        String FILTERS_SETTINGS = "🔴 фильтр товаров";
        String ADD_FILTER = "➕ добавить фильтр";
        String DELETE_FILTER = "🗑";
        String PRICE = "цена";
        String PRICE_WITH_PROMO = "цена с промо";
        String PRICE_TOTAL = "цена с промо и бонусами";
        String BONUS = "количество бонусов";
        String BONUS_PERCENT = "процент бонусов";
        String LESS_OR_EQUALS = "≤";
        String EQUALS = "=";
        String GREATER_OR_EQUALS = "≥";
    }

    interface Callback {
        String MY_PRODUCTS = "products";
        String MY_PROMOS = "promos";
        String APPLY_PROMO = "apply-promo";
        String DELETE_PROMO = "delete-promo";
        String KEYBOARD_PAGES = "pages";
        String APPLY_FILTER = "apply-filter";
        String BACK = "back";
        String EMPTY = "empty";
        String BACK_TO_PRODUCT_SETTINGS = "to-product-settings";
        String PRODUCT_NOTIFICATIONS = "product-notifications";
        String PRODUCT_SETTINGS = "product-settings";
        String DELETE_PRODUCT = "delete-product";
        String DELETE_FILTER = "delete-filter";
        String CONFIRM_DELETE = "confirm";
        String DECLINE_DELETE = "decline";
        String MY_FILTERS = "filters";
    }
}
