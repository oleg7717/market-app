package ru.goncharenko.market.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.goncharenko.market.core.exception.ResourceNotFoundException;
import ru.goncharenko.market.item.model.Item;
import ru.goncharenko.market.item.repository.ItemRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemCacheService {
	private final ItemRepository repository;

	@Cacheable(value = "item", key = "#id")
	public Mono<Item> findCachedItemById(Long id) {
		return repository.findById(id)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException(String.format("Item with id: %d not found.", id))));
	}

	@Cacheable(value = "items", key = "{#search, #pageable.pageNumber, #pageable.pageSize, #pageable.sort}")
	public Mono<List<Item>> getItemsPage(String search, Pageable pageable) {
		if (search == null || search.isEmpty()) {
			return repository.findAllBy(pageable).collectList();
		} else {
			return repository.findByDescriptionOrTitleContainingIgnoreCase(search, pageable).collectList();
		}
	}
}
