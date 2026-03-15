package ru.goncharenko.market.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.goncharenko.market.core.config.security.utils.SecurityUtils;
import ru.goncharenko.market.core.types.ActionEnum;
import ru.goncharenko.market.item.dto.CartContext;
import ru.goncharenko.market.item.dto.CartDTO;
import ru.goncharenko.market.item.mapper.CartItemMapper;
import ru.goncharenko.market.item.model.Cart;
import ru.goncharenko.market.item.model.CartItem;
import ru.goncharenko.market.item.repository.CartItemRepository;
import ru.goncharenko.market.item.repository.CartRepository;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final ItemCacheService cacheService;
	private final SecurityUtils securityUtils;
	private final CartItemMapper mapper;

	@Transactional
	public Mono<Cart> getOrCreateCart() {
		return securityUtils.getCurrentAuthentication()
				.flatMap(authentication -> {
					if (!(authentication instanceof AnonymousAuthenticationToken)) {
						String userName = authentication.getName();
						return cartRepository.findCartByUserName(userName).switchIfEmpty(Mono.defer(() -> {
							Cart newCart = new Cart();
							newCart.setUserName(userName);
							return cartRepository.save(newCart);
						}));
					}

					return Mono.empty();
				});
	}

	@Transactional
	public Mono<CartDTO> getItemsInCart() {
		return getOrCreateCart()
				.flatMap(cart -> cartItemRepository.findAllByCartIdOrderByItemId(cart.getId())
						.collectList()
						.map(items -> new CartContext(cart, items)))
				.flatMap(this::enrichItemsWithDetails)
				.map(mapper::buildCartDTO);
	}

	private Mono<CartContext> enrichItemsWithDetails(CartContext context) {
		if (context.getItems().isEmpty()) {
			return Mono.just(context);
		}

		return cacheService.loadItemsFromCache(context)
				.flatMap(cachedItems -> {
					boolean equals = cachedItems.size() == context.getItems().size()
							&& cachedItems.keySet().equals(
							context.getItems().stream()
									.map(CartItem::getItemId)
									.collect(Collectors.toSet()));
					if (!cachedItems.isEmpty() && equals) {
						return Mono.just(cachedItems);
					}

					return cacheService.loadItemsFromDbAndCache(context);
				})
				.map(itemsMap -> {
					context.setItemMap(itemsMap);
					return context;
				});
	}

	@Transactional
	public Mono<Void> changeItemsCountFromCart(long id, ActionEnum action) {
		return getOrCreateCart()
				.flatMap(cart -> switch (action) {
					case MINUS -> decreaseItemCount(cart, id);
					case PLUS -> increaseItemCount(cart, id);
				});
	}

	private Mono<Void> decreaseItemCount(Cart cart, Long itemId) {
		return cartItemRepository.findByCartIdAndItemId(cart.getId(), itemId)
				.switchIfEmpty(Mono.empty())
				.flatMap(cartItem -> {
					if (cartItem.getCount() == 1) {
						cacheService.removeItemInCartFromCache(itemId, cart.getUserName()).subscribe();
						return cartItemRepository.delete(cartItem);
					} else {
						cartItem.setCount(cartItem.getCount() - 1);
						return cartItemRepository.save(cartItem);
					}
				})
				.then();
	}

	private Mono<Void> increaseItemCount(Cart cart, Long itemId) {
		Long cartId = cart.getId();
		return cartItemRepository.findByCartIdAndItemId(cartId, itemId)
				.flatMap(cartItem -> {
					cartItem.setCount(cartItem.getCount() + 1);
					return cartItemRepository.save(cartItem);
				})
				.switchIfEmpty(Mono.defer(() -> {
					cacheService.addItemInCartToCache(itemId, cart.getUserName()).subscribe();
					CartItem newCartItem = new CartItem();
					newCartItem.setCartId(cartId);
					newCartItem.setItemId(itemId);
					newCartItem.setCount(1);
					return cartItemRepository.save(newCartItem);
				}))
				.then();
	}
}
