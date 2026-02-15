package ru.goncharenko.market.order.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BuyController {
	@PostMapping(path = "/path")
	public String buy(long id) {
		return String.format("redirect:/orders/%s?newOrder=true", id);
	}
}
