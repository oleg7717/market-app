package ru.goncharenko.market.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.goncharenko.market.core.types.ActionEnum;
import ru.goncharenko.market.item.dto.CartDTO;
import ru.goncharenko.market.item.service.CartService;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
	private final CartService service;

	@GetMapping(path = "/items")
	public CartDTO show() {
		return service.getItemsInCart();
	}

	@PostMapping(path = "/items")
	public CartDTO changeItemsCountFromCart(@RequestParam long id, @RequestParam ActionEnum action) {
		return service.changeItemsCountFromCart(id, action);
	}
}
