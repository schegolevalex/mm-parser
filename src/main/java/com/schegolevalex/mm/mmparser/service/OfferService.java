package com.schegolevalex.mm.mmparser.service;

import com.schegolevalex.mm.mmparser.entity.Offer;
import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional
@RequiredArgsConstructor
public class OfferService {
    private final OfferRepository offerRepository;

    public List<Offer> saveAllAndFlush(List<Offer> offers) {
        return offerRepository.saveAllAndFlush(offers);
    }

    public List<Offer> filterOffersWithDefaultParameters(List<Offer> offers) {
        return offers.stream().filter(offer -> {
            Integer priceBefore = offer.getPrice();
            double bonusPercent = (offer.getBonusPercent() + 2) / 100.0;
            int promo = priceBefore > 110_000 ? 20_000 : 10_000;

            boolean totalPrice = (priceBefore - promo - (priceBefore - promo) * bonusPercent) < 79_000;
            boolean scam = priceBefore > 100_000;
            return totalPrice && scam;
        }).toList();
    }

    public boolean isPresent(Product product, Offer offer) {
        return offerRepository.findTheSame(offer.getPrice(),
                        offer.getBonusPercent(),
                        offer.getBonus(),
                        product.getUrl(),
                        offer.getSeller().getName(),
                        offer.getSeller().getRating()).isPresent();
    }

    public Offer save(Offer newOffer) {
        return offerRepository.save(newOffer);
    }

    public List<Offer> saveAll(List<Offer> offers) {
        return offerRepository.saveAll(offers);
    }
}