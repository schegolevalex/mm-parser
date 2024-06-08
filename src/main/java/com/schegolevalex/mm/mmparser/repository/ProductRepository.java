package com.schegolevalex.mm.mmparser.repository;

import com.schegolevalex.mm.mmparser.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.user.chatId = :chatId")
    List<Product> findAllByChatId(Long chatId);

    List<Product> findAllByIsActive(Boolean isActive);
}
