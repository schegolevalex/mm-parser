package com.schegolevalex.mm.mmparser.repository;

import com.schegolevalex.mm.mmparser.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller> findByName(String sellerName);

    Optional<Seller> findByNameAndRatingAndOgrn(String sellerName, Double rating, String ogrn);

    Optional<Seller> findByMarketId(String marketId);
}
