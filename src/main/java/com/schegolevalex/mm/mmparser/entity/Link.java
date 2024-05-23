package com.schegolevalex.mm.mmparser.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
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
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String url;

    Long chatId;

    String title;

    @CreatedDate
    @Column(updatable = false)
    Instant createdAt;

    @Column(columnDefinition = "boolean default TRUE")
    @Builder.Default
    boolean isActive = true;

    public Link(String url, Long chatId, String title) {
        this.url = url;
        this.chatId = chatId;
        this.title = title;
    }

    @Override
    public String toString() {
        return "url = " + url;
    }
}