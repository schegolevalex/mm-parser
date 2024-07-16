package com.schegolevalex.mm.mmparser.service;

import com.schegolevalex.mm.mmparser.entity.Filter;
import com.schegolevalex.mm.mmparser.entity.Notify;
import com.schegolevalex.mm.mmparser.entity.Product;
import com.schegolevalex.mm.mmparser.repository.FilterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilterService {
    private final FilterRepository filterRepository;
    private final ProductService productService;
    private final NotifyService notifyService;

    public Optional<Filter> findById(long promoId) {
        return filterRepository.findById(promoId);
    }

    public List<Filter> findAllByChatId(Long chatId) {
        return filterRepository.findAllByChatId(chatId);
    }

    public Filter save(Filter filter) {
        return filterRepository.save(filter);
    }

    @Transactional
    public void deleteById(long id) {
        Filter filter = filterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Filter not found with id: " + id));
        for (Product product : filter.getProducts()) {
            product.removeFilter(filter);
            productService.save(product);
        }

        for (Notify notify : filter.getNotifies()) {
            notify.removeFilter(filter);
            notifyService.save(notify);
        }
        filterRepository.delete(filter);
    }
}
