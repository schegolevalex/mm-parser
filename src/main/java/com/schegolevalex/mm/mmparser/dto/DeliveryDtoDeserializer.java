package com.schegolevalex.mm.mmparser.dto;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class DeliveryDtoDeserializer extends StdDeserializer<DeliveryDto> {
    public DeliveryDtoDeserializer() {
        this(null);
    }

    protected DeliveryDtoDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public DeliveryDto deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JacksonException {
        JsonNode offerNode = parser.getCodec().readTree(parser);

        DeliveryDto deliveryDto = new DeliveryDto();
        deliveryDto.setType(offerNode.path("displayName").asText());
        deliveryDto.setDate(offerNode.path("displayDeliveryDate").asText());
        deliveryDto.setPrice(offerNode.path("price").asInt());

        return deliveryDto;
    }
}
