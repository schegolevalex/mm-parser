package com.schegolevalex.mm.mmparser.repository;

import com.schegolevalex.mm.mmparser.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByChatId(Long chatId);
}
