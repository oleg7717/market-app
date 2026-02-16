package ru.goncharenko.market.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.goncharenko.market.item.model.CartItem;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	@Query("SELECT ic from CartItem ic LEFT JOIN FETCH ic.cart c LEFT JOIN FETCH ic.item i " +
			"where i.id = :itemId and c.userName = :userName")
	Optional<CartItem> findItemInCartById(Long itemId, String userName);
}
