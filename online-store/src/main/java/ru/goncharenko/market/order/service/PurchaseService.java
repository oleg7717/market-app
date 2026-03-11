package ru.goncharenko.market.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.server.ResponseStatusException;
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
import ru.goncharenko.market.payment.client.ClientApi;
import ru.goncharenko.market.payment.model.Payment;
import ru.goncharenko.market.payment.model.PaymentStatus;

import java.util.List;

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
	private final ClientApi clientApi;

	private final String userName = "oleg";

	public Mono<Boolean> isSufficientBalance(String userName, Double orderAmount) {
		return clientApi.defaultApi().apiBalanceGet(userName, orderAmount)
				.map(PaymentStatus::getProcessed);
	}

	public Flux<OrderDTO> makePayment() {
		Mono<CartDTO> cartDTO = cartService.getItemsInCart();
		return cartDTO.flatMapMany(itemsInCart -> {
			Double orderAmount = itemsInCart.getTotal();
			Payment payment = new Payment();
			payment.setUserName(userName);
			payment.setOrderAmount(orderAmount);
			return clientApi.defaultApi().apiBalancePost(payment).flatMapMany(response -> {
				if (response.getProcessed()) {
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
