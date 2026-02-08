package ru.goncharenko.market.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.goncharenko.market.order.dto.ListOrdersDTO;
import ru.goncharenko.market.order.dto.SingleOrderDTO;
import ru.goncharenko.market.order.service.OrderService;

@RestController
@RequestMapping(path = "/orders")
@RequiredArgsConstructor
public class OrderController {
	private final OrderService service;

	@GetMapping()
	public ListOrdersDTO index() {
		return service.getAllOrders();
	}

	@GetMapping(path = "/{id}")
	public SingleOrderDTO index(@PathVariable long id, @RequestParam String newOrder) {
		return service.findById(id, newOrder);
	}
}
