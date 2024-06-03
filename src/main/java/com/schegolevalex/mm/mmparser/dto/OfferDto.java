package com.schegolevalex.mm.mmparser.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = OfferDtoDeserializer.class)
public class OfferDto {

    Integer price;

    Integer bonusPercent;

    Integer bonus;
}
