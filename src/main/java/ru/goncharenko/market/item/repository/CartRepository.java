package ru.goncharenko.market.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.goncharenko.market.item.model.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
