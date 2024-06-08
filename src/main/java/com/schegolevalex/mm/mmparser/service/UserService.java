package com.schegolevalex.mm.mmparser.service;

import com.schegolevalex.mm.mmparser.entity.User;
import com.schegolevalex.mm.mmparser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public User save(User user) {
        User savedUser = userRepository.saveAndFlush(user);
        log.info("Новый пользователь сохранен в БД: {}", savedUser);
        return savedUser;
    }

    public List<User> saveAll(List<User> users) {
        return userRepository.saveAll(users);
    }

    public Optional<User> findByChatId(Long chatId) {
        return userRepository.findByChatId(chatId);
    }
}