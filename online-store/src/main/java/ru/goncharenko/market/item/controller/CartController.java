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
import ru.goncharenko.market.order.service.PurchaseService;

@Controller
@RequestMapping(path = "/cart")
@RequiredArgsConstructor
public class CartController {
	private final CartService cartService;
	private final PurchaseService purchaseService;
	private final ImageUrlBuilder imageUrlBuilder;

	@GetMapping(path = "/items")
	public Mono<Rendering> show(ServerWebExchange exchange) {
		return cartService.getItemsInCart()
				.map(cartDTO -> imageUrlBuilder.enrichCartDTOWithImageUrls(cartDTO, exchange))
				.flatMap(cartDTO ->
						purchaseService.isSufficientBalance("oleg", cartDTO.getTotal())
								.flatMap(response -> Mono.just(Rendering.view("cart")
										.modelAttribute("items", cartDTO.getItems())
										.modelAttribute("total", cartDTO.getTotal())
										.modelAttribute("canBuy", response)
										.build()))
				);
	}

	@PostMapping(path = "/items")
	public Mono<Rendering> changeItemsCountFromCart(@ModelAttribute ItemRequest request, ServerWebExchange exchange) {
		return cartService.changeItemsCountFromCart(request.getId(), request.getAction()).then(
				cartService.getItemsInCart()
						.map(cartDTO -> imageUrlBuilder.enrichCartDTOWithImageUrls(cartDTO, exchange))
						.flatMap(cartDTO ->
								purchaseService.isSufficientBalance("oleg", cartDTO.getTotal())
										.flatMap(response -> Mono.just(Rendering.view("cart")
												.modelAttribute("items", cartDTO.getItems())
												.modelAttribute("total", cartDTO.getTotal())
												.modelAttribute("canBuy", response)
												.build()))
						)
		);
	}
}
