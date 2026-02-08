package ru.goncharenko.market.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.goncharenko.market.order.dto.ItemInOrderDTO;
import ru.goncharenko.market.order.dto.ListOrdersDTO;
import ru.goncharenko.market.order.dto.OrderDTO;
import ru.goncharenko.market.order.model.Order;
import ru.goncharenko.market.order.model.OrderItem;

import java.util.Collections;
import java.util.List;

@Mapper(
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
		componentModel = MappingConstants.ComponentModel.SPRING,
		unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderMapper {
	@Mapping(source = "orderItems", target = "items")
	@Mapping(target = "newOrder", ignore = true)
	OrderDTO toOrderDto(Order order);

	default ListOrdersDTO ordersToListOrderDTO(List<Order> orders) {
		List<OrderDTO> orderListDTO = toOrderListDTO(orders);
		return new ListOrdersDTO(orderListDTO != null ? orderListDTO : Collections.emptyList());
	}

	List<OrderDTO> toOrderListDTO(List<Order> orders);

	@Mapping(source = "item.id", target = "id")
	@Mapping(source = "item.title", target = "title")
	@Mapping(source = "item.price", target = "price")
	@Mapping(target = "item.itemInCart", ignore = true)
	ItemInOrderDTO orderItemToItemInOrderDTO(OrderItem orderItem);
}

