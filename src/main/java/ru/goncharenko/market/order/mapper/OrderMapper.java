package ru.goncharenko.market.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.goncharenko.market.item.dto.ItemInOrderDTO;
import ru.goncharenko.market.order.dto.OrderDTO;
import ru.goncharenko.market.order.model.Order;
import ru.goncharenko.market.order.model.OrderItem;

@Mapper(
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
		componentModel = MappingConstants.ComponentModel.SPRING,
		unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderMapper {
	@Mapping(source = "orderItems", target = "items")
	OrderDTO toOrderDto(Order order);

	@Mapping(source = "item.id", target = "id")
	@Mapping(source = "item.title", target = "title")
	@Mapping(source = "item.price", target = "price")
	ItemInOrderDTO toDTO(OrderItem orderItem);

//	ItemDto toItemDto(OrderItem orderItem);
}

