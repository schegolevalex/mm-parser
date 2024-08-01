package com.schegolevalex.mm.mmparser.bot.util;

import com.schegolevalex.mm.mmparser.entity.Filter;
import com.schegolevalex.mm.mmparser.entity.FilterField;
import com.schegolevalex.mm.mmparser.entity.Offer;
import com.schegolevalex.mm.mmparser.entity.Operation;
import com.schegolevalex.mm.mmparser.service.OfferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PredicateConstructorTest {

    @Mock
    private OfferService offerService;

    @InjectMocks
    private PredicateConstructor predicateConstructor;

    @Mock
    private Offer offer;

    @BeforeEach
    void setUp() {
        when(offerService.calculatePriceWithPromoAndBonuses(any())).thenReturn(79_000);
        when(offerService.calculatePriceWithPromo(any())).thenReturn(90_000);
        when(offer.getPrice()).thenReturn(100_000);
        when(offer.getBonus()).thenReturn(22_000);
        when(offer.getBonusPercent()).thenReturn(22);
    }

    @Test
    void testFromFilter_PriceWithPromoAndBonuses_LessOrEquals1() {
        Filter filter = Filter.builder()
                .field(FilterField.PRICE_WITH_PROMO_AND_BONUSES)
                .operation(Operation.LESS_OR_EQUALS)
                .value(80_000)
                .build();

        assertTrue(predicateConstructor.fromFilter(filter).test(offer));
        verify(offerService).calculatePriceWithPromoAndBonuses(offer);
    }

    @Test
    void testFromFilter_PriceWithPromoAndBonuses_LessOrEquals2() {
        Filter filter = Filter.builder()
                .field(FilterField.PRICE_WITH_PROMO_AND_BONUSES)
                .operation(Operation.LESS_OR_EQUALS)
                .value(79_000)
                .build();

        assertTrue(predicateConstructor.fromFilter(filter).test(offer));
        verify(offerService).calculatePriceWithPromoAndBonuses(offer);
    }

    @Test
    void testFromFilter_PriceWithPromoAndBonuses_GreaterOrEquals1() {
        Filter filter = Filter.builder()
                .field(FilterField.PRICE_WITH_PROMO_AND_BONUSES)
                .operation(Operation.GREATER_OR_EQUALS)
                .value(78_000)
                .build();

        assertTrue(predicateConstructor.fromFilter(filter).test(offer));
        verify(offerService).calculatePriceWithPromoAndBonuses(offer);
    }

    @Test
    void testFromFilter_PriceWithPromoAndBonuses_GreaterOrEquals2() {
        Filter filter = Filter.builder()
                .field(FilterField.PRICE_WITH_PROMO_AND_BONUSES)
                .operation(Operation.GREATER_OR_EQUALS)
                .value(79_000)
                .build();

        assertTrue(predicateConstructor.fromFilter(filter).test(offer));
        verify(offerService).calculatePriceWithPromoAndBonuses(offer);
    }

    @Test
    void testFromFilter_PriceWithPromoAndBonuses_Equals() {
        Filter filter = Filter.builder()
                .field(FilterField.PRICE_WITH_PROMO_AND_BONUSES)
                .operation(Operation.EQUALS)
                .value(79_000)
                .build();

        assertTrue(predicateConstructor.fromFilter(filter).test(offer));
        verify(offerService).calculatePriceWithPromoAndBonuses(offer);
    }

    @Test
    void testFromFilter_PriceWithPromo_LessOrEquals1() {
        Filter filter = Filter.builder()
                .field(FilterField.PRICE_WITH_PROMO)
                .operation(Operation.LESS_OR_EQUALS)
                .value(91_000)
                .build();

        assertTrue(predicateConstructor.fromFilter(filter).test(offer));
        verify(offerService).calculatePriceWithPromo(offer);
    }

    @Test
    void testFromFilter_PriceWithPromo_LessOrEquals2() {
        Filter filter = Filter.builder()
                .field(FilterField.PRICE_WITH_PROMO)
                .operation(Operation.LESS_OR_EQUALS)
                .value(90_000)
                .build();

        assertTrue(predicateConstructor.fromFilter(filter).test(offer));
        verify(offerService).calculatePriceWithPromo(offer);
    }

    @Test
    void testFromFilter_PriceWithPromo_GreaterOrEquals1() {
        Filter filter = Filter.builder()
                .field(FilterField.PRICE_WITH_PROMO)
                .operation(Operation.GREATER_OR_EQUALS)
                .value(89_000)
                .build();

        assertTrue(predicateConstructor.fromFilter(filter).test(offer));
        verify(offerService).calculatePriceWithPromo(offer);
    }

    @Test
    void testFromFilter_PriceWithPromo_GreaterOrEquals2() {
        Filter filter = Filter.builder()
                .field(FilterField.PRICE_WITH_PROMO)
                .operation(Operation.GREATER_OR_EQUALS)
                .value(90_000)
                .build();

        assertTrue(predicateConstructor.fromFilter(filter).test(offer));
        verify(offerService).calculatePriceWithPromo(offer);
    }

    @Test
    void testFromFilter_PriceWithPromo_Equals() {
        Filter filter = Filter.builder()
                .field(FilterField.PRICE_WITH_PROMO)
                .operation(Operation.EQUALS)
                .value(90_000)
                .build();

        assertTrue(predicateConstructor.fromFilter(filter).test(offer));
        verify(offerService).calculatePriceWithPromo(offer);
    }

    @Test
    void testFromFilter_Price_LessOrEquals1() {
        Filter filter = Filter.builder()
                .field(FilterField.PRICE)
                .operation(Operation.LESS_OR_EQUALS)
                .value(101_000)
                .build();

        assertTrue(predicateConstructor.fromFilter(filter).test(offer));
    }

    @Test
    void testFromFilter_Price_LessOrEquals2() {
        Filter filter = Filter.builder()
                .field(FilterField.PRICE)
                .operation(Operation.LESS_OR_EQUALS)
                .value(100_000)
                .build();

        assertTrue(predicateConstructor.fromFilter(filter).test(offer));
    }

    @Test
    void testFromFilter_Price_GreaterOrEquals1() {
        Filter filter = Filter.builder()
                .field(FilterField.PRICE)
                .operation(Operation.GREATER_OR_EQUALS)
                .value(99_000)
                .build();

        assertTrue(predicateConstructor.fromFilter(filter).test(offer));
    }

    @Test
    void testFromFilter_Price_GreaterOrEquals2() {
        Filter filter = Filter.builder()
                .field(FilterField.PRICE)
                .operation(Operation.GREATER_OR_EQUALS)
                .value(100_000)
                .build();

        assertTrue(predicateConstructor.fromFilter(filter).test(offer));
    }

    @Test
    void testFromFilter_Price_Equals() {
        Filter filter = Filter.builder()
                .field(FilterField.PRICE)
                .operation(Operation.EQUALS)
                .value(100_000)
                .build();

        assertTrue(predicateConstructor.fromFilter(filter).test(offer));
    }

    @Test
    void testFromFilter_Bonus_LessOrEquals1() {
        Filter filter = Filter.builder()
                .field(FilterField.BONUS)
                .operation(Operation.LESS_OR_EQUALS)
                .value(23_000)
                .build();

        assertTrue(predicateConstructor.fromFilter(filter).test(offer));
    }

    @Test
    void testFromFilter_Bonus_LessOrEquals2() {
        Filter filter = Filter.builder()
                .field(FilterField.BONUS)
                .operation(Operation.LESS_OR_EQUALS)
                .value(22_000)
                .build();

        assertTrue(predicateConstructor.fromFilter(filter).test(offer));
    }

    @Test
    void testFromFilter_Bonus_GreaterOrEquals1() {
        Filter filter = Filter.builder()
                .field(FilterField.BONUS)
                .operation(Operation.GREATER_OR_EQUALS)
                .value(21_000)
                .build();

        assertTrue(predicateConstructor.fromFilter(filter).test(offer));
    }

    @Test
    void testFromFilter_Bonus_GreaterOrEquals2() {
        Filter filter = Filter.builder()
                .field(FilterField.BONUS)
                .operation(Operation.GREATER_OR_EQUALS)
                .value(22_000)
                .build();

        assertTrue(predicateConstructor.fromFilter(filter).test(offer));
    }

    @Test
    void testFromFilter_Bonus_Equals() {
        Filter filter = Filter.builder()
                .field(FilterField.BONUS)
                .operation(Operation.EQUALS)
                .value(22_000)
                .build();

        assertTrue(predicateConstructor.fromFilter(filter).test(offer));
    }

    @Test
    void testFromFilter_BonusPercent_LessOrEquals1() {
        Filter filter = Filter.builder()
                .field(FilterField.BONUS_PERCENT)
                .operation(Operation.LESS_OR_EQUALS)
                .value(23)
                .build();

        assertTrue(predicateConstructor.fromFilter(filter).test(offer));
    }

    @Test
    void testFromFilter_BonusPercent_LessOrEquals2() {
        Filter filter = Filter.builder()
                .field(FilterField.BONUS_PERCENT)
                .operation(Operation.LESS_OR_EQUALS)
                .value(22)
                .build();

        assertTrue(predicateConstructor.fromFilter(filter).test(offer));
    }

    @Test
    void testFromFilter_BonusPercent_GreaterOrEquals1() {
        Filter filter = Filter.builder()
                .field(FilterField.BONUS_PERCENT)
                .operation(Operation.GREATER_OR_EQUALS)
                .value(21)
                .build();

        assertTrue(predicateConstructor.fromFilter(filter).test(offer));
    }

    @Test
    void testFromFilter_BonusPercent_GreaterOrEquals2() {
        Filter filter = Filter.builder()
                .field(FilterField.BONUS_PERCENT)
                .operation(Operation.GREATER_OR_EQUALS)
                .value(22)
                .build();

        assertTrue(predicateConstructor.fromFilter(filter).test(offer));
    }

    @Test
    void testFromFilter_BonusPercent_Equals() {
        Filter filter = Filter.builder()
                .field(FilterField.BONUS_PERCENT)
                .operation(Operation.EQUALS)
                .value(22)
                .build();

        assertTrue(predicateConstructor.fromFilter(filter).test(offer));
    }
}