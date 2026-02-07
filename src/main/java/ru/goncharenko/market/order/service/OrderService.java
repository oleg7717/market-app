package ru.goncharenko.market.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.goncharenko.market.order.dto.OrderDTO;
import ru.goncharenko.market.core.exception.ResourceNotFoundException;
import ru.goncharenko.market.order.mapper.OrderMapper;
import ru.goncharenko.market.order.model.Order;
import ru.goncharenko.market.order.repository.OrderRepository;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository repository;
	private final OrderMapper mapper;

	public OrderDTO findById(long id) {
		Order order = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(String.format("Order with id: %s not found", id)));
		return mapper.toOrderDto(order);
	}
}
