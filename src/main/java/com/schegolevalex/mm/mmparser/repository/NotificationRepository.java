package com.schegolevalex.mm.mmparser.repository;

import com.schegolevalex.mm.mmparser.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query(
            "SELECT n FROM Notification n " +
            "WHERE n.offer = :offer " +
            "AND n.user = :user " +
            "AND (SIZE(n.filters) = :size " +
            "AND NOT EXISTS (SELECT f FROM Filter f WHERE f IN :filters AND n.id NOT IN " +
            "(SELECT m.id FROM Notification m JOIN m.filters mf WHERE mf = f))) " +
            "AND (n.promo = :promo OR (:promo IS NULL AND n.promo IS NULL)) " +
            "AND n.cashbackLevel = :cashbackLevel")
    Optional<Notification> findExist(Offer offer, User user, Set<Filter> filters, Promo promo, Integer cashbackLevel, int size);

    @Query(
            "SELECT n FROM Notification n " +
            "WHERE n.offer = :#{#notification.offer} " +
            "AND n.user = :#{#notification.user} " +
            "AND (SIZE(n.filters) = :#{#notification.filters.size} " +
            "AND NOT EXISTS (SELECT f FROM Filter f WHERE f IN :#{#notification.filters} AND n.id NOT IN " +
            "(SELECT m.id FROM Notification m JOIN m.filters mf WHERE mf = f))) " +
            "AND (n.promo = :#{#notification.promo} OR (:#{#notification.promo} IS NULL AND n.promo IS NULL)) " +
            "AND n.cashbackLevel = :#{#notification.cashbackLevel}")
    Optional<Notification> findExist(Notification notification);

    void deleteAllByPromoId(long id);

    @Query("SELECT n FROM Notification n WHERE n.offer.parseId = :parseId")
    List<Notification> findByParseId(UUID parseId);
}
