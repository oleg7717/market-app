package ru.goncharenko.payment.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ru.goncharenko.payment.model.Account;

public interface AccountRepository extends ReactiveCrudRepository<Account, Long> {
	Mono<Account> findById(Long id);

	Mono<Account> findByUserName(String userName);
}
