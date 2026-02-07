package ru.goncharenko.market.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.goncharenko.market.item.dto.ItemInCartDTO;
import ru.goncharenko.market.item.model.Item;

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
}
