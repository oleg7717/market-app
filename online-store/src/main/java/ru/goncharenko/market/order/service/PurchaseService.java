package ru.goncharenko.market.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.goncharenko.market.item.dto.CartDTO;
import ru.goncharenko.market.item.repository.CartRepository;
import ru.goncharenko.market.item.service.CartService;
import ru.goncharenko.market.order.dto.OrderDTO;
import ru.goncharenko.market.order.enums.OrderStatus;
import ru.goncharenko.market.order.model.Order;
import ru.goncharenko.market.order.model.OrderItem;
import ru.goncharenko.market.order.repository.OrderItemRepository;
import ru.goncharenko.market.order.repository.OrderRepository;
import ru.goncharenko.market.payment.client.DefaultApi;
import ru.goncharenko.market.payment.model.Payment;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseService {
	private final OrderService orderService;
	private final CartService cartService;
	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final CartRepository cartRepository;
	private final TransactionalOperator transactionalOperator;
	private final String userName = "oleg";

	public Mono<Boolean> isSufficientBalance(String userName, Double orderAmount) {
		return new DefaultApi().apiBalanceGet(userName)
				.map(response -> Objects.requireNonNull(response.getBalance()) > orderAmount);
	}
	// ToDo проверка по статусу 200 факт оплаты
	public Flux<OrderDTO> makePayment(ServerWebExchange exchange) {
		Mono<CartDTO> cartDTO = cartService.getItemsInCart(exchange);
		return cartDTO.flatMapMany(itemsInCart -> {
			Double orderAmount = itemsInCart.getTotal();
			Payment payment = new Payment();
			payment.setUserName(userName);
			payment.setOrderAmount(orderAmount);
			return new DefaultApi().apiBalancePost(payment).flatMapMany(response -> {
				if (response.equals("Payment completed.")) {
					Order newOrder = new Order();
					newOrder.setTotalSum(orderAmount);
					newOrder.setStatus(OrderStatus.ORDERED);
					return orderRepository.save(newOrder)
							.flatMapMany(order -> {
								Long orderId = order.getId();
								List<OrderItem> orderedItems = itemsInCart.getItems().stream()
										.map(item -> {
											OrderItem orderItem = new OrderItem();
											orderItem.setItemId(item.id());
											orderItem.setOrderId(orderId);
											orderItem.setCount(item.count());
											return orderItem;
										})
										.toList();

								return orderItemRepository.saveAll(orderedItems)
										.as(transactionalOperator::transactional)
										.thenMany(cartRepository.deleteByUserName(userName))
										.thenMany(orderService.findById(orderId));
							});
				} else {
					return Flux.error(new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, "Платёж не осуществлён."));
				}
			});
		});
	}
}
