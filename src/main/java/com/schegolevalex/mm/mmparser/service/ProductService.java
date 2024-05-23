package com.schegolevalex.mm.mmparser.service;

import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> findAllByChatId(Long chatId) {
        return productRepository.findAllByChatId(chatId);
    }

    public Product saveAndFlush(Product product) {
        return productRepository.saveAndFlush(product);
    }

    public List<Product> findAllByIsActive(boolean b) {
        return productRepository.findAllByIsActive(b);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }
}
