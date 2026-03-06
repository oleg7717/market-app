package ru.goncharenko.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.goncharenko.payment.model.Payment;
import ru.goncharenko.payment.repository.AccountRepository;
import ru.goncharenko.payment.response.ApiBalanceGet200Response;

@Service
@RequiredArgsConstructor
public class AccountService {
	private final AccountRepository repository;

	public Mono<ResponseEntity<ApiBalanceGet200Response>> getUserBalance(String userName) {
		return repository.findByUserName(userName).map(account -> ResponseEntity.ok()
						.body(new ApiBalanceGet200Response().balance(account.getBalance()))
				)
				.switchIfEmpty(Mono.error(new ResponseStatusException(
						HttpStatus.NOT_FOUND, "Account for user: " + userName + " not found."
				)));
	}

	@Transactional
	public Mono<ResponseEntity<String>> makePayment(Payment payment) {
		String userName = payment.getUserName();
		Double amount = payment.getOrderAmount() == null ? 0 : payment.getOrderAmount();
		return repository.findByUserName(userName).flatMap(account -> {
					Double balance = account.getBalance();
					if (balance < amount) {
						return Mono.just(ResponseEntity
								.status(HttpStatus.PAYMENT_REQUIRED)
								.body("There are insufficient funds in the account."));
					}
					account.setBalance(balance - amount);
					return repository.save(account)
							.map(savedAccount -> ResponseEntity.ok().body("Payment completed."));
				})
				.switchIfEmpty(
						Mono.just(ResponseEntity
								.status(HttpStatus.NOT_FOUND)
								.body("Account for user: " + userName + " not found.")
						)
				);
	}
}
