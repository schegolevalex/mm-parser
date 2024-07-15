package com.schegolevalex.mm.mmparser.entity;

import com.schegolevalex.mm.mmparser.bot.Constant;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
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
public class Filter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    Long id;

    @Enumerated(value = EnumType.STRING)
    FilterField field;

    @Enumerated(value = EnumType.STRING)
    Operation operation;

    Integer value;

    @CreatedDate
    @Column(updatable = false)
    Instant createdAt;

    @LastModifiedDate
    Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    User user;

    @ManyToMany(mappedBy = "filters", fetch = FetchType.LAZY)
    @Builder.Default
    @EqualsAndHashCode.Exclude
    Set<Product> products = new HashSet<>();

    @ManyToMany(mappedBy = "filters", fetch = FetchType.LAZY)
    @Builder.Default
    @EqualsAndHashCode.Exclude
    Set<Notify> notifies = new HashSet<>();

    @Override
    public String toString() {
        String fieldName;
        String operationName;
        switch (field) {
            case FilterField.PRICE -> fieldName = Constant.Button.PRICE;
            case FilterField.PRICE_WITH_PROMO -> fieldName = Constant.Button.PRICE_WITH_PROMO;
            case FilterField.PRICE_TOTAL -> fieldName = Constant.Button.PRICE_TOTAL;
            case FilterField.BONUS -> fieldName = Constant.Button.BONUS;
            case FilterField.BONUS_PERCENT -> fieldName = Constant.Button.BONUS_PERCENT;
            default -> fieldName = "error";
        }

        switch (operation) {
            case EQUALS -> operationName = "=";
            case GREATER_OR_EQUALS -> operationName = "≥";
            case LESS_OR_EQUALS -> operationName = "≤";
            default -> operationName = "error";
        }

        return fieldName + " " + operationName + " " + value;
    }
}
