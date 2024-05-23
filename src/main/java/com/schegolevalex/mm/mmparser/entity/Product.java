package com.schegolevalex.mm.mmparser.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String url;

    Long chatId;

    String title;

    @CreatedDate
    @Column(updatable = false)
    Instant createdAt;

    @Column(columnDefinition = "boolean default TRUE")
    @Builder.Default
    boolean isActive = true;

    @ManyToMany(mappedBy = "products", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    Set<Seller> sellers = new HashSet<>();

    @OneToMany(mappedBy = "product"
            , cascade = CascadeType.ALL
            , fetch = FetchType.LAZY
            , orphanRemoval = true)
    @ToString.Exclude
    List<Offer> offers = new ArrayList<>();

    public void addOffer(Offer offer) {
        offers.add(offer);
        offer.setProduct(this);
    }

    public void addOffers(List<Offer> offersList) {
        offersList.forEach(this::addOffer);
    }

    public void removeOffer(Offer offer) {
        offers.remove(offer);
        offer.setProduct(null);
    }

    @Override
    public String toString() {
        return "url = " + url;
    }
}