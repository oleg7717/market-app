package ru.goncharenko.market.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.goncharenko.market.item.dto.CartDTO;
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

//		Rendering rendering = Rendering.view("cart")
//				.modelAttribute("items", cartDTO.getItems())
//				.modelAttribute("total", cartDTO.getTotal())
//				.build();
//		return Mono.just(rendering);
	}

/*	@PostMapping(path = "/items")
	public Mono<Rendering> changeItemsCountFromCart(@RequestParam long id, @RequestParam ActionEnum action) {
		service.changeItemsCountFromCart(id, action);
		Flux<CartDTO> cartDTO = service.getItemsInCart();

		Rendering rendering = Rendering.view("cart")
				.modelAttribute("items", cartDTO.getItems())
				.modelAttribute("total", cartDTO.getTotal())
				.build();
		return Mono.just(rendering);

		return rendering;
	}*/
}
