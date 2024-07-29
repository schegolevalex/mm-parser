package com.schegolevalex.mm.mmparser.service;

import com.github.sonus21.rqueue.annotation.RqueueListener;
import com.github.sonus21.rqueue.core.RqueueMessageEnqueuer;
import com.schegolevalex.mm.mmparser.entity.Offer;
import com.schegolevalex.mm.mmparser.parser.Parser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ParserService {
    private final ObjectFactory<Parser> parserFactory;
    //    private final Parser parser;
    private final ProductService productService;
    private final OfferService offerService;
    private final RqueueMessageEnqueuer rqueueMessageEnqueuer;

    @RqueueListener(value = "product-queue"/*, concurrency = "3"*/)
    public void parseJob(Long productId) {
        productService.findById(productId).ifPresent(product -> {
            List<Offer> parsedOffers = parserFactory.getObject().parseProduct(product).stream()
                    .map(offer -> {
                        Optional<Offer> maybeExist = offerService.findExist(offer);
                        if (maybeExist.isPresent()) {
                            Offer exist = maybeExist.get();
                            exist.setUpdatedAt(Instant.now());
                            log.trace("Найдено существующее предложение: {}", exist);
                            return exist;
                        } else {
                            log.trace("Найдено новое предложение: {}", offer);
                            return offer;
                        }
                    })
                    .toList();

            rqueueMessageEnqueuer.enqueueIn("product-queue", productId, Duration.ofMinutes(1));
            offerService.saveAll(parsedOffers);
            if (!parsedOffers.isEmpty())
                parsedOffers.forEach(offer -> rqueueMessageEnqueuer.enqueue("notification-queue", offer.getId()));
        });
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        productService.findAllNotDeletedAndUserIsActive()
                .forEach(product -> rqueueMessageEnqueuer.enqueue("product-queue", product.getId()));
    }
}
