package com.schegolevalex.mm.mmparser.repository;

import com.schegolevalex.mm.mmparser.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller> findByName(String sellerName);

    Optional<Seller> findByNameAndRatingAndOgrn(String sellerName, Double rating, String ogrn);

    Optional<Seller> findByMarketId(String marketId);
}
