package com.schegolevalex.mm.mmparser.service;

import com.schegolevalex.mm.mmparser.entity.Delivery;
import com.schegolevalex.mm.mmparser.entity.Offer;
import com.schegolevalex.mm.mmparser.entity.Product;
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

    public List<Offer> saveAllAndFlush(List<Offer> offers) {
        return offerRepository.saveAllAndFlush(offers);
    }

    public List<Offer> filterOffersWithDefaultParameters(List<Offer> offers) {
        return offers.stream().filter(offer -> {
            Integer priceBefore = offer.getPrice();
            double bonusPercent = offer.getBonusPercent() / 100.0;
            int promo = priceBefore > 110_000 ? 20_000 : 10_000;

            double sberprime = priceBefore * bonusPercent > 2_000 ? 2_000 : (priceBefore * bonusPercent);

            boolean totalPrice = (priceBefore - promo - (priceBefore - promo) * bonusPercent - sberprime) < 79_000;
            boolean scam = priceBefore > 100_000;
            return totalPrice && scam;
        }).toList();
    }

    public boolean isPresent(Product product, Offer offer) {
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
                product.getUrl(),
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