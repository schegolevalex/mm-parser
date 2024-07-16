package com.schegolevalex.mm.mmparser.service;

import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;

    public Optional<Product> findById(long id) {
        return productRepository.findById(id);
    }

    public List<Product> findAllByChatIdNotDeletedAndUserIsActive(Long chatId) {
        return productRepository.findAllByChatIdNotDeletedAndUserIsActive(chatId);
    }

    public List<Product> findAllNotDeletedAndUserIsActive() {
        return productRepository.findAllNotDeletedAndUserIsActive();
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public List<Product> findByPromoId(long promoId) {
        return productRepository.findByPromoId(promoId);
    }
}
