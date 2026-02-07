package ru.goncharenko.market.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.goncharenko.market.order.dto.OrderDTO;
import ru.goncharenko.market.order.model.Order;
import ru.goncharenko.market.order.service.OrderService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {
	private final OrderService service;

	@GetMapping(path = "/orders")
	public List<Order> index() {
		return null;
	}

	@GetMapping(path = "/orders/{id}")
	public OrderDTO index(@PathVariable long id) {
		return service.findById(id);
	}

}
