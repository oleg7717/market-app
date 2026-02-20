package ru.goncharenko.market.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.goncharenko.market.item.model.Cart;
import ru.goncharenko.market.item.model.CartItem;
import ru.goncharenko.market.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class CartContext {
	private final Cart cart;
	private final List<CartItem> items;
	private Map<Long, Item> itemMap = new HashMap<>();

	public CartContext(Cart cart, List<CartItem> items) {
		this.cart = cart;
		this.items = items;
	}
}
