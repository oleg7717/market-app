package ru.goncharenko.market.item.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.goncharenko.market.core.types.ActionEnum;
import ru.goncharenko.market.item.dto.CartDTO;
import ru.goncharenko.market.item.mapper.CartItemMapper;
import ru.goncharenko.market.item.model.Cart;
import ru.goncharenko.market.item.model.CartItem;
import ru.goncharenko.market.item.repository.CartItemRepository;
import ru.goncharenko.market.item.repository.CartRepository;
import ru.goncharenko.market.item.repository.ItemRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final ItemRepository itemRepository;
	private final CartItemMapper mapper;
	private final HttpServletRequest request;

	private final String userName = "anonymous";

	@Transactional
	public Cart getOrCreateCart() {
		return cartRepository.findCartByUserName(userName)
				.orElseGet(() -> {
					Cart newCart = new Cart();
					newCart.setUserName(userName);
					return cartRepository.save(newCart);
				});
	}

	@Transactional(readOnly = true)
	public CartDTO getItemsInCart() {
		Cart cart = getOrCreateCart();
		CartDTO cartDTO = mapper.onlyItemsInCart(cart);
		mapper.setImgUrl(cartDTO.getItems(), request);

		return cartDTO;
	}

	@Transactional
	public void changeItemsCountFromCart(long id, ActionEnum action) {
		Optional<CartItem> itemInCart = cartItemRepository.findItemInCartByUserNameAndItemId(userName, id);
		switch (action) {
			case MINUS -> decreaseItemCount(itemInCart, id);
			case PLUS -> increaseItemCount(itemInCart, id);
		}
	}

	@Transactional
	void decreaseItemCount(Optional<CartItem> itemInCart, Long id) {
		itemInCart.ifPresent(item -> {
			if (item.getCount() == 1) {
				cartItemRepository.deleteByUsernameAndItemId(userName, id);
			} else {
				item.removeOne();
			}
		});
	}

	@Transactional
	void increaseItemCount(Optional<CartItem> itemInCart, Long id) {
		itemInCart.ifPresentOrElse(CartItem::addOne, () -> {
			itemRepository.findById(id).ifPresent(item -> {
				CartItem cartItem = new CartItem();
				cartItem.setCount(1);
				cartItem.setItem(item);
				cartItem.setCart(getOrCreateCart());
				cartItemRepository.save(cartItem);
			});
		});
	}
}
