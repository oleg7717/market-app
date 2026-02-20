package ru.goncharenko.market.item.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import ru.goncharenko.market.item.model.CartItem;

import java.util.Optional;

public interface CartItemRepository extends ReactiveCrudRepository<CartItem, Long> {
//	Optional<CartItem> findItemInCartByUserNameAndItemId(String userName, Long itemId);

//	@Transactional
//	void deleteByUsernameAndItemId(String username, Long id);

	Flux<CartItem> findAllByCartId(Long cartId);
}
