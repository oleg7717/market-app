package ru.goncharenko.market.item.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.goncharenko.market.item.dto.ItemInCartDTO;

import java.util.List;

@RestController
public class CartController {
	@GetMapping(path = "/cart/items")
	public List<ItemInCartDTO> show() {
		return null;
	}

	@PostMapping(path = "/cart/items")
	public List<ItemInCartDTO> chageItemsCountFromCart(
			@RequestParam(required = true) long id,
			@RequestParam(required = true) String action) {
		return null;
	}


}
