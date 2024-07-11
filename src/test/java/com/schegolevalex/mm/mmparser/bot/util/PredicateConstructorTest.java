package com.schegolevalex.mm.mmparser.bot.util;

import com.schegolevalex.mm.mmparser.entity.*;
import com.schegolevalex.mm.mmparser.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class PredicateConstructorTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private PredicateConstructor predicateConstructor;

    private Offer offer;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .chatId(1123124L)
                .nickname("nickname")
                .firstName("firstName")
                .lastName("lastName")
                .isPremium(false)
                .cashbackLevel(2)
                .build();

        offer = Offer.builder()
                .price(100_000)
                .bonus(22_000)
                .bonusPercent(22)
                .product(Product.builder()
                        .url("url")
                        .title("title")
                        .user(user)
                        .promo(Promo.builder()
                                .promoSteps(List.of(PromoStep.builder()
                                                .priceFrom(110_000)
                                                .discount(20_000)
                                                .build(),
                                        PromoStep.builder()
                                                .priceFrom(80_000)
                                                .discount(10_000)
                                                .build()))
                                .build())
                        .build())
                .build();

        lenient().when(userService.findByChatId(anyLong())).thenReturn(Optional.of(user));
    }

    @Test
    void testCreateFromFilter_PriceWithPromo_LessOrEquals() {
        Filter filter = new Filter();
        filter.setField(FilterField.PRICE_TOTAL);
        filter.setOperation(Operation.LESS_OR_EQUALS);
        filter.setValue(80_000);


        assertTrue(predicateConstructor.createFromFilter(filter).test(offer));
    }

    @Test
    void testCreateFromFilter_PriceTotal_GreaterOrEquals() {
        Filter filter = new Filter();
        filter.setField(FilterField.PRICE_TOTAL);
        filter.setOperation(Operation.GREATER_OR_EQUALS);
        filter.setValue(10_000);

        assertTrue(predicateConstructor.createFromFilter(filter).test(offer));
    }

    @Test
    void testCreateFromFilter_Price_Equals() {
        Filter filter = new Filter();
        filter.setField(FilterField.PRICE);
        filter.setOperation(Operation.EQUALS);
        filter.setValue(100_000);

        assertTrue(predicateConstructor.createFromFilter(filter).test(offer));
    }

    @Test
    void testCalculatePrice_WithPromo() {
        assertEquals(68_400, predicateConstructor.calculatePrice(offer, true));
    }

    @Test
    void testCalculatePrice_WithoutPromo() {
        assertEquals(76_000, predicateConstructor.calculatePrice(offer, false));
    }
}