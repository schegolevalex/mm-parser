package com.schegolevalex.mm.mmparser.bot;

import com.schegolevalex.mm.mmparser.bot.page.base.BasePage;
import com.schegolevalex.mm.mmparser.bot.page.base.Page;
import com.schegolevalex.mm.mmparser.entity.Filter;
import com.schegolevalex.mm.mmparser.entity.Promo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ContextTest {

    @Mock
    private ParserBot bot;

    @Mock
    private BasePage conversationPage;

    @Mock
    private BasePage mainPage;

    @InjectMocks
    private Context context;

    Long chatId = 1L;

    @BeforeEach
    void setUp() {
        List<BasePage> allPossiblePages = List.of(mainPage, conversationPage);
        context = new Context(allPossiblePages);
        when(bot.getContext()).thenReturn(context);
        when(mainPage.getPage()).thenReturn(Page.MAIN);
        when(conversationPage.getPage()).thenReturn(Page.NEW_CONVERSATION);
    }

    @Test
    void testPutPage() {
        context.putPage(chatId, Page.MAIN);

        assertEquals(Page.MAIN, context.peekPage(chatId).getPage());
    }

    @Test
    void testPutPage_whenPutDuplicatePage_ThenNotPut() {
        context.putPage(chatId, Page.MAIN);
        context.putPage(chatId, Page.MAIN);

        assertEquals(Page.MAIN, context.popPage(chatId).getPage());
        assertEquals(Page.NEW_CONVERSATION, context.popPage(chatId).getPage());
    }

    @Test
    void testPutPage_createsNewStackIfNotExists() {
        assertAll(() -> assertEquals(Page.NEW_CONVERSATION, context.popPage(chatId).getPage()));
    }

    @Test
    void testPutPage_stackShouldAlwaysBeNotEmpty() {
        assertAll(() -> assertEquals(Page.NEW_CONVERSATION, context.popPage(chatId).getPage()),
                () -> assertEquals(Page.NEW_CONVERSATION, context.popPage(chatId).getPage()),
                () -> assertEquals(Page.NEW_CONVERSATION, context.popPage(chatId).getPage()),
                () -> assertEquals(Page.NEW_CONVERSATION, context.popPage(chatId).getPage()));
    }

    @Test
    void testPeekPage() {
        context.putPage(chatId, Page.MAIN);
        assertEquals(Page.MAIN, context.peekPage(chatId).getPage());
    }

    @Test
    void testPopPage() {
        context.putPage(chatId, Page.MAIN);
        assertEquals(Page.MAIN, context.popPage(chatId).getPage());
    }

    @Test
    void testIsActiveUser() {
        assertFalse(context.isActiveUser(chatId));
        context.putPage(chatId, Page.MAIN);
        assertTrue(context.isActiveUser(chatId));
    }

    @Test
    void testClearPage() {
        context.putPage(chatId, Page.MAIN);
        context.clearPages(chatId);
        assertEquals(Page.NEW_CONVERSATION, context.peekPage(chatId).getPage());
    }

    @Test
    void putPromo() {
        Promo promo = new Promo();
        context.putPromo(chatId, promo);
        assertEquals(promo, context.getPromo(chatId));
    }

    @Test
    void testGetPromo() {
        Promo promo = new Promo();
        context.putPromo(chatId, promo);
        assertEquals(promo, context.getPromo(chatId));
    }

    @Test
    void testClearPromo() {
        context.putPromo(chatId, new Promo());
        context.clearPromo(chatId);
        assertNull(context.getPromo(chatId));
    }

    @Test
    void putFilter() {
        Filter filter = new Filter();
        context.putFilter(chatId, filter);
        assertEquals(filter, context.getFilter(chatId));
    }

    @Test
    void testGetFilter() {
        Filter filter = new Filter();
        context.putFilter(chatId, filter);
        assertEquals(filter, context.getFilter(chatId));
    }

    @Test
    void testClearFilter() {
        context.putFilter(chatId, new Filter());
        context.clearFilter(chatId);
        assertNull(context.getFilter(chatId));
    }

    @Test
    void testClear() {
        context.putPage(chatId, Page.MAIN);
        context.putPromo(chatId, new Promo());
        context.putFilter(chatId, new Filter());
        context.clear(chatId);
        assertEquals(Page.NEW_CONVERSATION, context.peekPage(chatId).getPage());
        assertNull(context.getPromo(chatId));
        assertNull(context.getFilter(chatId));
    }
}