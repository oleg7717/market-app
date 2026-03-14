package ru.goncharenko.market.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@Controller
public class LogoutController {
	@GetMapping("/confirm-logout")
	public Mono<String> confirmLogout() {
		return Mono.just("confirm-logout");
	}
}
