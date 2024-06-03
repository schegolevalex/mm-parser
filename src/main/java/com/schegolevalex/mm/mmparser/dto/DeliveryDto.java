package com.schegolevalex.mm.mmparser.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = DeliveryDtoDeserializer.class)
public class DeliveryDto {
    String storeDate;
    Integer storePrice;

    String courierDate;
    Integer courierPrice;

    String clickCourierDate;
    Integer clickCourierPrice;

    String pickupDate;
    Integer pickupPrice;
}
