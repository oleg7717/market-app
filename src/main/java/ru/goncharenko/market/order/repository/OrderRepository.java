package ru.goncharenko.market.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.goncharenko.market.order.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
	String baseOrderQuery = "SELECT o " +
			"FROM Order o " +
			"LEFT JOIN FETCH o.orderItems oi " +
			"LEFT JOIN FETCH oi.item ";
	String whereClause = "WHERE o.id = :id";

	@Query(baseOrderQuery)
	List<Order> findAllOrders();

	@Query(baseOrderQuery + whereClause)
	Optional<Order> findById(long id);
}
