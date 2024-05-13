package com.schegolevalex.mm.mmparser.repository;

import com.schegolevalex.mm.mmparser.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
}
