package com.schegolevalex.mm.mmparser.repository;

import com.schegolevalex.mm.mmparser.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    @Query(
            "SELECT o FROM Offer o " +
            "WHERE o.price = :#{#offer.price} " +
            "AND o.bonusPercent = :#{#offer.bonusPercent} " +
            "AND o.bonus = :#{#offer.bonus} " +
            "AND o.product.url = :#{#offer.product.url} " +
            "AND o.seller.marketId = :#{#offer.seller.marketId} " +
            "AND (COALESCE(o.delivery.clickCourierDate, '') = :#{#offer.delivery.clickCourierDate} OR :#{#offer.delivery.clickCourierDate} IS NULL) " +
            "AND (COALESCE(o.delivery.clickCourierPrice, 0) = :#{#offer.delivery.clickCourierPrice} OR :#{#offer.delivery.clickCourierPrice} IS NULL) " +
            "AND (COALESCE(o.delivery.pickupDate, '') = :#{#offer.delivery.pickupDate} OR :#{#offer.delivery.pickupDate} IS NULL) " +
            "AND (COALESCE(o.delivery.pickupPrice, 0) = :#{#offer.delivery.pickupPrice} OR :#{#offer.delivery.pickupPrice} IS NULL) " +
            "AND (COALESCE(o.delivery.storeDate, '') = :#{#offer.delivery.storeDate} OR :#{#offer.delivery.storeDate} IS NULL) " +
            "AND (COALESCE(o.delivery.storePrice, 0) = :#{#offer.delivery.storePrice} OR :#{#offer.delivery.storePrice} IS NULL) " +
            "AND (COALESCE(o.delivery.courierDate, '') = :#{#offer.delivery.courierDate} OR :#{#offer.delivery.courierDate} IS NULL) " +
            "AND (COALESCE(o.delivery.courierPrice, 0) = :#{#offer.delivery.courierPrice} OR :#{#offer.delivery.courierPrice} IS NULL)")
    @Transactional
    Optional<Offer> findExist(Offer offer);

    List<Offer> findByParseId(UUID parseId);
}
