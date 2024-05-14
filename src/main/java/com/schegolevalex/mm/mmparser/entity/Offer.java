package com.schegolevalex.mm.mmparser.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String seller;

    Integer price;

    @Column(columnDefinition = "integer default 0")
    Integer bonusPercent;

    @Column(columnDefinition = "integer default 0")
    Integer bonus;

    @CreatedDate
    @Column(updatable = false)
    Instant createdAt;

    @ManyToOne
    Link link;

    @Override
    public String toString() {
        return "Предложение:" +
                "\n- продавец: \"" + seller + "\"" +
                "\n- цена: " + price +
                "\n- процент бонусов: " + bonusPercent +
                "\n- количество бонусов: " + bonus +
                "\n- ссылка: " + link + ")";
    }
}
