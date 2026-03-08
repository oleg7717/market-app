package ru.goncharenko.market.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import ru.goncharenko.market.core.exception.ResourceNotFoundException;
import ru.goncharenko.market.item.dto.ItemContext;
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
		log.info("Load item from db");
		return repository.findById(id)
				.switchIfEmpty(Mono.error(new ResourceNotFoundException(String.format("Item with id: %d not found.", id))));
	}

	@Cacheable(value = "items", key = "{#search, #pageable.pageNumber, #pageable.pageSize, #pageable.sort}")
	public Mono<ItemContext> getItemsPage(String search, Pageable pageable) {
		Mono<Tuple2<List<Item>, Long>> items;
		if (search == null || search.isEmpty()) {
			log.info("Load items on page from db");
			items = Mono.zip(
					repository.findAllBy(pageable).collectList(),
					repository.count()
			);
		} else {
			log.info("Load items on page with filter from db");
			items = Mono.zip(
					repository.findByDescriptionOrTitleContainingIgnoreCase(search, pageable)
							.collectList(),
					repository.countByDescriptionOrTitleContainingIgnoreCase(search)
			);
		}

		return items.map(tuple -> new ItemContext(tuple.getT1(), tuple.getT2()));
	}
}
