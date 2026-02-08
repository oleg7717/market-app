package ru.goncharenko.market.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.goncharenko.market.order.dto.ListOrdersDTO;
import ru.goncharenko.market.order.dto.OrderDTO;
import ru.goncharenko.market.core.exception.ResourceNotFoundException;
import ru.goncharenko.market.order.dto.SingleOrderDTO;
import ru.goncharenko.market.order.mapper.OrderMapper;
import ru.goncharenko.market.order.model.Order;
import ru.goncharenko.market.order.repository.OrderRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository repository;
	private final OrderMapper mapper;

	public ListOrdersDTO getAllOrders() {
		List<Order> orders = repository.findAllOrders();
		return mapper.ordersToListOrderDTO(orders);
	}

	public SingleOrderDTO findById(long id, String newOrder) {
		Order order = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(String.format("Order with id: %s not found", id)));
		OrderDTO orderDTO = mapper.toOrderDto(order);
		orderDTO.setNewOrder(newOrder);
		return SingleOrderDTO.builder().order(orderDTO).build();
	}
}
