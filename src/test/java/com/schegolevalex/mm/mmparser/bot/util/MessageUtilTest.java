package com.schegolevalex.mm.mmparser.bot.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageUtilTest {
    @Test
    public void testPrepareToMarkdownV2() {
        String input = "Hello, |world|! This is a (test) message - it's _important_.";
        String expected = "Hello, \\|world\\|\\! This is a \\(test\\) message \\- it's \\_important\\_\\.";

        String actual = MessageUtil.prepareToMarkdownV2(input);
        assertEquals(expected, actual);
    }

    @Test
    public void testPrepareToMarkdownV2WithMultipleSpecialCharacters() {
        String input = "Special characters: !()|_-+. Test 123.";
        String expected = "Special characters: \\!\\(\\)\\|\\_\\-\\+\\. Test 123\\.";

        String actual = MessageUtil.prepareToMarkdownV2(input);
        assertEquals(expected, actual);
    }

    @Test
    public void testPrepareToMarkdownV2WithNoSpecialCharacters() {
        String input = "This is a regular message with no special characters";
        String expected = "This is a regular message with no special characters";

        String actual = MessageUtil.prepareToMarkdownV2(input);
        assertEquals(expected, actual);
    }
}