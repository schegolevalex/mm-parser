package com.schegolevalex.mm.mmparser.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    Long id;

    String url;

    String title;

    String sku;

    @ManyToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    Promo promo;

    @CreatedDate
    @Column(updatable = false)
    Instant createdAt;

    @Column(columnDefinition = "boolean default TRUE")
    @Builder.Default
    boolean isActive = true;

    @ManyToMany(mappedBy = "products", fetch = FetchType.LAZY)
    @Builder.Default
    @EqualsAndHashCode.Exclude
    Set<Seller> sellers = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    User user;

    @Override
    public String toString() {
        return "url = " + url;
    }
}