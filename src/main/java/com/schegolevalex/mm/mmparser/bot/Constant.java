package com.schegolevalex.mm.mmparser.bot;

public interface Constant {
    interface Info {
        //        String CHAT_STATES = "chatStates";
        String BOT_DESCRIPTION = "mm-bot";
        String START = "начать работы";
        String STOP = "завершить работу";
    }

    interface Message {
        String CHOOSE_ACTION = "Выберите действие:";
        String SUGGESTION_TO_LINK_INPUT = "Введите ссылку:";
        String LINKS_IS_EMPTY = "Вы еще не добавили ни одну ссылку";
        String WELCOME = "Привет, начинаем?";
        String LINK_IS_ACCEPTED = "Ссылка принята! Можете добавить еще одну или вернуться назад";
        String UNEXPECTED_INPUT = "Я ещё не знаю как отвечать на такое...";
        String BYE = "👋";
        String OFFER = """
                Предложение (~%d рублей):\n
                - продавец: "%s"\n
                - цена: %d руб\n
                - процент бонусов: %d%%\n
                - количество бонусов: %d\n
                - ссылка: %s""";
        String CHOOSE_SETTINGS = "Что настроим?";
        String PROMOS_IS_EMPTY = "Список промокодов пуст";
        String CHOOSE_YOUR_CASHBACK_LEVEL = "Выберите Ваш уровень Сберпрайм на текущий месяц";
    }

    interface Button {
        String MY_LINKS = "📝 мои ссылки";
        String ADD_LINK = "➕ добавить ссылку";
        String BACK = "⬅ назад";
        String MAIN_PAGE = "🏠 на главную";
        String OK = "OK";
        String START_CONVERSATION = "начинаем";
        String SETTINGS = "⚙️ настройки";
        String PROMOS_SETTINGS = "🔵 промокоды";
        String CASHBACK_SETTINGS = "🟢 кэшбэк Сберпрайм";
        String ADD_PROMO = "➕ добавить промокод";
        String MY_PROMOS = "📝 мои промокоды";
    }

//    interface Callback {
//        String GO_TO_MAIN_PAGE = "/main";
//        String GO_TO_MY_REMINDERS = "/reminders";
//        String GO_TO_MY_REMINDER = "/reminders/";
//        String GO_TO_ADD_REMINDER = "/addReminder";
//        String GO_TO_EDIT_REMINDER_TEXT = "/editReminderText/";
//        String GO_TO_EDIT_REMINDER_DATE = "/editReminderDate/";
//        String GO_TO_EDIT_REMINDER_TIME = "/editReminderTime/";
//        String GO_TO_CONFIRM_TO_DELETE_REMINDER = "/confirmToDeleteReminder/";
//        String GO_TO_CONFIRMED_DELETION = "/confirmedDeletion/";
//        String GO_BACK = "/back";
//        String OK = "/ok";
//        String EMPTY = "/empty";
//    }
}
