package ru.goncharenko.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.goncharenko.payment.model.Payment;
import ru.goncharenko.payment.model.PaymentStatus;
import ru.goncharenko.payment.repository.AccountRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class AccountService {
	private final AccountRepository repository;

	public Mono<ResponseEntity<PaymentStatus>> getUserBalance(String userName, Double orderAmount) {
		return repository.findByUserName(userName).flatMap(account -> {
							Double balance = account.getBalance();
							if (balance < orderAmount) {
								return Mono.just(ResponseEntity.ok()
										.body(new PaymentStatus()
												.code(HttpStatus.PAYMENT_REQUIRED.value())
												.message("Недостаточно средств на счету")
												.processed(false)));
							}
							return Mono.just(ResponseEntity.ok()
									.body(new PaymentStatus()
											.code(HttpStatus.OK.value())
											.message("Достаточно средств на счету")
											.processed(true)));
						}
				)
				.switchIfEmpty(Mono.error(new ResponseStatusException(
						HttpStatus.NOT_FOUND, String.format("У пользователя %s нет счета в банке ", userName)
				)));
	}

	@Transactional
	public Mono<ResponseEntity<PaymentStatus>> makePayment(Mono<Payment> payment) {
		return payment.flatMap(pay -> {
			String userName = pay.getUserName();
			Double amount = pay.getOrderAmount() == null ? 0 : pay.getOrderAmount();
			return repository.findByUserName(userName).flatMap(account -> {
						Double balance = account.getBalance();
						if (balance < amount) {
							return Mono.just(ResponseEntity.ok()
									.body(new PaymentStatus()
											.code(HttpStatus.PAYMENT_REQUIRED.value())
											.message("Недостаточно средств на счету")
											.processed(false)));
						}
						account.setBalance(BigDecimal.valueOf(balance)
								.subtract(BigDecimal.valueOf(amount))
								.setScale(2, RoundingMode.HALF_UP)
								.doubleValue()
						);
						return repository.save(account)
								.map(savedAccount -> ResponseEntity.ok()
										.body(new PaymentStatus()
												.code(HttpStatus.OK.value())
												.message("Платёж совершён")
												.processed(true)));
					})
					.switchIfEmpty(Mono.error(new ResponseStatusException(
							HttpStatus.NOT_FOUND, String.format("У пользователя %s нет счета в банке ", userName)
					)));
		});
	}
}
