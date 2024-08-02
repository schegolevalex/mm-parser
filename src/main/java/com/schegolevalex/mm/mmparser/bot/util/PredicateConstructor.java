package com.schegolevalex.mm.mmparser.bot.util;

import com.schegolevalex.mm.mmparser.entity.Filter;
import com.schegolevalex.mm.mmparser.entity.Offer;
import com.schegolevalex.mm.mmparser.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Transactional
public class PredicateConstructor {
    private final OfferService offerService;

    public Predicate<Offer> fromFilter(Filter filter) {
        return switch (filter.getField()) {
            case PRICE_WITH_PROMO -> createPredicate(filter, offerService::calculatePriceWithPromo);
            case PRICE_WITH_PROMO_AND_BONUSES ->
                    createPredicate(filter, offerService::calculatePriceWithPromoAndBonuses);
            case PRICE -> createPredicate(filter, Offer::getPrice);
            case BONUS -> createPredicate(filter, Offer::getBonus);
            case BONUS_PERCENT -> createPredicate(filter, Offer::getBonusPercent);
        };
    }

    private static Predicate<Offer> createPredicate(Filter filter, Function<Offer, Integer> function) {
        return switch (filter.getOperation()) {
            case LESS_OR_EQUALS -> offer -> function.apply(offer) <= filter.getValue();
            case EQUALS -> offer -> function.apply(offer).equals(filter.getValue());
            case GREATER_OR_EQUALS -> offer -> function.apply(offer) >= filter.getValue();
        };
    }
}