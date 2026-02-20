package ru.goncharenko.market.item.mapper;

//import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import ru.goncharenko.market.item.dto.CartContext;
import ru.goncharenko.market.item.dto.CartDTO;
import ru.goncharenko.market.item.dto.ItemInCartDTO;
import ru.goncharenko.market.item.model.Cart;
import ru.goncharenko.market.item.model.CartItem;
import ru.goncharenko.market.item.model.Item;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//import static ru.goncharenko.market.item.service.ItemService.getBaseUrl;

@Component
public class CartItemMapper {
	public CartDTO buildCartDTO(CartContext context) {
		Cart cart = context.getCart();
		List<CartItem> items = context.getItems();
		Map<Long, Item> itemMap = context.getItemMap();

		List<ItemInCartDTO> itemDTOs = buildItemDTOs(items, itemMap);
		long total = calculateTotal(itemDTOs);

		return new CartDTO(
				itemDTOs,
				total
		);
	}

	private long calculateTotal(List<ItemInCartDTO> items) {
		return items.stream()
				.mapToLong(itemICart -> itemICart.price() * itemICart.count())
				.sum();
	}

	public List<ItemInCartDTO> buildItemDTOs(List<CartItem> items, Map<Long, Item> itemMap) {
		return items.stream()
				.map(item -> createCartItemDTO(item, itemMap.get(item.getItemId())))
				.collect(Collectors.toList());
	}

	private ItemInCartDTO createCartItemDTO(CartItem cartItem, Item item) {
		return new ItemInCartDTO(
				item.getId(),
				item.getTitle(),
				item.getDescription(),
				item.getImgPath(),
				item.getPrice(),
				cartItem.getCount()
		);
	}

/*	default void setImgUrl(List<ItemInCartDTO> items, HttpServletRequest request) {
		items.forEach(item -> setImgUrl(item, request));
	}

	default void setImgUrl(ItemInCartDTO item, HttpServletRequest request) {
		String imageUrl = getBaseUrl(request) + item.imgPath();
		item.imgPath(imageUrl);
	}*/
}
