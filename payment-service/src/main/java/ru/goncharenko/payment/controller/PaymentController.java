package ru.goncharenko.payment.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.goncharenko.payment.model.Payment;
import ru.goncharenko.payment.model.PaymentStatus;
import ru.goncharenko.payment.service.AccountService;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-03-05T18:56:04.818676400+03:00[GMT+03:00]", comments = "Generator version: 7.20.0")
@Controller
@RequestMapping("${openapi.payment.base-path:}")
@RequiredArgsConstructor
public class PaymentController implements PaymentApi {
	private final AccountService service;

	@RequestMapping(
			method = RequestMethod.GET,
			value = ru.goncharenko.payment.controller.PaymentApi.PATH_API_BALANCE_GET,
			produces = { "application/json" }
	)
	public Mono<ResponseEntity<PaymentStatus>> apiBalanceGet(
			@NotNull @Parameter(name = "userName", description = "Логин пользователя", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "userName", required = true) String userName,
			@NotNull @Parameter(name = "orderAmount", description = "Сумма заказа", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "orderAmount", required = true) Double orderAmount,
			@Parameter(hidden = true) final ServerWebExchange exchange
	) {
		return service.getUserBalance(userName, orderAmount);
	}

	@RequestMapping(
			method = RequestMethod.POST,
			value = ru.goncharenko.payment.controller.PaymentApi.PATH_API_BALANCE_POST,
			produces = { "application/json" },
			consumes = { "application/json" }
	)
	public Mono<ResponseEntity<PaymentStatus>> apiBalancePost(
			@Parameter(name = "Payment", description = "", required = true) @Valid @RequestBody Mono<Payment> payment,
			@Parameter(hidden = true) final ServerWebExchange exchange
	) {
		return service.makePayment(payment);
	}
}
