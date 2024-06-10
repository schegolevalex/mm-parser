package com.schegolevalex.mm.mmparser.repository;

import com.schegolevalex.mm.mmparser.entity.Promo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PromoRepository extends JpaRepository<Promo, Long> {

    @Query("SELECT p FROM Promo p WHERE p.user.chatId = :chatId")
    List<Promo> findAllByChatId(Long chatId);
}
