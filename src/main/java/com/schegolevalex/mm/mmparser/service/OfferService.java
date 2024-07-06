package com.schegolevalex.mm.mmparser.service;

import com.schegolevalex.mm.mmparser.bot.util.PredicateConstructor;
import com.schegolevalex.mm.mmparser.entity.Delivery;
import com.schegolevalex.mm.mmparser.entity.Offer;
import com.schegolevalex.mm.mmparser.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class OfferService {
    private final OfferRepository offerRepository;
    private final PredicateConstructor predicateConstructor;

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

    public boolean isPresent(Offer offer) {
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
        Optional<Offer> exist = offerRepository.findExist(offer.getPrice(),
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
        return exist.isPresent();
    }

    public Offer save(Offer newOffer) {
        return offerRepository.save(newOffer);
    }

    public List<Offer> saveAll(List<Offer> offers) {
        return offerRepository.saveAll(offers);
    }
}