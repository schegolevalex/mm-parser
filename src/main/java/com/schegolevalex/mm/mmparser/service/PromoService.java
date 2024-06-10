package com.schegolevalex.mm.mmparser.service;

import com.schegolevalex.mm.mmparser.entity.Promo;
import com.schegolevalex.mm.mmparser.repository.PromoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PromoService {
    private final PromoRepository promoRepository;

    public List<Promo> findAllByChatId(Long chatId) {
        return promoRepository.findAllByChatId(chatId);
    }

    public void delete(long id) {
        promoRepository.deleteById(id);
    }
}
