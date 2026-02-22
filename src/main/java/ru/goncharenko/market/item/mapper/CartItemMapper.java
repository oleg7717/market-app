package ru.goncharenko.market.item.mapper;

import org.springframework.stereotype.Component;
import ru.goncharenko.market.item.dto.CartContext;
import ru.goncharenko.market.item.dto.CartDTO;
import ru.goncharenko.market.item.dto.ItemInCartDTO;
import ru.goncharenko.market.item.model.CartItem;
import ru.goncharenko.market.item.model.Item;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CartItemMapper {
	public ItemInCartDTO toItemInCart(Item item) {
		if (item == null) {
			return null;
		}

		return ItemInCartDTO.builder()
				.id(item.getId())
				.title(item.getTitle())
				.description(item.getDescription())
				.imgPath(item.getImgPath())
				.price(item.getPrice())
				.build();
	}

	public CartDTO buildCartDTO(CartContext context) {
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
				setImgUrl(item),
				item.getPrice(),
				cartItem.getCount()
		);
	}

	private String setImgUrl(Item item) {
		return "/localhost:8080" + item.getImgPath();
	}
}
