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

    public Predicate<Offer> createFromFilter(Filter filter) {
        return switch (filter.getField()) {
            case PRICE_WITH_PROMO -> switch (filter.getOperation()) {
                case LESS_OR_EQUALS -> offer -> offerService.calculatePrice(offer, false) <= filter.getValue();
                case EQUALS -> offer -> offerService.calculatePrice(offer, false) == filter.getValue();
                case GREATER_OR_EQUALS -> offer -> offerService.calculatePrice(offer, false) >= filter.getValue();
            };
            case PRICE_TOTAL -> switch (filter.getOperation()) {
                case LESS_OR_EQUALS -> offer -> offerService.calculatePrice(offer, true) <= filter.getValue();
                case EQUALS -> offer -> offerService.calculatePrice(offer, true) == filter.getValue();
                case GREATER_OR_EQUALS -> offer -> offerService.calculatePrice(offer, true) >= filter.getValue();
            };
            case PRICE -> createPredicate(filter, Offer::getPrice);
            case BONUS -> createPredicate(filter, Offer::getBonus);
            case BONUS_PERCENT -> createPredicate(filter, Offer::getBonusPercent);
        };
    }

    private static Predicate<Offer> createPredicate(Filter filter, Function<Offer, Integer> getter) {
        int value = filter.getValue();
        return switch (filter.getOperation()) {
            case LESS_OR_EQUALS -> offer -> getter.apply(offer) <= value;
            case EQUALS -> offer -> getter.apply(offer).equals(value);
            case GREATER_OR_EQUALS -> offer -> getter.apply(offer) >= value;
        };
    }
}