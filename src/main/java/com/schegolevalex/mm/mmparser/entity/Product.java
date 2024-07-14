package com.schegolevalex.mm.mmparser.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
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
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    Long id;

    String url;

    String title;

    String sku;

    @ManyToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    Promo promo;

    @CreatedDate
    @Column(updatable = false)
    Instant createdAt;

    @Column(columnDefinition = "boolean default TRUE")
    @Builder.Default
    boolean isActive = true;

    @ManyToMany(mappedBy = "products", fetch = FetchType.LAZY)
    @Builder.Default
    @EqualsAndHashCode.Exclude
    Set<Seller> sellers = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    User user;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "filter_product",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "filter_id"))
    @Builder.Default
    @ToString.Exclude
    Set<Filter> filters = new HashSet<>();

    @Override
    public String toString() {
        return "url = " + url;
    }

    public void addFilter(Filter filter) {
        filters.add(filter);
        filter.getProducts().add(this);
    }

    public void addFilters(List<Filter> filterList) {
        filterList.forEach(this::addFilter);
    }

    public void removeFilter(Filter filter) {
        filters.remove(filter);
        filter.getProducts().remove(this);
    }
}