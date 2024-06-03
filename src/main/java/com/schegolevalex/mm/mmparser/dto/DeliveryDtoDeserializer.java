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
        JsonNode deliveriesNode = parser.getCodec().readTree(parser);
        DeliveryDto deliveryDto = new DeliveryDto();
        for (JsonNode deliveryNode : deliveriesNode) {
            if ("Доставка по клику".equals(deliveryNode.get("displayName").asText())) {
                deliveryDto.setClickCourierDate(deliveryNode.get("displayDeliveryDate").asText());
                deliveryDto.setClickCourierPrice(deliveryNode.get("price").asInt());
            }
            if ("Пункты выдачи".equals(deliveryNode.get("displayName").asText())) {
                deliveryDto.setPickupDate(deliveryNode.get("displayDeliveryDate").asText());
                deliveryDto.setPickupPrice(deliveryNode.get("price").asInt());
            }
            if ("Забрать в магазине".equals(deliveryNode.get("displayName").asText())) {
                deliveryDto.setStoreDate(deliveryNode.get("displayDeliveryDate").asText());
                deliveryDto.setStorePrice(deliveryNode.get("price").asInt());
            }
            if ("Доставка курьером продавца".equals(deliveryNode.get("displayName").asText())) {
                deliveryDto.setCourierDate(deliveryNode.get("displayDeliveryDate").asText());
                deliveryDto.setCourierPrice(deliveryNode.get("price").asInt());
            }
        }
        return deliveryDto;
    }
}
