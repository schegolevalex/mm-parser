package com.schegolevalex.mm.mmparser.service;

import com.schegolevalex.mm.mmparser.entity.Seller;
import com.schegolevalex.mm.mmparser.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
//@Transactional
public class SellerService {
    private final SellerRepository sellerRepository;

    public Optional<Seller> findByName(String sellerName) {
        return sellerRepository.findByName(sellerName);
    }

    public Optional<Seller> findByNameAndRating(String sellerName, Double rating, String ogrn) {
        return sellerRepository.findByNameAndRatingAndOgrn(sellerName, rating, ogrn);
    }

    public Optional<Seller> findByMarketId(String marketId) {
        return sellerRepository.findByMarketId(marketId);
    }

    public Seller save(Seller newSeller) {
        return sellerRepository.save(newSeller);
    }
}
