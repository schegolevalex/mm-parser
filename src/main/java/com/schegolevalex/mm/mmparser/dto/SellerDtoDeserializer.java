package com.schegolevalex.mm.mmparser.dto;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class SellerDtoDeserializer extends StdDeserializer<SellerDto> {

    public SellerDtoDeserializer() {
        this(null);
    }

    protected SellerDtoDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public SellerDto deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JacksonException {
        JsonNode offerNode = parser.getCodec().readTree(parser);

        SellerDto sellerDto = new SellerDto();
        sellerDto.setName(offerNode.path("merchant").path("name").asText());
        sellerDto.setMarketId(offerNode.path("merchant").path("id").asText());
        sellerDto.setRating(offerNode.path("merchantSummaryRating").asDouble());

        return sellerDto;
    }
}
