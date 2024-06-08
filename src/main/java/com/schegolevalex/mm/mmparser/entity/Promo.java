package com.schegolevalex.mm.mmparser.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    @OneToMany(mappedBy = "promo"
            , cascade = CascadeType.ALL
            , fetch = FetchType.LAZY
            , orphanRemoval = true)
    @Builder.Default
    @EqualsAndHashCode.Exclude
    List<PromoStep> promoSteps = new ArrayList<>();

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
}
