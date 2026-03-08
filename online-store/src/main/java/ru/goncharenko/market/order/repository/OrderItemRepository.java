package ru.goncharenko.market.order.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.goncharenko.market.order.model.OrderItem;

public interface OrderItemRepository extends ReactiveCrudRepository<OrderItem, Long> {
}
