package com.schegolevalex.mm.mmparser.repository;

import com.schegolevalex.mm.mmparser.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.user.chatId = :chatId and p.active = :active and p.deleted = :deleted")
    List<Product> findAllByChatIdAndActiveAndDeleted(Long chatId, Boolean active, Boolean deleted);

    List<Product> findAllByActiveAndDeleted(Boolean active, Boolean deleted);

    List<Product> findByPromoId(long promoId);
}
