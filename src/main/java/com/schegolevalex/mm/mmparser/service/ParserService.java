package com.schegolevalex.mm.mmparser.service;

import com.github.sonus21.rqueue.annotation.RqueueListener;
import com.github.sonus21.rqueue.core.RqueueMessageEnqueuer;
import com.github.sonus21.rqueue.core.RqueueMessageManager;
import com.schegolevalex.mm.mmparser.entity.Offer;
import com.schegolevalex.mm.mmparser.parser.Parser;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.annotation.Value;
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
    private final ProductService productService;
    private final OfferService offerService;
    private final GenericObjectPool<Parser> parserPool;
    private final RqueueMessageEnqueuer rqueueMessageEnqueuer;
    private final RqueueMessageManager rqueueMessageManager;

    @Value("${mm.rqueue.product-queue}")
    private String productQueue;
    @Value("${mm.rqueue.notification-queue}")
    private String notificationQueue;
    @Value("${mm.parser.free-parser-delay-in-minutes}")
    private int delay;

    @RqueueListener(value = "product-queue")
    public void parseJob(Long productId) {
        productService.findById(productId).ifPresent(product -> {
            try {
                Parser parser = parserPool.borrowObject();
                List<Offer> parsedOffers = parser.parseProduct(product)
                        .stream()
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

                rqueueMessageEnqueuer.enqueueIn(productQueue, productId, Duration.ofMinutes(delay));
                offerService.saveAll(parsedOffers);

                if (!parsedOffers.isEmpty())
                    parsedOffers.forEach(offer -> rqueueMessageEnqueuer.enqueue(notificationQueue, offer.getId()));

                parserPool.returnObject(parser);
            } catch (Exception e) {
                log.error("При обработке продукта произошла ошибка: {}", e.getMessage());
                parserPool.clear();
                throw new RuntimeException(e);
            }
        });
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        productService.findAllNotDeletedAndUserIsActive()
                .forEach(product -> rqueueMessageEnqueuer.enqueue(productQueue, product.getId()));
    }

    @PreDestroy
    public void deleteAllMessagesFromProductQueue() {
        rqueueMessageManager.deleteAllMessages(productQueue);
        log.info("Удалены все сообщения из очереди: {}", productQueue);
    }
}
