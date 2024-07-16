package com.schegolevalex.mm.mmparser.repository;

import com.schegolevalex.mm.mmparser.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface NotifyRepository extends JpaRepository<Notify, Long> {
    @Query(
            "SELECT n FROM Notify n " +
            "WHERE n.offer = :offer " +
            "AND n.user = :user " +
            "AND (SIZE(n.filters) = :size " +
            "AND NOT EXISTS (SELECT f FROM Filter f WHERE f IN :filters AND n.id NOT IN " +
            "(SELECT m.id FROM Notify m JOIN m.filters mf WHERE mf = f))) " +
            "AND (n.promo = :promo OR (:promo IS NULL AND n.promo IS NULL)) " +
            "AND n.cashbackLevel = :cashbackLevel")
    Optional<Notify> findExist(Offer offer, User user, Set<Filter> filters, Promo promo, Integer cashbackLevel, int size);

    void deleteAllByPromoId(long id);
}
