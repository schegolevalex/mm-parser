package com.schegolevalex.mm.mmparser.service;

import com.schegolevalex.mm.mmparser.entity.Seller;
import com.schegolevalex.mm.mmparser.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SellerService {
    private final SellerRepository sellerRepository;

    public Optional<Seller> findByName(String sellerName) {
        return sellerRepository.findByName(sellerName);
    }

    public Optional<Seller> findByNameAndRatingAndOgrn(String sellerName, Double rating, String ogrn) {
        return sellerRepository.findByNameAndRatingAndOgrn(sellerName, rating, ogrn);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Seller saveOrUpdate(Seller seller) {
        Seller savedOrUpdatedSeller = sellerRepository.saveOrUpdate(seller);
        Hibernate.initialize(savedOrUpdatedSeller.getProducts());
        return savedOrUpdatedSeller;
    }

    public Seller save(Seller seller) {
        return sellerRepository.save(seller);
    }
}
