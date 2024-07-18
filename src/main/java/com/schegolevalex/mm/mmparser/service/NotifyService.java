package com.schegolevalex.mm.mmparser.service;

import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.entity.Notify;
import com.schegolevalex.mm.mmparser.entity.Offer;
import com.schegolevalex.mm.mmparser.repository.NotifyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Predicate;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NotifyService {

    private final NotifyRepository notifyRepository;
    private final ProductService productService;
    private final OfferService offerService;
    private final ParserBot parserBot;

    public boolean isPresent(Notify notify) {
        return notifyRepository.findExist(notify.getOffer(),
                        notify.getUser(),
                        notify.getFilters(),
                        notify.getPromo(),
                        notify.getCashbackLevel(),
                        notify.getFilters().size())
                .isPresent();
    }

    public List<Notify> saveAll(List<Notify> notifies) {
        return notifyRepository.saveAll(notifies);
    }

    public Notify save(Notify notify) {
        return notifyRepository.save(notify);
    }

    public void deleteAllByPromoId(long id) {
        notifyRepository.deleteAllByPromoId(id);
        log.trace("Удалены уведомления для промо c id = : {}", id);
    }

    @Async
    @Scheduled(cron = "*/30 * * * * *", zone = "Europe/Moscow")
    protected void notifyJob() {
        log.info("Запуск задачи уведомления пользователей");
        productService.findAllNotDeletedAndUserIsActive()
                .forEach(product -> {
                    List<Offer> parsedOffers = offerService.findAllForSpecifiedTime(product, 1, ChronoUnit.MINUTES);

                    List<Notify> notifies = offerService.filterOffers(parsedOffers)
                            .stream()
                            .map(offer -> {
                                Notify notify = Notify.builder()
                                        .offer(offer)
                                        .user(product.getUser())
                                        .promo(product.getPromo())
                                        .cashbackLevel(product.getUser().getCashbackLevel())
                                        .build();
                                notify.addFilters(product.getFilters());
                                return notify;
                            })
                            .filter(Predicate.not(this::isPresent))
                            .toList();

                    notifies.forEach(parserBot::sendNotifies);
                    this.saveAll(notifies);
                });
        log.info("Завершение задачи уведомления пользователей");
    }
}
