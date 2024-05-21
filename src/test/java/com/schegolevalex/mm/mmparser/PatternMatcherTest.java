package com.schegolevalex.mm.mmparser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternMatcherTest {
    @Test
    public void test() {
        String input = "khbh https://megamarket.ru/catalog/details/apple-iphone-15-pro-128gb-natural-titanium-100062422149/ лроп";
        String expectedUrl = "https://megamarket.ru/catalog/details/apple-iphone-15-pro-128gb-natural-titanium-100062422149/";

        String urlRegexp = "(http(s)?://.)?(www\\.)?[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_+.~#?&/=]*)";
        Pattern pattern = Pattern.compile(urlRegexp);

        Matcher matcher = pattern.matcher(input);
        String url = "";
        if (matcher.find())
            url = matcher.group();

        Assertions.assertEquals(expectedUrl, url);
    }
}
