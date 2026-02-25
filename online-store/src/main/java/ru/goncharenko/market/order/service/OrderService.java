package ru.goncharenko.market.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.goncharenko.market.order.dto.OrderDTO;
import ru.goncharenko.market.order.mapper.OrderMapper;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final DatabaseClient databaseClient;
	private final OrderMapper mapper;

	private static final String findAllOrders = """
            SELECT
                o.id as order_id, o.total_sum, o.status, oi.id as order_item_id, oi.count,
                i.id as item_id, i.title, i.description, i.img_path, i.price
            FROM orders o
            LEFT JOIN order_item oi ON o.id = oi.order_id
            LEFT JOIN items i ON oi.item_id = i.id
            """;

	public Flux<OrderDTO> getAllOrders() {
		return databaseClient.sql(findAllOrders)
				.fetch()
				.all()
				.bufferUntilChanged(row -> row.get("order_id"))
				.map(mapper::mapToOrderDto);
	}

	public Flux<OrderDTO> findById(long id) {
		return databaseClient.sql(findAllOrders + String.format(" where o.id = %d", id))
				.fetch()
				.all()
				.bufferUntilChanged(row -> row.get("order_id"))
				.map(mapper::mapToOrderDto);
	}
}
