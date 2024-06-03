package com.schegolevalex.mm.mmparser.entity;

import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Embeddable
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Delivery {
    String storeDate;
    Integer storePrice;

    String courierDate;
    Integer courierPrice;

    String clickCourierDate;
    Integer clickCourierPrice;

    String pickupDate;
    Integer pickupPrice;
}
