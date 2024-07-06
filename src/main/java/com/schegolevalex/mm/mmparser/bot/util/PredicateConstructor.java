package com.schegolevalex.mm.mmparser.bot.util;

import com.schegolevalex.mm.mmparser.entity.Filter;
import com.schegolevalex.mm.mmparser.entity.Offer;

import java.util.function.Function;
import java.util.function.Predicate;

public class PredicateConstructor {

    public Predicate<Offer> createPredicate(Filter filter) {
        return switch (filter.getField()) {
            case PRICE -> createIntegerPredicate(filter, Offer::getPrice);
            case BONUS -> createIntegerPredicate(filter, Offer::getBonus);
            case BONUS_PERCENT -> createIntegerPredicate(filter, Offer::getBonusPercent);
        };
    }

    private Predicate<Offer> createIntegerPredicate(Filter filter, Function<Offer, Integer> getter) {
        int value = filter.getValue();
        return switch (filter.getOperation()) {
            case LESS_OR_EQUALS -> offer -> getter.apply(offer) <= value;
//            case LESS -> offer -> getter.apply(offer) < value;
            case EQUALS -> offer -> getter.apply(offer).equals(value);
            case GREATER_OR_EQUALS -> offer -> getter.apply(offer) >= value;
//            case GREATER -> offer -> getter.apply(offer) > value;
        };
    }
}
