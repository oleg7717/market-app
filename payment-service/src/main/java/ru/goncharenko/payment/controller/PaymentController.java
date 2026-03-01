package ru.goncharenko.payment.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {
	@GetMapping("/diagnostic/liquibase")
	public void diagnostic() {
	}
}
