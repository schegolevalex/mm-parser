package com.schegolevalex.mm.mmparser.service;

import com.schegolevalex.mm.mmparser.bot.Constant;
import com.schegolevalex.mm.mmparser.bot.util.PredicateConstructor;
import com.schegolevalex.mm.mmparser.entity.Delivery;
import com.schegolevalex.mm.mmparser.entity.Offer;
import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.entity.PromoStep;
import com.schegolevalex.mm.mmparser.repository.OfferRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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

    public List<Offer> filterOffersWithDefaultParameters(List<Offer> offers) {
        return offers.stream()
                .filter(offer -> {
                    Integer priceBefore = offer.getPrice();
                    double bonusPercent = offer.getBonusPercent() / 100.0;
                    int promo = priceBefore > 110_000 ? 20_000 : 10_000;

                    double sberprime = priceBefore * bonusPercent > 2_000 ? 2_000 : (priceBefore * bonusPercent);

                    boolean totalPrice = (priceBefore - promo - (priceBefore - promo) * bonusPercent - sberprime) < 79_000;
                    boolean scam = priceBefore > 100_000;
                    return totalPrice && scam;
                })
                .toList();
    }

    public List<Offer> filterOffers(List<Offer> offers) {
        return offers.stream()
                .filter(offer -> offer.getProduct().getFilters().stream()
                        .allMatch(filter -> predicateConstructor.createFromFilter(filter).test(offer)))
                .toList();
    }

    public Optional<Offer> findExist(Offer offer) {
        // todo стоит исключить отсюда даты доставки, чтобы не создавался новый элемент только из-за изменения даты доставки
        Delivery delivery = offer.getDelivery();
        String marketId = offer.getSeller().getMarketId();
        String clickCourierDate = delivery.getClickCourierDate();
        Integer clickCourierPrice = delivery.getClickCourierPrice();
        String pickupDate = delivery.getPickupDate();
        Integer pickupPrice = delivery.getPickupPrice();
        String storeDate = delivery.getStoreDate();
        Integer storePrice = delivery.getStorePrice();
        String courierDate = delivery.getCourierDate();
        Integer courierPrice = delivery.getCourierPrice();
        return offerRepository.findExist(offer.getPrice(),
                offer.getBonusPercent(),
                offer.getBonus(),
                offer.getProduct().getUrl(),
                marketId,
                clickCourierDate,
                clickCourierPrice,
                pickupDate,
                pickupPrice,
                storeDate,
                storePrice,
                courierDate,
                courierPrice);
    }

    public Offer save(Offer newOffer) {
        return offerRepository.save(newOffer);
    }

    public List<Offer> saveAll(List<Offer> offers) {
        return offerRepository.saveAll(offers);
    }

    public List<Offer> findAllForSpecifiedTime(Product product, Integer timeAgo, ChronoUnit unit) {
        Instant minutesAgo = Instant.now().minus(timeAgo, unit);
        return offerRepository.findAllByProductAndUpdatedAtGreaterThanEqual(product, minutesAgo);
    }

    public int calculatePrice(Offer offer, boolean withPromo) {

        Integer priceBefore = offer.getPrice();
        double bonusPercent = offer.getBonusPercent() / 100.0;

        AtomicInteger promoDiscount = new AtomicInteger();
        if (withPromo && offer.getProduct().getPromo() != null) {
            offer.getProduct().getPromo().getPromoSteps().stream()
                    .filter(promoStep -> priceBefore >= promoStep.getPriceFrom())
                    .max(Comparator.comparing(PromoStep::getDiscount))
                    .ifPresent(promoStep -> promoDiscount.set(promoStep.getDiscount()));
        }

        Integer cashbackLevel = offer.getProduct().getUser().getCashbackLevel();
        double sberprime = (priceBefore - promoDiscount.get()) * cashbackLevel / 100.0 > 2_000 ? 2_000 : ((priceBefore - promoDiscount.get()) * cashbackLevel / 100.0);
        return (int) ((1 - bonusPercent) * (priceBefore - promoDiscount.get()) - sberprime);
    }

    public String getOfferMessage(Offer offer) {
        return String.format(Constant.Message.OFFER,
                prepareToMarkdownV2(offer.getProduct().getTitle()),
                prepareToMarkdownV2(offer.getProduct().getUrl()),
                calculatePrice(offer, true),
                prepareToMarkdownV2(offer.getSeller().getName()),
                offer.getPrice(),
                offer.getBonusPercent(),
                offer.getBonus());
    }
}