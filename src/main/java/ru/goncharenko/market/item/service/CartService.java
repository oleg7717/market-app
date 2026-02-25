package ru.goncharenko.market.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.goncharenko.market.core.exception.ResourceNotFoundException;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final ItemRepository itemRepository;
	private final CartItemMapper mapper;

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
	public Mono<CartDTO> getItemsInCart(ServerWebExchange exchange) {
		return getOrCreateCart()
				.flatMap(cart -> cartItemRepository.findAllByCartId(cart.getId())
						.collectList()
						.map(items -> new CartContext(cart, items)))
				.flatMap(this::enrichItemsWithDetails)
				.map(cartContext -> mapper.buildCartDTO(cartContext, exchange));
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
				.switchIfEmpty(
						Mono.error(new ResourceNotFoundException(String.format("Item with id: %d not found.", itemId))))
				.flatMap(cartItem -> {
					if (cartItem.getCount() == 1) {
						return cartItemRepository.delete(cartItem);
					} else {
						cartItem.setCount(cartItem.getCount() - 1);
						return cartItemRepository.save(cartItem);
					}
				})
				.then();
	}

	private Mono<Void> increaseItemCount(Long cartId, Long itemId) {
		return cartItemRepository.findByCartIdAndItemId(cartId, itemId)
				.flatMap(cartItem -> {
					cartItem.setCount(cartItem.getCount() + 1);
					return cartItemRepository.save(cartItem);
				})
				.switchIfEmpty(Mono.defer(() -> {
					CartItem newCartItem = new CartItem();
					newCartItem.setCartId(cartId);
					newCartItem.setItemId(itemId);
					newCartItem.setCount(1);
					return cartItemRepository.save(newCartItem);
				}))
				.then();
	}
}
