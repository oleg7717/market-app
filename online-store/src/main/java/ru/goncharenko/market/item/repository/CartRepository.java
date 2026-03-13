package ru.goncharenko.market.item.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ru.goncharenko.market.item.model.Cart;

public interface CartRepository extends ReactiveCrudRepository<Cart, Long> {
	Mono<Cart> findCartByUserName(String userName);

	Mono<Void> deleteByUserName(String userName);
}
