package ru.goncharenko.market.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.goncharenko.market.item.dto.ItemRequest;
import ru.goncharenko.market.item.service.CartService;

@Controller
@RequestMapping(path = "/cart")
@RequiredArgsConstructor
public class CartController {
	private final CartService service;

	@GetMapping(path = "/items")
	public Mono<Rendering> show() {
		return service.getItemsInCart().flatMap(cartDTO ->
			Mono.just(Rendering.view("cart")
					.modelAttribute("items", cartDTO.getItems())
					.modelAttribute("total", cartDTO.getTotal())
					.build())
		);
	}

	@PostMapping(path = "/items")
	public Mono<Rendering> changeItemsCountFromCart(@ModelAttribute ItemRequest request) {
		return service.changeItemsCountFromCart(request.getId(), request.getAction()).then(
				service.getItemsInCart().flatMap(cartDTO ->
						Mono.just(Rendering.view("cart")
								.modelAttribute("items", cartDTO.getItems())
								.modelAttribute("total", cartDTO.getTotal())
								.build())
				)
		);
	}
}
