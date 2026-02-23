package ru.goncharenko.market.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import ru.goncharenko.market.core.exception.ResourceNotFoundException;
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
	private final CartItemRepository cartItemRepository;
	private final CartService cartService;
	private final CartItemMapper mapper;
	private final DatabaseClient databaseClient;

	public Mono<ListItemsDTO> getItems(String search, SortEnum sort, int page, int size) {
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
							.map(counts -> buildResponse(items, counts, page, size, total));
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
					itemRepository.findByDescriptionContainingIgnoreCaseOrTitleContainingIgnoreCase(search, search, pageable)
							.collectList(),
					itemRepository.countByDescriptionContainingIgnoreCaseOrTitleContainingIgnoreCase(search, search)
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
	                                   int page, int size, long total) {
		List<List<ItemInCartDTO>> groupedItems = groupItems(items, counts, size);

		return ListItemsDTO.builder()
				.items(groupedItems)
				.paging(new Paging(page, size, page > 1, ((long) page * size) < total))
				.build();
	}

	private List<List<ItemInCartDTO>> groupItems(List<Item> items, Map<Long, Integer> counts, int size) {
		int groupSize = Math.min(size, 3);
		List<List<ItemInCartDTO>> result = new ArrayList<>();

		for (int i = 0; i < items.size(); i += groupSize) {
			int end = Math.min(i + groupSize, items.size());
			List<ItemInCartDTO> group = new ArrayList<>();

			for (int j = i; j < end; j++) {
				Item item = items.get(j);
				ItemInCartDTO dto = mapper.toItemInCart(item);
				dto.count(counts.getOrDefault(item.getId(), 0));
				setImgUrl(dto);
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

	public Mono<ItemInCartDTO> findById(Long id) {
		return cartService.getOrCreateCart()
				.flatMap(cart -> findItemWithCount(cart.getId(), id))
				.map(tuple -> {
					Item item = tuple.getT1();
					Integer count = tuple.getT2();

					ItemInCartDTO dto = mapper.toItemInCart(item);
					dto.count(count);
					setImgUrl(dto);
					return dto;
				});
	}

	private Mono<Tuple2<Item, Integer>> findItemWithCount(Long cartId, Long itemId) {
		return databaseClient.sql(
						"SELECT i.*, COALESCE(ci.count, 0) as item_count " +
								"FROM items i " +
								"LEFT JOIN cart_item ci ON i.id = ci.item_id AND ci.cart_id = :cartId " +
								"WHERE i.id = :itemId")
				.bind("cartId", cartId)
				.bind("itemId", itemId)
				.map((row, metadata) -> {
					Item item = new Item();
					item.setId(row.get("id", Long.class));
					item.setTitle(row.get("title", String.class));
					item.setDescription(row.get("description", String.class));
					item.setImgPath(row.get("img_path", String.class));
					item.setPrice(row.get("price", Long.class));

					Integer count = row.get("item_count", Integer.class);

					return Tuples.of(item, Objects.requireNonNull(count));
				})
				.one()
				.switchIfEmpty(Mono.error(new ResourceNotFoundException(String.format("Item with id: %d not found.", itemId))));
	}

	private void setImgUrl(ItemInCartDTO item) {
		item.imgPath("/localhost:8080" + item.imgPath());
	}
}
