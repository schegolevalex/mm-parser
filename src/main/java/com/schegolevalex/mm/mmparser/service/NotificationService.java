package com.schegolevalex.mm.mmparser.service;

import com.github.sonus21.rqueue.annotation.RqueueListener;
import com.schegolevalex.mm.mmparser.bot.ParserBot;
import com.schegolevalex.mm.mmparser.entity.Notification;
import com.schegolevalex.mm.mmparser.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final OfferService offerService;
    private final ParserBot parserBot;

    public boolean isAlreadyNotified(Notification notification) {
        return notificationRepository.findExist(notification.getOffer(),
                        notification.getUser(),
                        notification.getFilters(),
                        notification.getPromo(),
                        notification.getCashbackLevel(),
                        notification.getFilters().size())
                .isPresent();
    }

    public List<Notification> saveAll(List<Notification> notifies) {
        return notificationRepository.saveAll(notifies);
    }

    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    public void deleteAllByPromoId(long id) {
        notificationRepository.deleteAllByPromoId(id);
        log.trace("Удалены уведомления для промо c id = : {}", id);
    }

    @RqueueListener(value = "notification-queue")
    protected void notifyJob(Long offerId) {
        offerService.findById(offerId).ifPresent(offer -> {
            if (offerService.isPassFilters(offer)) {
                Notification notification = Notification.builder()
                        .offer(offer)
                        .user(offer.getProduct().getUser())
                        .promo(offer.getProduct().getPromo())
                        .cashbackLevel(offer.getProduct().getUser().getCashbackLevel())
                        .build();
                notification.addFilters(offer.getProduct().getFilters());

                if (!isAlreadyNotified(notification)) {
                    parserBot.sendNotifies(notification);
                    notificationRepository.save(notification);
                }
            }
        });
    }
}
