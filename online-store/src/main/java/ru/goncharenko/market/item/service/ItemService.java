package ru.goncharenko.market.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import ru.goncharenko.market.core.response.Paging;
import ru.goncharenko.market.core.types.SortEnum;
import ru.goncharenko.market.item.dto.ItemInCartDTO;
import ru.goncharenko.market.item.dto.ListItemsDTO;
import ru.goncharenko.market.item.mapper.CartItemMapper;
import ru.goncharenko.market.item.model.Cart;
import ru.goncharenko.market.item.model.CartItem;
import ru.goncharenko.market.item.model.Item;
import ru.goncharenko.market.item.repository.CartItemRepository;
import ru.goncharenko.market.item.repository.ItemRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemService {
	private final ItemRepository itemRepository;
	private final ItemCacheService cacheService;
	private final CartItemRepository cartItemRepository;
	private final CartService cartService;
	private final CartItemMapper mapper;
	private final DatabaseClient databaseClient;

	public Mono<ListItemsDTO> getItems(String search, SortEnum sort, int page, int size, ServerWebExchange exchange) {
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sort.getFieldName()));

		return Mono.zip(
						getItemsPage(search, pageable),
						cartService.getOrCreateCart()
				)
				.flatMap(tuple -> {
					List<Item> items = tuple.getT1().getT1();
					long total = tuple.getT1().getT2();
					Cart cart = tuple.getT2();

					return getItemCounts(cart.getId(), items)
							.map(counts -> buildResponse(items, counts, page, size, total, exchange));
				});
	}

	private Mono<Tuple2<List<Item>, Long>> getItemsPage(String search, Pageable pageable) {
		if (search == null || search.isEmpty()) {
			return Mono.zip(
					itemRepository.findAllBy(pageable).collectList(),
					itemRepository.count()
			);
		} else {
			return Mono.zip(
					itemRepository.findByDescriptionOrTitleContainingIgnoreCase(search, pageable)
							.collectList(),
					itemRepository.countByDescriptionOrTitleContainingIgnoreCase(search)
			);
		}
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
	                                   int page, int size, long total, ServerWebExchange exchange) {
		List<List<ItemInCartDTO>> groupedItems = groupItems(items, counts, size, exchange);

		return ListItemsDTO.builder()
				.items(groupedItems)
				.paging(new Paging(page, size, page > 1, ((long) page * size) < total))
				.build();
	}

	private List<List<ItemInCartDTO>> groupItems(List<Item> items, Map<Long, Integer> counts, int size, ServerWebExchange exchange) {
		int groupSize = Math.min(size, 3);
		List<List<ItemInCartDTO>> result = new ArrayList<>();

		for (int i = 0; i < items.size(); i += groupSize) {
			int end = Math.min(i + groupSize, items.size());
			List<ItemInCartDTO> group = new ArrayList<>();

			for (int j = i; j < end; j++) {
				Item item = items.get(j);
				ItemInCartDTO dto = mapper.toItemInCart(item);
				dto.count(counts.getOrDefault(item.getId(), 0));
				dto.imgPath(CartItemMapper.getImgUrl(exchange) + dto.imgPath());
				group.add(dto);
			}

			while (group.size() < groupSize) {
				ItemInCartDTO empty = new ItemInCartDTO();
				empty.id(-1);
				empty.count(0);
				group.add(empty);
			}

			result.add(group);
		}

		return result;
	}

	public Mono<ItemInCartDTO> findItem(Long id, ServerWebExchange exchange) {
		return cartService.getOrCreateCart()
				.flatMap(cart -> findItemWithCount(cart.getId(), id))
				.flatMap(count -> cacheService.findCachedItemById(id)
						.map(cachedItem -> {
							ItemInCartDTO dto = mapper.toItemInCart(cachedItem);
							dto.count(count);
							dto.imgPath(CartItemMapper.getImgUrl(exchange) + dto.imgPath());
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
