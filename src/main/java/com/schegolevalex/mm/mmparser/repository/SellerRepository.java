package com.schegolevalex.mm.mmparser.repository;

import com.schegolevalex.mm.mmparser.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller> findByName(String sellerName);

    Optional<Seller> findByNameAndRatingAndOgrn(String sellerName, Double rating, String ogrn);

    @Query(value = "INSERT INTO seller (name, rating, created_at, updated_at, is_active, ogrn, email, market_id) " +
                   "VALUES (:#{#seller.name}, :#{#seller.rating}, NOW(), NOW(), :#{#seller.isActive}, :#{#seller.ogrn}, :#{#seller.email}, :#{#seller.marketId}) " +
                   "ON CONFLICT(market_id) DO UPDATE SET " +
                   "name = COALESCE(excluded.name, seller.name), " +
                   "rating = COALESCE(excluded.rating, seller.rating), " +
                   "created_at = COALESCE(excluded.created_at, NOW()), " +
                   "updated_at = NOW(), " +
                   "is_active = COALESCE(excluded.is_active, seller.is_active), " +
                   "ogrn = COALESCE(excluded.ogrn, seller.ogrn), " +
                   "email = COALESCE(excluded.email, seller.email) " +
                   "RETURNING *",
            nativeQuery = true)
    Seller saveOrUpdate(Seller seller);
}
