package ru.goncharenko.market.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.goncharenko.market.order.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
//	@Query("SELECT o FROM Order o JOIN FETCH o.orderItems oi JOIN FETCH oi.item WHERE o.id = :id")
//	Order findOrderWithItems(@Param("id") Long id);
}
