package ru.goncharenko.market.user.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ru.goncharenko.market.user.model.User;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {
	Mono<User> findByUserName(String username);
}
