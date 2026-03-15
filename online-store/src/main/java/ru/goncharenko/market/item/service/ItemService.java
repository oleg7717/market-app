package ru.goncharenko.market.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.goncharenko.market.core.response.Paging;
import ru.goncharenko.market.core.types.SortEnum;
import ru.goncharenko.market.item.dto.ItemInCartDTO;
import ru.goncharenko.market.item.dto.ListItemsDTO;
import ru.goncharenko.market.item.mapper.CartItemMapper;
import ru.goncharenko.market.item.model.Cart;
import ru.goncharenko.market.item.model.CartItem;
import ru.goncharenko.market.item.model.Item;
import ru.goncharenko.market.item.repository.CartItemRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
	private final ItemCacheService cacheService;
	private final CartItemRepository cartItemRepository;
	private final CartService cartService;
	private final CartItemMapper mapper;
	private final DatabaseClient databaseClient;

	public Mono<ListItemsDTO> getItems(String search, SortEnum sort, int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sort.getFieldName()));

		return Mono.zip(
						cacheService.getItemsPage(search, pageable),
						cartService.getOrCreateCart()
				)
				.flatMap(tuple -> {
					List<Item> items = tuple.getT1().getItems();
					long total = tuple.getT1().getCount();
					Cart cart = tuple.getT2();

					return getItemCounts(cart.getId(), items)
							.map(counts -> buildResponse(items, counts, page, size, total));
				});
	}

	private Mono<Map<Long, Integer>> getItemCounts(Long cartId, List<Item> items) {
		List<Long> itemIds = items.stream()
				.map(Item::getId)
				.filter(id -> id > 0)
				.toList();

		if (itemIds.isEmpty()) {
			return Mono.just(new HashMap<>());
		}

		return cartItemRepository.findByCartIdAndItemIdIn(cartId, itemIds)
				.collectMap(
						CartItem::getItemId,
						CartItem::getCount
				)
				.defaultIfEmpty(new HashMap<>());
	}

	private ListItemsDTO buildResponse(List<Item> items, Map<Long, Integer> counts,
	                                   int page, int size, long total) {
		List<ItemInCartDTO> itemsWithCounts = items.stream()
				.map(item -> {
					ItemInCartDTO dto = mapper.toItemInCart(item);
					dto.count(counts.getOrDefault(item.getId(), 0));
					return dto;
				})
				.collect(Collectors.toList());

		return ListItemsDTO.builder()
				.items(itemsWithCounts)
				.paging(new Paging(page, size, page > 1, ((long) page * size) < total))
				.build();
	}

	public Mono<ItemInCartDTO> findItem(Long id) {
		return cartService.getOrCreateCart()
				.flatMap(cart -> findItemWithCount(cart.getId(), id))
				.flatMap(count -> cacheService.findCachedItemById(id)
						.map(cachedItem -> {
							ItemInCartDTO dto = mapper.toItemInCart(cachedItem);
							dto.count(count);
							return dto;
						}));
	}

	private Mono<Integer> findItemWithCount(Long cartId, Long itemId) {
		return databaseClient.sql(
						"SELECT COALESCE(ci.count, 0) as item_count " +
								"FROM items i " +
								"LEFT JOIN cart_item ci ON i.id = ci.item_id AND ci.cart_id = :cartId " +
								"WHERE i.id = :itemId")
				.bind("cartId", cartId)
				.bind("itemId", itemId)
				.map((row, metadata) -> Objects.requireNonNull(row.get("item_count", Integer.class)))
				.one();
	}
}
