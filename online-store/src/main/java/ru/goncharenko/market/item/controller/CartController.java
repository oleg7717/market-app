package ru.goncharenko.market.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.goncharenko.market.item.dto.ItemRequest;
import ru.goncharenko.market.item.service.CartService;
import ru.goncharenko.market.item.service.ImageUrlBuilder;

@Controller
@RequestMapping(path = "/cart")
@RequiredArgsConstructor
public class CartController {
	private final CartService service;
	private final ImageUrlBuilder imageUrlBuilder;

	@GetMapping(path = "/items")
	public Mono<Rendering> show(ServerWebExchange exchange) {
		return service.getItemsInCart()
				.map(cartDTO -> imageUrlBuilder.enrichCartDTOWithImageUrls(cartDTO, exchange))
				.flatMap(cartDTO ->
						Mono.just(Rendering.view("cart")
								.modelAttribute("items", cartDTO.getItems())
								.modelAttribute("total", cartDTO.getTotal())
								.build())
				);
	}

	@PostMapping(path = "/items")
	public Mono<Rendering> changeItemsCountFromCart(@ModelAttribute ItemRequest request, ServerWebExchange exchange) {
		return service.changeItemsCountFromCart(request.getId(), request.getAction()).then(
				service.getItemsInCart()
						.map(cartDTO -> imageUrlBuilder.enrichCartDTOWithImageUrls(cartDTO, exchange))
						.flatMap(cartDTO ->
								Mono.just(Rendering.view("cart")
										.modelAttribute("items", cartDTO.getItems())
										.modelAttribute("total", cartDTO.getTotal())
										.build())
						)
		);
	}
}
