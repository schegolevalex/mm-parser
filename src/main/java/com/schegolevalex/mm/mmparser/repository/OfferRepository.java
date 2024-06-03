package com.schegolevalex.mm.mmparser.repository;

import com.schegolevalex.mm.mmparser.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    @Query("select o from Offer o " +
            "where o.price = :price " +
            "and o.bonusPercent = :bonusPercent " +
            "and o.bonus = :bonus " +
            "and o.product.url = :url " +
            "and o.seller.marketId = :marketId")
    @Transactional
    Optional<Offer> findExist(Integer price, Integer bonusPercent, Integer bonus, String url, String marketId);

    // todo сделать отдельный метод на проверку по Delivery
}
