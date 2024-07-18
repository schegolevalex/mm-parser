package com.schegolevalex.mm.mmparser.service;

import com.schegolevalex.mm.mmparser.entity.Offer;
import com.schegolevalex.mm.mmparser.parser.Parser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ParserService {
    private final Parser parser;
    private final ProductService productService;
    private final OfferService offerService;


    //    @Async
    @Scheduled(cron = "0 */1 * * * *", zone = "Europe/Moscow")
    public void parseJob() {
        log.info("Запуск процесса парсинга");
        long start = System.currentTimeMillis();
        productService.findAllNotDeletedAndUserIsActive().forEach(product -> {
            List<Offer> parsedOffers = parser.parseProduct(product).stream()
                    .map(offer -> {
                        Optional<Offer> maybeExist = offerService.findExist(offer);
                        if (maybeExist.isPresent()) {
                            Offer exist = maybeExist.get();
                            exist.setUpdatedAt(Instant.now());
                            log.trace("Спарсено существующее предложение: {}", exist);
                            return exist;
                        } else {
                            log.trace("Спарсено новое предложение: {}", offer);
                            return offer;
                        }
                    })
                    .toList();
            offerService.saveAll(parsedOffers);
        });
        log.info("Парсинг завершен за {} сек", (System.currentTimeMillis() - start) / 1000.0);
    }
}
