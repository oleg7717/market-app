package ru.goncharenko.market.item.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.goncharenko.market.core.types.ActionEnum;
import ru.goncharenko.market.item.dto.CartDTO;
import ru.goncharenko.market.item.mapper.CartItemMapper;
import ru.goncharenko.market.item.model.Cart;
import ru.goncharenko.market.item.model.CartItem;
import ru.goncharenko.market.item.repository.CartItemRepository;
import ru.goncharenko.market.item.repository.CartRepository;

@Service
@RequiredArgsConstructor
public class CartService {
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final CartItemMapper mapper;
	private final HttpServletRequest request;

	private final String userName = "anonymous";

	public CartDTO getItemsInCart() {
		Cart cart = cartRepository.findAllByUserName(userName);
		CartDTO cartDTO = mapper.onlyItemsInCart(cart);
		mapper.setImgUrl(cartDTO.getItems(), request);

		return cartDTO;
	}

	@Transactional
	public void changeItemsCountFromCart(long id, ActionEnum action) {
		cartRepository.findAllByUserName(userName).getCartItems().forEach(itemInCart -> {
			if (itemInCart.getItem().getId() == id) {
				switch (action) {
					case MINUS -> decreaseItemCount(itemInCart);
					case PLUS -> increaseItemCount(itemInCart);
				}
			}
		});
	}

	@Transactional
	public void decreaseItemCount(CartItem itemInCart) {
		if (itemInCart == null) return;

		if (itemInCart.getCount() == 1) {
			cartItemRepository.deleteById(itemInCart.getId());
		} else {
			itemInCart.removeOne();
		}
	}

	private void increaseItemCount(CartItem itemInCart) {
		itemInCart.addOne();
	}
}
