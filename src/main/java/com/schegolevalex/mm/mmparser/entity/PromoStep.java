package com.schegolevalex.mm.mmparser.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
public class PromoStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    Long id;

    Integer priceFrom;

    Integer discount;

    @ManyToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    Promo promo;

    public void setPriceFrom(Integer priceFrom) throws IllegalArgumentException {
        if (this.discount != null && this.discount >= priceFrom) {
            throw new IllegalArgumentException("Цена должна быть больше скидки");
        }
        this.priceFrom = priceFrom;
    }

    public void setDiscount(Integer discount) throws IllegalArgumentException {
        if (this.priceFrom != null && this.priceFrom <= discount) {
            throw new IllegalArgumentException("Скидка должна быть меньше цены, от которой она действует");
        }
        this.discount = discount;
    }
}
