package com.schegolevalex.mm.mmparser.service;

import com.schegolevalex.mm.mmparser.entity.Notify;
import com.schegolevalex.mm.mmparser.repository.NotifyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
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
}
