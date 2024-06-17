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
import com.schegolevalex.mm.mmparser.service.SellerService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.modelmapper.ModelMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class JsonParser extends Parser {
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;
    private final SellerService sellerService;

    protected JsonParser(ProxyService proxyService, ObjectMapper objectMapper, ModelMapper modelMapper, SellerService sellerService) {
        super(proxyService);
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
        this.sellerService = sellerService;
    }

    @Override
    public List<Offer> parseProduct(Product product) {
        openProductUrl(product.getUrl());
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOfElementLocated(By.tagName("html")));

        String page = driver.getPageSource();
//        saveFile(page);
        String jsonString = getJsonString(Jsoup.parse(page));

        List<Offer> offers = new ArrayList<>();
        try {
            JsonNode rootNode = objectMapper.readTree(jsonString);
            if (product.getTitle() == null || product.getTitle().isEmpty()) {
                JsonPointer productTitlePointer = JsonPointer.compile("/hydratorState/PrefetchStore/componentsInitialState/catalog.details/mainInfo/name");
                String productTitle = rootNode.at(productTitlePointer).asText();
                product.setTitle(productTitle);
                log.info("Товару установлено название: {}", productTitle);
            }
            if (product.getSku() == null || product.getSku().isEmpty()) {
                JsonPointer skuPointer = JsonPointer.compile("/hydratorState/PrefetchStore/componentsInitialState/catalog.details/mainInfo/sku");
                String sku = rootNode.at(skuPointer).asText();
                product.setSku(sku);
                log.info("Товару установлен SKU: {}", sku);
            }

            JsonPointer offersPointer = JsonPointer.compile("/hydratorState/PrefetchStore/componentsInitialState/catalog.details/offersData/offers");
            JsonNode offersNode = rootNode.at(offersPointer);
            if (offersNode.isArray()) {
                for (JsonNode offerNode : offersNode) {
                    Offer offer = modelMapper.map(objectMapper.convertValue(offerNode, OfferDto.class), Offer.class);
                    Seller seller = modelMapper.map(objectMapper.convertValue(offerNode, SellerDto.class), Seller.class);

                    Optional<Seller> maybeExistSeller = sellerService.findByMarketId(seller.getMarketId());
                    if (maybeExistSeller.isPresent()) {
                        seller = maybeExistSeller.get();
                        offer.setSeller(seller);
                        log.info("Найден существующий продавец: {}", seller);
                    } else {
                        seller = sellerService.save(seller);
                        log.info("Создан новый продавец: {}", seller);
                    }
                    seller.addProduct(product);
                    offer.setProduct(product);

                    Delivery delivery = modelMapper.map(objectMapper.convertValue(offerNode.path("delivery"), DeliveryDto.class), Delivery.class);

                    offer.setSeller(seller);
                    offer.setDelivery(delivery);
                    offers.add(offer);
                }
            }
        } catch (JsonProcessingException e) {
            log.error("Не удалось распарсить JSON", e);
            throw new RuntimeException(e);
        }
        return offers;
    }

    private static @NotNull String getJsonString(Document html) {
        Elements scripts = html.select("script");
        String jsonString = "";
        for (Element script : scripts) {
            if (script.data().startsWith("window.__APP__=")) {
                jsonString = script.data()
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
        return jsonString;
    }

    private static void saveFile(String page) {
        try {
            File file = new File("output");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(page);
            writer.close();
            log.info("HTML сохранен в файл output");
        } catch (IOException e) {
            log.info("Ошибка при сохранении HTML файла: {}", e.getMessage());
        }
    }
}
