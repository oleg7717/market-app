package ru.goncharenko.market.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.goncharenko.market.order.dto.OrderDTO;
import ru.goncharenko.market.order.service.OrderService;

@Controller
@RequestMapping(path = "/orders")
@RequiredArgsConstructor
public class OrderController {
	private final OrderService service;

	@GetMapping()
	public Mono<Rendering> index() {
		Flux<OrderDTO> orders = service.getAllOrders();
		Rendering rendering = Rendering.view("orders")
				.modelAttribute("orders", orders)
				.build();
		return Mono.just(rendering);
	}

	@GetMapping(path = "/{id}")
	public Mono<Rendering> index(@PathVariable long id,
	                             @RequestParam(name = "newOrder", required = false) String newOrder) {
		return service.findById(id)
				.map(order -> Rendering.view("order")
						.modelAttribute("order", order)
						.modelAttribute("newOrder", newOrder)
						.build()
				)
				.switchIfEmpty(Mono.just(Rendering.view("error/404")
						.status(HttpStatus.NOT_FOUND)
						.modelAttribute("message", "Заказ не найден")
						.modelAttribute("buttonText", "К списку заказов")
						.modelAttribute("returnUrl", "/orders")
						.build()
				));
	}
}
