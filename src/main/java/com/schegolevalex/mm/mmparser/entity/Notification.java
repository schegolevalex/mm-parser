package com.schegolevalex.mm.mmparser.entity;

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
@ToString
@EqualsAndHashCode
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    Offer offer;

    @ManyToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    User user;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "notification_filter",
            joinColumns = @JoinColumn(name = "notification_id"),
            inverseJoinColumns = @JoinColumn(name = "filter_id"))
    @Builder.Default
    @EqualsAndHashCode.Exclude
    Set<Filter> filters = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    Promo promo;

    Integer cashbackLevel;

    @CreatedDate
    @Column(updatable = false)
    Instant createdAt;

    @LastModifiedDate
    Instant updatedAt;

    public void addFilter(Filter filter) {
        filters.add(filter);
        filter.getNotifies().add(this);
    }

    public void addFilters(Set<Filter> filterList) {
        filterList.forEach(this::addFilter);
    }

    public void removeFilter(Filter filter) {
        filters.remove(filter);
        filter.getNotifies().remove(this);
    }
}
