package ru.goncharenko.market.order.mapper;

import org.springframework.stereotype.Component;
import ru.goncharenko.market.order.dto.ItemInOrderDTO;
import ru.goncharenko.market.order.dto.OrderDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class OrderMapper {
	public OrderDTO mapToOrderDto(List<Map<String, Object>> rows) {
		if (rows.isEmpty()) {
			return null;
		}

		Map<String, Object> firstRow = rows.getFirst();
		OrderDTO orderDto = new OrderDTO();
		orderDto.id(((Number) firstRow.get("order_id")).longValue());
		orderDto.totalSum(((Number) firstRow.get("total_sum")).doubleValue());

		List<ItemInOrderDTO> items = new ArrayList<>();

		for (Map<String, Object> row : rows) {
			if (row.get("order_item_id") != null) {
				ItemInOrderDTO itemDto = new ItemInOrderDTO();
				itemDto.id(((Number) row.get("item_id")).longValue());
				itemDto.title((String) row.get("title"));
				itemDto.price(((Number) row.get("price")).doubleValue());
				itemDto.count(((Number) row.get("count")).intValue());

				items.add(itemDto);
			}
		}
		orderDto.items(items);

		return orderDto;
	}
}

