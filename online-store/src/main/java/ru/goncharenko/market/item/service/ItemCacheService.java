package ru.goncharenko.market.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import ru.goncharenko.market.core.exception.ResourceNotFoundException;
import ru.goncharenko.market.item.dto.CartContext;
import ru.goncharenko.market.item.dto.ItemContext;
import ru.goncharenko.market.item.model.CartItem;
import ru.goncharenko.market.item.model.Item;
import ru.goncharenko.market.item.repository.ItemRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemCacheService {
	private final ItemRepository repository;
	private final ReactiveRedisTemplate<String, Item> redisTemplate;
	private final String cartCachePrefix = "cart:";

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
			log.info("Load items on page from DB");
			items = Mono.zip(
					repository.findAllBy(pageable).collectList(),
					repository.count()
			);
		} else {
			log.info("Load items on page with filter from DB");
			items = Mono.zip(
					repository.findByDescriptionOrTitleContainingIgnoreCase(search, pageable)
							.collectList(),
					repository.countByDescriptionOrTitleContainingIgnoreCase(search)
			);
		}

		return items.map(tuple -> new ItemContext(tuple.getT1(), tuple.getT2()));
	}

	public Mono<Map<Long, Item>> loadItemsFromCache(CartContext context) {
		String cacheKey = cartCachePrefix + context.getCart().getUserName();

		return redisTemplate.opsForHash()
				.entries(cacheKey)
				.collectMap(
						entry -> Long.valueOf(entry.getKey().toString()),
						entry -> (Item) entry.getValue()
				)
				.doOnSuccess(map -> {
					if (Objects.requireNonNull(map).isEmpty()) {
						log.info("Cache MISS for key: {}", cacheKey);
					} else {
						log.info("Cache HIT for key: {}, loaded {} items", cacheKey, map.size());
					}
				})
				.onErrorResume(e -> {
					log.error("Error loading from cache for key: {}", cacheKey, e);
					return Mono.just(Collections.emptyMap());
				});
	}

	public Mono<Map<Long, Item>> loadItemsFromDbAndCache(CartContext context) {
		log.info("Load items in cart from DB");
		String cacheKey = cartCachePrefix + context.getCart().getUserName();
		List<CartItem> items = context.getItems();
		List<Long> itemIds = extractItemIds(items);
		return repository.findAllByIdIn(itemIds)
				.collectMap(Item::getId)
				.flatMap(itemsMap -> {
					Map<String, Item> stringKeyMap = itemsMap.entrySet().stream()
							.collect(Collectors.toMap(
									entry -> String.valueOf(entry.getKey()),
									Map.Entry::getValue
							));

					return redisTemplate.opsForHash()
							.delete(cacheKey)
							.flatMap(bool ->
									redisTemplate.opsForHash()
											.putAll(cacheKey, stringKeyMap)
											.thenReturn(itemsMap)
							);
				});
	}

	public Mono<Void> addItemInCartToCache(Long itemId, String userName) {
		String cacheKey = cartCachePrefix + userName;
		return redisTemplate.hasKey(cacheKey).flatMap(keyExists -> {
							if (!keyExists) {
								log.info("Cache key {} does not exist, skipping item {}", cacheKey, itemId);
								return Mono.empty();
							}

							log.info("Cache key {} exists, adding item {}", cacheKey, itemId);
							return repository.findById(itemId)
									.switchIfEmpty(Mono.error(new ResourceNotFoundException(String.format("Item with %d not found", itemId))))
									.flatMap(item -> redisTemplate.opsForHash().put(cacheKey, itemId.toString(), item));
						}
				)
				.then();
	}

	public Mono<Void> removeItemInCartFromCache(Long itemId, String userName) {
		String cacheKey = cartCachePrefix + userName;
		return redisTemplate.hasKey(cacheKey).flatMap(keyExists -> {
							if (keyExists) {
								return redisTemplate.opsForHash()
										.remove(cartCachePrefix + userName, itemId.toString());
							}

							return Mono.empty();
						}
				)
				.then();
	}

	private List<Long> extractItemIds(List<CartItem> items) {
		return items.stream()
				.map(CartItem::getItemId)
				.collect(Collectors.toList());
	}
}
