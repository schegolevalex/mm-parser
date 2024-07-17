package com.schegolevalex.mm.mmparser.bot.util;

public class MessageUtil {
    public static String prepareToMarkdownV2(String text) {
        return text
                .replace("|", "\\|")
                .replace("!", "\\!")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("-", "\\-")
                .replace("_", "\\_")
                .replace(".", "\\.")
                .replace("+", "\\+");
    }
}
