package com.schegolevalex.mm.mmparser.bot;

public interface Constant {
    String DELIMITER = "/";

    interface Info {
        String BOT_DESCRIPTION = "mm-bot";
        String START = "начать работу";
        String STOP = "завершить работу";
    }

    interface Message {
        String CHOOSE_ACTION = "Выберите действие 🎯"; //todo
        String SUGGESTION_TO_LINK_INPUT = "Введите ссылку на товар 🔗"; //todo
        String LINKS_IS_EMPTY = "Вы еще не добавили ни один товар 😦";
        String WELCOME = "Привет, начинаем?";
        String LINK_IS_ACCEPTED = "Ссылка принята☺️\nМожете добавить еще одну или вернуться на главную🏠";
        String UNEXPECTED_INPUT = "не понимаю😶";
        String BYE = "👋";
        String OFFER = """
                Предложение (~%d рублей):\n
                - продавец: "%s"\n
                - цена: %d руб\n
                - процент бонусов: %d%%\n
                - количество бонусов: %d\n
                - ссылка: %s""";
        String CHOOSE_SETTINGS = "Что настроим? 🔧";
        String PROMOS_IS_EMPTY = "Вы еще не добавили ни один промокод 😦";
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
        String PROMO = "%d от %d рублей";
    }

    interface Button {
        String MY_LINKS = "🛒 мои товары";
        String ADD_LINK = "➕ добавить товар";
        String BACK = "⬅ назад";
        String MAIN_PAGE = "🏠 на главную";
        String OK = "👌 OK";
        String START_CONVERSATION = "🏃 начинаем";
        String SETTINGS = "⚙️ настройки";
        String PROMOS_SETTINGS = "🔵 промокоды";
        String CASHBACK_SETTINGS = "🟢 кэшбэк Сберпрайм";
        String ADD_PROMO = "➕ добавить промокод";
        String MY_PROMOS = "📘 мои промокоды";
        String YES_ADD_MORE_PROMO_STEPS = "⏭ да, добавить еще скидку в промокод";
        String NO_SAVE_PROMO = "💾 нет, сохранить промокод";
        String DELETE_PROMO = "🗑 удалить промокод";
    }
}
