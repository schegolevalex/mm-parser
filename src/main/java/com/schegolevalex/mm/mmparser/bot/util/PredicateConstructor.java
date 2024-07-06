package com.schegolevalex.mm.mmparser.bot.util;

import com.schegolevalex.mm.mmparser.entity.Filter;
import com.schegolevalex.mm.mmparser.entity.Offer;
import com.schegolevalex.mm.mmparser.entity.PromoStep;
import com.schegolevalex.mm.mmparser.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
@Transactional
public class PredicateConstructor {

    private final UserService userService;

    public PredicateConstructor(UserService userService) {
        this.userService = userService;
    }

    public Predicate<Offer> createFromFilter(Filter filter) {
        return switch (filter.getField()) {
            case PRICE_WITH_PROMO -> switch (filter.getOperation()) {
                case LESS_OR_EQUALS -> offer -> calculatePrice(offer, false) <= filter.getValue();
                case EQUALS -> offer -> calculatePrice(offer, false) == filter.getValue();
                case GREATER_OR_EQUALS -> offer -> calculatePrice(offer, false) >= filter.getValue();
            };
            case PRICE_TOTAL -> switch (filter.getOperation()) {
                case LESS_OR_EQUALS -> offer -> calculatePrice(offer, true) <= filter.getValue();
                case EQUALS -> offer -> calculatePrice(offer, true) == filter.getValue();
                case GREATER_OR_EQUALS -> offer -> calculatePrice(offer, true) >= filter.getValue();
            };
            case PRICE -> createPredicate(filter, Offer::getPrice);
            case BONUS -> createPredicate(filter, Offer::getBonus);
            case BONUS_PERCENT -> createPredicate(filter, Offer::getBonusPercent);
        };
    }

    private double calculatePrice(Offer offer, boolean withPromo) {
        Integer priceBefore = offer.getPrice();
        double bonusPercent = offer.getBonusPercent() / 100.0;

        AtomicInteger promoDiscount = new AtomicInteger();
        if (withPromo) {
            List<PromoStep> promoSteps = offer.getProduct().getPromo().getPromoSteps();
            promoSteps.stream()
                    .sorted(Comparator.comparing(PromoStep::getPriceFrom))
                    .filter(promoStep -> priceBefore >= promoStep.getPriceFrom())
                    .reduce((first, second) -> second)
                    .ifPresent(step -> promoDiscount.set(step.getDiscount()));
        }

        AtomicInteger cashbackLevel = new AtomicInteger();
        userService.findByChatId(offer.getProduct().getUser().getChatId())
                .ifPresent(user -> cashbackLevel.set(user.getCashbackLevel()));
        double sberprime = priceBefore * cashbackLevel.get() / 100.0 > 2_000 ? 2_000 : (priceBefore * cashbackLevel.get() / 100.0);
        return priceBefore - promoDiscount.get() - (priceBefore - promoDiscount.get()) * bonusPercent - sberprime;
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