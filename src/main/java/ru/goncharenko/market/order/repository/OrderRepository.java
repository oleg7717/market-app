package ru.goncharenko.market.order.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.goncharenko.market.item.model.Item;
import ru.goncharenko.market.order.model.Order;
import ru.goncharenko.market.order.model.OrderItem;

import java.util.List;

public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {
//	String baseOrderQuery = "SELECT o " +
//			"FROM Order o " +
//			"LEFT JOIN FETCH o.orderItems oi " +
//			"LEFT JOIN FETCH oi.item ";
//	String whereClause = "WHERE o.id = :id";

//	@Query(baseOrderQuery)
//	Flux<Order> findAll();

//	@Query(baseOrderQuery + whereClause)
	Mono<Order> findById(long id);

	@Query("SELECT o.* FROM orders o")
	Flux<Order> findAllOrders();

	@Query("SELECT oi.* FROM order_item oi WHERE oi.order_id = :orderId")
	Flux<OrderItem> findOrderItemsByOrderId(Long orderId);

	@Query("SELECT i.* FROM items i WHERE i.id IN (:itemIds)")
	Flux<Item> findItemsByIds(List<Item> itemIds);
}
