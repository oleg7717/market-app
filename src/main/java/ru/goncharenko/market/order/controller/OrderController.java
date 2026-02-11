package ru.goncharenko.market.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import ru.goncharenko.market.order.dto.ListOrdersDTO;
import ru.goncharenko.market.order.dto.SingleOrderDTO;
import ru.goncharenko.market.order.service.OrderService;

@RestController
@RequestMapping(path = "/orders")
@RequiredArgsConstructor
public class OrderController {
	private final OrderService service;

	@GetMapping()
	public ModelAndView index() {
		ListOrdersDTO listOrdersDTO = service.getAllOrders();
		ModelAndView modelAndView = new ModelAndView("orders");
		modelAndView.addObject("orders", listOrdersDTO.getOrders());

		return modelAndView;
	}

	@GetMapping(path = "/{id}")
	public ModelAndView index(@PathVariable long id, @RequestParam(required = false) String newOrder) {
		SingleOrderDTO orderDTO = service.findById(id, newOrder);
		ModelAndView modelAndView = new ModelAndView("order");
		modelAndView.addObject("order", orderDTO.getOrder());
		modelAndView.addObject("newOrder", newOrder);

		return modelAndView;

	}
}
