package ru.goncharenko.market.item.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.goncharenko.market.item.model.CartItem;

import java.util.Collection;

public interface CartItemRepository extends ReactiveCrudRepository<CartItem, Long> {
	Flux<CartItem> findAllByCartIdOrderByItemId(Long cartId);

	Mono<CartItem> findByCartIdAndItemId(Long cartId, Long itemId);

	Flux<CartItem> findByCartIdAndItemIdIn(Long cartId, Collection<Long> itemIds);
}
