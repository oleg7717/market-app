package ru.goncharenko.market.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.goncharenko.market.core.types.ActionEnum;
import ru.goncharenko.market.item.dto.CartContext;
import ru.goncharenko.market.item.dto.CartDTO;
import ru.goncharenko.market.item.mapper.CartItemMapper;
import ru.goncharenko.market.item.model.Cart;
import ru.goncharenko.market.item.model.CartItem;
import ru.goncharenko.market.item.model.Item;
import ru.goncharenko.market.item.repository.CartItemRepository;
import ru.goncharenko.market.item.repository.CartRepository;
import ru.goncharenko.market.item.repository.ItemRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final ItemRepository itemRepository;
	private final CartItemMapper mapper;
	private final DatabaseClient databaseClient;

	private final String userName = "anonymous";

	@Transactional
	public Mono<Cart> getOrCreateCart() {
		return cartRepository.findCartByUserName(userName).switchIfEmpty(Mono.defer(() -> {
			Cart newCart = new Cart();
			newCart.setUserName(userName);
			return cartRepository.save(newCart);
		}));
	}

	@Transactional
	public Mono<CartDTO> getItemsInCart() {
		return getOrCreateCart()
				.flatMap(cart -> cartItemRepository.findAllByCartId(cart.getId())
						.collectList()
						.map(items -> new CartContext(cart, items)))
				.flatMap(this::enrichItemsWithDetails)
				.map(mapper::buildCartDTO);
	}

	private Mono<CartContext> enrichItemsWithDetails(CartContext context) {
		if (context.getItems().isEmpty()) {
			return Mono.just(context);
		}

		return loadItemsMap(context.getItems())
				.map(itemsMap -> {
					context.setItemMap(itemsMap);
					return context;
				});
	}

	private Mono<Map<Long, Item>> loadItemsMap(List<CartItem> items) {
		List<Long> itemIds = extractItemIds(items);
		return itemRepository.findAllByIdIn(itemIds)
				.collectMap(Item::getId);
	}

	private List<Long> extractItemIds(List<CartItem> items) {
		return items.stream()
				.map(CartItem::getItemId)
				.collect(Collectors.toList());
	}

	@Transactional
	public Mono<Void> changeItemsCountFromCart(long id, ActionEnum action) {
		return getOrCreateCart()
				.flatMap(cart -> switch (action) {
					case MINUS -> decreaseItemCount(cart.getId(), id);
					case PLUS -> increaseItemCount(cart.getId(), id);
				});
	}

	private Mono<Void> decreaseItemCount(Long cartId, Long itemId) {
		return cartItemRepository.findByCartIdAndItemId(cartId, itemId)
				.flatMap(cartItem -> {
					if (cartItem.getCount() == 1) {
						return databaseClient
								.sql(String.format("delete from cart_item " +
												"where cart_id = %d and item_id = %d",
										cartId,
										itemId))
								.fetch()
								.one()
								.then();
					} else {
						return databaseClient
								.sql(String.format("update cart_item set count = %d " +
												"where cart_id = %d and item_id = %d",
										(cartItem.getCount() - 1),
										cartId,
										itemId))
								.fetch()
								.one()
								.then();
					}
				});
	}

	private Mono<Void> increaseItemCount(Long cartId, Long itemId) {
		return databaseClient.sql(
						"SELECT count FROM cart_item WHERE cart_id = :cartId AND item_id = :itemId")
				.bind("cartId", cartId)
				.bind("itemId", itemId)
				.map(row -> Objects.requireNonNull(row.get("count", Integer.class)))
				.all()
				.collectList()
				.flatMap(counts -> {
					if (counts.isEmpty()) {
						return databaseClient.sql(
										"insert into cart_item (item_id, cart_id, count) values (:itemId, :cartId, 1)")
								.bind("itemId", itemId)
								.bind("cartId", cartId)
								.fetch()
								.rowsUpdated()
								.then();
					} else {
						int currentCount = counts.getFirst();
						return databaseClient.sql(
										"UPDATE cart_item SET count = :count WHERE cart_id = :cartId AND item_id = :itemId")
								.bind("count", currentCount + 1)
								.bind("cartId", cartId)
								.bind("itemId", itemId)
								.fetch()
								.rowsUpdated()
								.then();
					}
				});
	}
}
