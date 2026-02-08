package ru.goncharenko.market.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.goncharenko.market.item.dto.CartDTO;
import ru.goncharenko.market.item.dto.ItemInCartDTO;
import ru.goncharenko.market.item.model.Item;

import java.util.Collections;
import java.util.List;

@Mapper(
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
		componentModel = MappingConstants.ComponentModel.SPRING,
		unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ItemMapper {
	@Mapping(source = "itemInCart.count", target = "count")
	ItemInCartDTO itemInCart(Item item);

	List<ItemInCartDTO> itemListInCart(List<Item> item);

	default CartDTO onlyItemsInCart(List<Item> items) {
		if (items == null || items.isEmpty()) {
			return new CartDTO(Collections.emptyList(), 0L);
		}

		List<ItemInCartDTO> itemInCartDTO = itemListInCart(items);
		long total = itemInCartDTO.stream()
				.mapToLong(itemInCart -> itemInCart.getPrice() * itemInCart.getCount())
				.sum();

		return new CartDTO(itemInCartDTO, total);
	}
}
