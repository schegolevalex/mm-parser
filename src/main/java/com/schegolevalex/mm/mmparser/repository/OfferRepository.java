package com.schegolevalex.mm.mmparser.repository;

import com.schegolevalex.mm.mmparser.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    @Query(
            "SELECT o FROM Offer o " +
                    "WHERE o.price = :price " +
                    "AND o.bonusPercent = :bonusPercent " +
                    "AND o.bonus = :bonus " +
                    "AND o.product.url = :url " +
                    "AND o.seller.marketId = :marketId " +
                    "AND (COALESCE(o.delivery.clickCourierDate, '') = :clickCourierDate or :clickCourierDate IS NULL) " +
                    "AND (COALESCE(o.delivery.clickCourierPrice, 0) = :clickCourierPrice or :clickCourierPrice IS NULL) " +
                    "AND (COALESCE(o.delivery.pickupDate, '') = :pickupDate or :pickupDate IS NULL) " +
                    "AND (COALESCE(o.delivery.pickupPrice, 0) = :pickupPrice or :pickupPrice IS NULL) " +
                    "AND (COALESCE(o.delivery.storeDate, '') = :storeDate or :storeDate IS NULL) " +
                    "AND (COALESCE(o.delivery.storePrice, 0) = :storePrice OR :storePrice IS NULL) " +
                    "AND (COALESCE(o.delivery.courierDate, '') = :courierDate OR :courierDate IS NULL) " +
                    "AND (COALESCE(o.delivery.courierPrice, 0) = :courierPrice OR :courierPrice IS NULL)")
    @Transactional
    Optional<Offer> findExist(Integer price, Integer bonusPercent, Integer bonus, String url, String marketId,
                              String clickCourierDate, Integer clickCourierPrice,
                              String pickupDate, Integer pickupPrice,
                              String storeDate, Integer storePrice,
                              String courierDate, Integer courierPrice);
}
