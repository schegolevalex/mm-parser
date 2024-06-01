package com.schegolevalex.mm.mmparser.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = SellerDtoDeserializer.class)
public class SellerDto {
    String name;

    Double rating;

    String marketId;
}
