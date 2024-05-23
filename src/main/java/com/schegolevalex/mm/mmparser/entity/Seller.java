package com.schegolevalex.mm.mmparser.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    Double rating;

    @CreatedDate
    @Column(updatable = false)
    Instant createdAt;

    @OneToMany(mappedBy = "seller"
            , cascade = CascadeType.ALL
            , fetch = FetchType.LAZY
            , orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Offer> offers = new ArrayList<>();

    @Column(columnDefinition = "boolean default true")
    boolean isActive = true;

    public void addOffer(Offer offer) {
        offers.add(offer);
        offer.setSeller(this);
    }

    public void removeOffer(Offer offer) {
        offers.remove(offer);
        offer.setSeller(null);
    }
}