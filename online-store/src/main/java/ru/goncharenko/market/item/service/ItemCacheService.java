package ru.goncharenko.market.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.goncharenko.market.core.exception.ResourceNotFoundException;
import ru.goncharenko.market.item.model.Item;
import ru.goncharenko.market.item.repository.ItemRepository;

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
}
