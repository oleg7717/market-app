package ru.goncharenko.market.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.goncharenko.market.core.config.security.utils.SecurityUtils;
import ru.goncharenko.market.order.dto.OrderDTO;
import ru.goncharenko.market.order.mapper.OrderMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
	private final DatabaseClient databaseClient;
	private final OrderMapper mapper;
	private final SecurityUtils securityUtils;

	private static final String findAllOrders = """
			SELECT
			    o.id as order_id, o.total_sum, o.status, oi.id as order_item_id, oi.count,
			    i.id as item_id, i.title, i.description, i.img_path, i.price
			FROM orders o
			LEFT JOIN order_item oi ON o.id = oi.order_id
			LEFT JOIN items i ON oi.item_id = i.id WHERE o.user_name = :user_name
			""";

	public Flux<OrderDTO> getAllOrders() {
		return securityUtils.getCurrentUsername()
				.flatMapMany(userName -> databaseClient.sql(findAllOrders)
						.bind("user_name", userName)
						.fetch()
						.all()
						.bufferUntilChanged(row -> row.get("order_id"))
						.map(mapper::mapToOrderDto)
				);
	}

	public Mono<OrderDTO> findById(long id) {
		return securityUtils.getCurrentUsername()
				.flatMap(userName -> databaseClient.sql(findAllOrders + " and o.id = :id")
						.bind("user_name", userName)
						.bind("id", id)
						.fetch()
						.all()
						.bufferUntilChanged(row -> row.get("order_id"))
						.map(mapper::mapToOrderDto).next()
				)
				.switchIfEmpty(Mono.empty());
	}
}
