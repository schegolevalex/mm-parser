package com.schegolevalex.mm.mmparser.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @ToString.Exclude
    Seller seller;

    Integer price;

    @Column(columnDefinition = "integer default 0")
    Integer bonusPercent;

    @Column(columnDefinition = "integer default 0")
    Integer bonus;

    @CreatedDate
    @Column(updatable = false)
    Instant createdAt;

    @LastModifiedDate
    Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE/*, CascadeType.PERSIST*/})
    @ToString.Exclude
    Link link;

//    @Override
//    public String toString() {
//        return "Предложение:" +
//                "\n- продавец: \"" + seller.getName() + "\"" +
//                "\n- цена: " + price +
//                "\n- процент бонусов: " + bonusPercent +
//                "\n- количество бонусов: " + bonus +
//                "\n- ссылка: " + link + ")";
//    }
}
