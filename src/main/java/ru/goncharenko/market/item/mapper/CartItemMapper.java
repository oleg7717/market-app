package ru.goncharenko.market.item.mapper;

import jakarta.servlet.http.HttpServletRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.goncharenko.market.item.dto.CartDTO;
import ru.goncharenko.market.item.dto.ItemInCartDTO;
import ru.goncharenko.market.item.model.Cart;
import ru.goncharenko.market.item.model.CartItem;
import ru.goncharenko.market.item.model.Item;

import java.util.Collections;
import java.util.List;

import static ru.goncharenko.market.item.service.ItemService.getBaseUrl;

@Mapper(
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
		componentModel = MappingConstants.ComponentModel.SPRING,
		unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CartItemMapper {
	ItemInCartDTO toItemInCart(Item item);

	List<ItemInCartDTO> toItemListInCart(List<Item> item);

	@Mapping(source = "item.id", target = "id")
	@Mapping(source = "item.title", target = "title")
	@Mapping(source = "item.description", target = "description")
	@Mapping(source = "item.imgPath", target = "imgPath")
	@Mapping(source = "item.price", target = "price")
	ItemInCartDTO toItemInCartDto(CartItem item);

	List<ItemInCartDTO> toItemInCartList(List<CartItem> item);

	default CartDTO onlyItemsInCart(Cart cart) {
		if (cart == null) {
			return new CartDTO(Collections.emptyList(), 0L);
		}

		List<ItemInCartDTO> itemsInCartDTO = toItemInCartList(cart.getCartItems());
		long total = itemsInCartDTO.stream()
				.mapToLong(itemInCart -> itemInCart.price() * itemInCart.count())
				.sum();

		return new CartDTO(itemsInCartDTO, total);
	}

	default void setCountToItem(Cart cart, ItemInCartDTO itemDTO) {
		if (cart.getCartItems() != null) {
			cart.getCartItems().forEach(itemInCart -> {
				if (itemInCart.getItem().getId() == itemDTO.id()) {
					itemDTO.count(itemInCart.getCount());
				}
			});
		}
	}

	default void setImgUrl(List<ItemInCartDTO> items, HttpServletRequest request) {
		items.forEach(item -> setImgUrl(item, request));
	}

	default void setImgUrl(ItemInCartDTO item, HttpServletRequest request) {
		String imageUrl = getBaseUrl(request) + item.imgPath();
		item.imgPath(imageUrl);
	}
}
