package ru.goncharenko.market.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.goncharenko.market.item.model.Cart;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
	@Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems ic LEFT JOIN FETCH ic.item i " +
			"where i.id = :itemId and c.userName = :userName")
	Cart findByItemId(long itemId, String userName);

	@Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems ic LEFT JOIN FETCH ic.item i where c.userName = :userName")
	Cart findAllByUserName(String userName);
}
