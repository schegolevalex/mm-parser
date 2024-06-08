package com.schegolevalex.mm.mmparser.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TelegramUser")
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class User {

    @Id
    Long chatId;

    String nickname;

    String firstName;

    String lastName;

    boolean isPremium;

    @CreatedDate
    @Column(updatable = false)
    Instant createdAt;

    @LastModifiedDate
    Instant updatedAt;

    @OneToMany(mappedBy = "user"
            , cascade = CascadeType.ALL
            , fetch = FetchType.LAZY
            , orphanRemoval = true)
    @Builder.Default
    @EqualsAndHashCode.Exclude
    List<Promo> promos = new ArrayList<>();

    @OneToMany(mappedBy = "user"
            , cascade = CascadeType.ALL
            , fetch = FetchType.LAZY
            , orphanRemoval = true)
    @Builder.Default
    @EqualsAndHashCode.Exclude
    List<Product> products = new ArrayList<>();

    @Builder.Default
    Long cashbackLevel = 0L;

    public void addPromo(Promo promo) {
        promos.add(promo);
        promo.setUser(this);
    }

    public void addPromos(List<Promo> promosList) {
        promosList.forEach(this::addPromo);
    }

    public void removePromo(Promo promo) {
        promos.remove(promo);
        promo.setUser(null);
    }

    public void addProduct(Product product) {
        products.add(product);
        product.setUser(this);
    }

    public void addProducts(List<Product> productList) {
        productList.forEach(this::addProduct);
    }

    public void removeProduct(Product product) {
        products.remove(product);
        product.setUser(null);
    }
}
