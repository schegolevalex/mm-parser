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
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Promo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    User user;

    @CreatedDate
    @Column(updatable = false)
    Instant createdAt;

    @OneToMany(mappedBy = "promo"
            , cascade = CascadeType.ALL
            , fetch = FetchType.LAZY
            , orphanRemoval = true)
    @Builder.Default
    @EqualsAndHashCode.Exclude
    List<PromoStep> promoSteps = new ArrayList<>();

    @OneToMany(mappedBy = "promo"
            , cascade = CascadeType.ALL //todo получается, что при удалении промо сразу удаляется продукты, которые ссылаются на него
            , fetch = FetchType.LAZY
            , orphanRemoval = true)
    @Builder.Default
    @EqualsAndHashCode.Exclude
    List<Product> products = new ArrayList<>();

    public void addPromoStep(PromoStep promoStep) {
        promoSteps.add(promoStep);
        promoStep.setPromo(this);
    }

    public void addPromoSteps(List<PromoStep> promoStepList) {
        promoStepList.forEach(this::addPromoStep);
    }

    public void removePromoStep(PromoStep promoStep) {
        promoSteps.remove(promoStep);
        promoStep.setPromo(null);
    }

    public void addProduct(Product product) {
        products.add(product);
        product.setPromo(this);
    }

    public void addProducts(List<Product> productList) {
        productList.forEach(this::addProduct);
    }

    public void removeProduct(Product product) {
        products.remove(product);
        product.setPromo(null);
    }
}
