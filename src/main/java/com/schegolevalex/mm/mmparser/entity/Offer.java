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
    Integer bonusPercent;
    Integer bonus;

    @CreatedDate
    @Column(updatable = false)
    Instant createdAt;

    @ManyToOne
    Link link;
}
