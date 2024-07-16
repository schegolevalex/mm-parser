package com.schegolevalex.mm.mmparser.service;

import com.schegolevalex.mm.mmparser.entity.Notify;
import com.schegolevalex.mm.mmparser.repository.NotifyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NotifyService {

    private final NotifyRepository notifyRepository;

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
}
