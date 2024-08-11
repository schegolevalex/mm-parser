package com.schegolevalex.mm.mmparser.service;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.util.PredicateConstructor;
import com.schegolevalex.mm.mmparser.entity.Filter;
import com.schegolevalex.mm.mmparser.entity.Offer;
import com.schegolevalex.mm.mmparser.entity.Promo;
import com.schegolevalex.mm.mmparser.entity.PromoStep;
import com.schegolevalex.mm.mmparser.repository.OfferRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.schegolevalex.mm.mmparser.bot.util.MessageUtil.prepareToMarkdownV2;

@Service
@Transactional
public class OfferService {
    private final OfferRepository offerRepository;
    private final PredicateConstructor predicateConstructor;

    public OfferService(OfferRepository offerRepository,
                        @Lazy PredicateConstructor predicateConstructor) {
        this.offerRepository = offerRepository;
        this.predicateConstructor = predicateConstructor;
    }

    public Boolean isPassFilters(Offer offer) {
        Set<Filter> filters = offer.getProduct().getFilters();
        return filters.stream()
                .allMatch(filter -> predicateConstructor.fromFilter(filter).test(offer));
    }

    public Optional<Offer> findExist(Offer offer) {
        return offerRepository.findExist(offer);
    }

    public Offer save(Offer newOffer) {
        return offerRepository.save(newOffer);
    }

    public List<Offer> saveAll(List<Offer> offers) {
        return offerRepository.saveAllAndFlush(offers);
    }

    public int calculatePriceWithPromoAndBonuses(Offer offer) {
        Integer priceBefore = offer.getPrice();
        double bonusPercent = offer.getBonusPercent() / 100.0;
        int promoDiscount = calculatePromoDiscount(offer.getProduct().getPromo(), priceBefore);
        Integer cashbackLevel = offer.getProduct().getUser().getCashbackLevel();
        double sberprime = (priceBefore - promoDiscount) * cashbackLevel / 100.0 > 2_000 ? 2_000 : ((priceBefore - promoDiscount) * cashbackLevel / 100.0);

        return (int) ((1 - bonusPercent) * (priceBefore - promoDiscount) - sberprime);
    }

    public int calculatePriceWithPromo(Offer offer) {
        Integer priceBefore = offer.getPrice();
        int promoDiscount = calculatePromoDiscount(offer.getProduct().getPromo(), priceBefore);

        return priceBefore - promoDiscount;
    }

    private int calculatePromoDiscount(Promo promo, Integer priceBefore) {
        AtomicInteger promoDiscount = new AtomicInteger();
        if (promo != null) {
            promo.getPromoSteps().stream()
                    .filter(promoStep -> priceBefore >= promoStep.getPriceFrom())
                    .max(Comparator.comparing(PromoStep::getDiscount))
                    .ifPresent(promoStep -> promoDiscount.set(promoStep.getDiscount()));
        }
        return promoDiscount.get();
    }

    public String getOfferMessage(Offer offer) {
        return String.format(Constant.Message.OFFER,
                prepareToMarkdownV2(offer.getProduct().getTitle()),
                prepareToMarkdownV2(offer.getProduct().getUrl()),
                calculatePriceWithPromoAndBonuses(offer),
                prepareToMarkdownV2(offer.getSeller().getName()),
                offer.getPrice(),
                offer.getBonusPercent(),
                offer.getBonus());
    }

    public Optional<Offer> findById(Long offerId) {
        return offerRepository.findById(offerId);
    }

    public List<Offer> findByParseId(UUID parseId) {
        return offerRepository.findByParseId(parseId);
    }
}