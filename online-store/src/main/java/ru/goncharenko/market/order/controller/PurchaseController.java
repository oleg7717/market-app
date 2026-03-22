package ru.goncharenko.market.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.goncharenko.market.order.dto.OrderDTO;
import ru.goncharenko.market.order.service.PurchaseService;

@Controller
@RequiredArgsConstructor
public class PurchaseController {
	private final PurchaseService service;

	@GetMapping(path = "/buy")
	public Mono<Boolean> buy(Double orderAmount) {
		return service.isSufficientBalance(orderAmount);
	}

	@PostMapping(path = "/buy")
	public Mono<Rendering> buy() {
		Flux<OrderDTO> order = service.makePayment();
		Rendering rendering = Rendering.view("order")
				.modelAttribute("order", order.next())
				.build();
		return Mono.just(rendering);
	}
}
