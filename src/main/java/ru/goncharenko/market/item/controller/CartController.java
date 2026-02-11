package ru.goncharenko.market.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import ru.goncharenko.market.core.types.ActionEnum;
import ru.goncharenko.market.item.dto.CartDTO;
import ru.goncharenko.market.item.service.CartService;

@RestController
@RequestMapping(path = "/cart")
@RequiredArgsConstructor
public class CartController {
	private final CartService service;

	@GetMapping(path = "/items")
	public ModelAndView show() {
		CartDTO cartDTO = service.getItemsInCart();
		ModelAndView modelAndView = new ModelAndView("cart");
		modelAndView.addObject("items", cartDTO.getItems());
		modelAndView.addObject("total", cartDTO.getTotal());

		return modelAndView;
	}

	@PostMapping(path = "/items")
	public ModelAndView changeItemsCountFromCart(@RequestParam long id, @RequestParam ActionEnum action) {
		CartDTO cartDTO =  service.changeItemsCountFromCart(id, action);
		ModelAndView modelAndView = new ModelAndView("cart");
		modelAndView.addObject("items", cartDTO.getItems());
		modelAndView.addObject("total", cartDTO.getTotal());

		return modelAndView;
	}
}
