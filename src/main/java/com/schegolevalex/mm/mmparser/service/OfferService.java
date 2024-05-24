package com.schegolevalex.mm.mmparser.service;

import com.schegolevalex.mm.mmparser.entity.Offer;
import com.schegolevalex.mm.mmparser.repository.OfferRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
            double bonusPercent = (offer.getBonusPercent() + 2) / 100.0;
            int promo = priceBefore > 110_000 ? 20_000 : 10_000;

            boolean totalPrice = (priceBefore - promo - (priceBefore - promo) * bonusPercent) < 79_000;
            boolean scam = priceBefore > 100_000;
            return totalPrice && scam;
        }).toList();
    }

    public Offer checkPrevious(Offer offer) {
        return offerRepository.save(offerRepository.checkPrevious(offer.getPrice(),
                        offer.getBonusPercent(),
                        offer.getBonus(),
                        offer.getProduct().getUrl(),
                        offer.getSeller().getName())
                .orElse(offer));
    }

    public Offer save(Offer newOffer) {
        return offerRepository.save(newOffer);
    }
}