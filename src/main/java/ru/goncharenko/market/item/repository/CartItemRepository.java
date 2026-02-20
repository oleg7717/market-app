package ru.goncharenko.market.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.goncharenko.market.item.model.CartItem;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	@Query("SELECT ic from CartItem ic LEFT JOIN FETCH ic.cart c LEFT JOIN FETCH ic.item i " +
			"where c.userName = :userName and i.id = :itemId")
	Optional<CartItem> findItemInCartByUserNameAndItemId(String userName, Long itemId);

	@Modifying
	@Transactional
	@Query("DELETE FROM CartItem ci WHERE ci.cart.userName = :username and ci.item.id = :id")
	void deleteByUsernameAndItemId(String username, Long id);
}
