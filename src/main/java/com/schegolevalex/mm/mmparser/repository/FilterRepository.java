package com.schegolevalex.mm.mmparser.repository;

import com.schegolevalex.mm.mmparser.entity.Filter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface FilterRepository extends JpaRepository<Filter, Long> {

    @Query("SELECT f FROM Filter f WHERE f.user.chatId = :chatId")
    List<Filter> findAllByChatId(Long chatId);
}
