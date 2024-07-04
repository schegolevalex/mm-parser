package com.schegolevalex.mm.mmparser.service;

import com.schegolevalex.mm.mmparser.entity.Promo;
import com.schegolevalex.mm.mmparser.repository.PromoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PromoService {
    private final PromoRepository promoRepository;
    private final ProductService productService;

    public List<Promo> findAllByChatId(Long chatId) {
        return promoRepository.findAllByChatId(chatId);
    }

    public void deleteById(long id) {
        productService.findByPromoId(id).forEach(product -> product.setPromo(null));
        promoRepository.deleteById(id);
    }

    public Optional<Promo> findById(long promoId) {
        return promoRepository.findById(promoId);
    }

    public void save(Promo promo) {
        promoRepository.save(promo);
    }
}
