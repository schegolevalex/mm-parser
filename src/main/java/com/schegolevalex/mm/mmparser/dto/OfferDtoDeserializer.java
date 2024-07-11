package com.schegolevalex.mm.mmparser.dto;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class OfferDtoDeserializer extends StdDeserializer<OfferDto> {

    public OfferDtoDeserializer() {
        this(null);
    }

    protected OfferDtoDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public OfferDto deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JacksonException {
        JsonNode offerNode = parser.getCodec().readTree(parser);
        Integer price = offerNode.path("price").asInt();

        JsonNode bonusInfoGroupsNode = offerNode.path("bonusInfoGroups");
        int bonusPercent = 0;
        int bonus = 0;

        if (bonusInfoGroupsNode.isArray()) {
            for (JsonNode groupNode : bonusInfoGroupsNode) {
                if ("PAYMENT_TYPE_BONUS".equals(groupNode.get("type").asText())) {
                    bonusPercent = (groupNode.get("percent").isNull()) ? 0 : groupNode.get("percent").asInt();
                    bonus = (groupNode.get("totalAmount").isNull()) ? 0 : groupNode.get("totalAmount").asInt();
                    break;
                } else if ("ITEMS_BONUS".equals(groupNode.get("type").asText())) {
                    bonusPercent = (groupNode.get("percent").isNull()) ? 0 : groupNode.get("percent").asInt();
                    bonus = (groupNode.get("totalAmount").isNull()) ? 0 : groupNode.get("totalAmount").asInt();
                }
            }
        }
        return new OfferDto(price, bonusPercent, bonus);
    }
}
