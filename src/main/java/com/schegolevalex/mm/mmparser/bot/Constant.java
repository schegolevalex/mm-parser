package com.schegolevalex.mm.mmparser.bot;

public interface Constant {
    interface Info {
        //        String CHAT_STATES = "chatStates";
        String BOT_DESCRIPTION = "mm-bot";
        String START = "начало работы";
    }

    interface Message {
        String CHOOSE_MAIN_PAGE_ACTION = "Выберите действие:";
        String SUGGESTION_TO_LINK_INPUT = "Введите ссылку:";
        String LINKS_IS_EMPTY = "Вы еще не добавили ни одну ссылку";
        String WELCOME = "Привет, начинаем?";
        String LINK_IS_ACCEPTED = "Ссылка принята";
        String WRONG_INPUT = "Я ещё не знаю как отвечать на такое...";
        String BYE = "👋";
        String OFFER = """
                Предложение (~%d рублей):\n
                - продавец: "%s"\n
                - цена: %d руб\n
                - процент бонусов: %d%%\n
                - количество бонусов: %d\n
                - ссылка: %s""";
    }

    interface Button {
        String MY_LINKS = "📝 Мои ссылки";
        String ADD_LINK = "➕ Добавить ссылку";
        String BACK = "⬅ Назад";
        String MAIN_PAGE = "🏠 На главную";
        String OK = "OK";
        String START_CONVERSATION = "Начинаем";

//        String EDIT_REMINDER_TEXT = "✏️ Изменить текст";
//        String EDIT_REMINDER_DATE = "📝 Изменить дату";
//        String EDIT_REMINDER_TIME = "🕙 Изменить время";
//        String DELETE_REMINDER = "❌ Удалить";
//        String CONFIRM_TO_DELETE_REMINDER = "🆗 Да, удалить напоминание";
//        String[] DAYS_OF_WEEK = new String[]{"ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС"};
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
