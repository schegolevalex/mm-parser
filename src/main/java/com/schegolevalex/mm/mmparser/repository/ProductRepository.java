package com.schegolevalex.mm.mmparser.repository;

import com.schegolevalex.mm.mmparser.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.user.chatId = :chatId and p.isActive = :isActive")
    List<Product> findAllByChatIdAndActive(Long chatId, Boolean isActive);

    List<Product> findAllByIsActive(Boolean isActive);
}
