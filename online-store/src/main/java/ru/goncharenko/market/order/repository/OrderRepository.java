package ru.goncharenko.market.order.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.goncharenko.market.order.model.Order;

public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {
}
