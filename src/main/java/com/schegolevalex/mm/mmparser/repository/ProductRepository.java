package com.schegolevalex.mm.mmparser.repository;

import com.schegolevalex.mm.mmparser.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.user.chatId = :chatId and p.user.active = true and p.deleted = false")
    List<Product> findAllByChatIdNotDeletedAndUserIsActive(Long chatId);

    @Query("select p from Product p where p.user.active = true and p.deleted = false")
    List<Product> findAllNotDeletedAndUserIsActive();

    List<Product> findByPromoId(long promoId);
}
