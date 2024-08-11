package com.schegolevalex.mm.mmparser.parser;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schegolevalex.mm.mmparser.dto.DeliveryDto;
import com.schegolevalex.mm.mmparser.dto.OfferDto;
import com.schegolevalex.mm.mmparser.dto.SellerDto;
import com.schegolevalex.mm.mmparser.entity.Delivery;
import com.schegolevalex.mm.mmparser.entity.Offer;
import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.entity.Seller;
import com.schegolevalex.mm.mmparser.service.ProxyService;
import com.schegolevalex.mm.mmparser.service.SellerService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Scope("prototype")
@Transactional
@Slf4j
public class JsonParser extends Parser {
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;
    private final SellerService sellerService;

    protected JsonParser(ProxyService proxyService,
                         ObjectMapper objectMapper,
                         ModelMapper modelMapper,
                         SellerService sellerService) {
        super(proxyService);
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
        this.sellerService = sellerService;
    }

    @Override
    public List<Offer> parseProduct(Product product) {
        openUrl(product.getUrl() + "#?details_block=prices");

        String page = driver.getPageSource();
        Document html = Jsoup.parse(page);
        JsonNode jsonRoot = getJsonData(html);

        if (product.getTitle() == null || product.getTitle().isEmpty()) {
            JsonPointer productTitlePointer = JsonPointer.compile("/hydratorState/PrefetchStore/componentsInitialState/catalog.details/mainInfo/name");
            String productTitle = jsonRoot.at(productTitlePointer).asText();
            product.setTitle(productTitle);
            log.trace("Товару установлено название: {}", productTitle);
        }
        if (product.getSku() == null || product.getSku().isEmpty()) {
            JsonPointer skuPointer = JsonPointer.compile("/hydratorState/PrefetchStore/componentsInitialState/catalog.details/mainInfo/sku");
            String sku = jsonRoot.at(skuPointer).asText();
            product.setSku(sku);
            log.trace("Товару установлен SKU: {}", sku);
        }

        List<Offer> offers = new ArrayList<>();
        JsonPointer offersPointer = JsonPointer.compile("/hydratorState/PrefetchStore/componentsInitialState/catalog.details/offersData/offers");
        JsonNode offersNode = jsonRoot.at(offersPointer);

        UUID parseId = UUID.randomUUID();

        if (offersNode.isArray()) {
            for (JsonNode offerNode : offersNode) {
                Offer offer = modelMapper.map(objectMapper.convertValue(offerNode, OfferDto.class), Offer.class);
                Seller parsedSeller = modelMapper.map(objectMapper.convertValue(offerNode, SellerDto.class), Seller.class);
                Seller seller = sellerService.saveOrUpdate(parsedSeller);
                Delivery delivery = modelMapper.map(objectMapper.convertValue(offerNode.path("delivery"), DeliveryDto.class), Delivery.class);

                offer.setParseId(parseId);
                offer.setSeller(seller);
                seller.addProduct(product);
                offer.setProduct(product);
                offer.setDelivery(delivery);
                offers.add(offer);
            }
        }
//        saveJsonData(jsonRoot, product.getTitle()); // для отладки
        return offers;
    }

    private JsonNode getJsonData(Document html) {
        Elements scripts = html.select("script");
        String json = "";
        for (Element script : scripts) {
            if (script.data().startsWith("window.__APP__=")) {
                json = script.data()
                        .substring(script.data().indexOf("{"), script.data().lastIndexOf("}") + 1)
                        .replace("undefined", "null")
                        .replace("(name) => {\n" +
                                 "      if (name === \"constructor\") {\n" +
                                 "        return \"\";\n" +
                                 "      }\n" +
                                 "      return experiments.value[name] || null;\n" +
                                 "    }", "null");
                break;
            }
        }
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            log.error("Не удалось распарсить JSON", e);
            throw new RuntimeException(e);
        }
    }

    private static void saveJsonData(JsonNode jsonRoot, String title) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("product_data_" + title + ".json"))) {
            writer.write(jsonRoot.toPrettyString());
            log.debug("Json сохранен в файл {}", "product_data_" + title + ".json");
        } catch (IOException e) {
            log.debug("Ошибка при сохранении Json файла: {}", e.getMessage());
        }
    }
}
