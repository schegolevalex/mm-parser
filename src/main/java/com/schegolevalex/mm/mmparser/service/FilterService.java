package com.schegolevalex.mm.mmparser.service;

import com.schegolevalex.mm.mmparser.entity.Filter;
import com.schegolevalex.mm.mmparser.repository.FilterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilterService {
    private final FilterRepository filterRepository;

    public List<Filter> findAllByChatId(Long chatId) {
        return filterRepository.findAllByChatId(chatId);
    }
}
