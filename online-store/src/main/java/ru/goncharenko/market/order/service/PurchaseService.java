package ru.goncharenko.market.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.goncharenko.market.core.config.security.utils.SecurityUtils;
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
import ru.goncharenko.market.payment.model.PaymentStatus;

import java.net.ConnectException;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpStatus.PAYMENT_REQUIRED;
import static ru.goncharenko.market.core.ObjectsUtil.requireNonNullResult;

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
	private final SecurityUtils securityUtils;
	private final DefaultApi paymentApi;

	private final String userName = "oleg";

	public Mono<Boolean> isSufficientBalance(String userName, Double orderAmount) {
		return securityUtils.getAuthorize()
				.doOnSubscribe(sub -> log.info("Starting payment service call"))
				.doOnSuccess(token -> log.info("Token obtained successfully"))
				.doOnError(error -> log.error("Failed to obtain token", error))
				.flatMap(client -> {
					securityUtils.setToken(client, paymentApi);
					return paymentApi.apiBalanceGet(userName, orderAmount)
							.doOnSubscribe(sub -> log.debug("API request subscribed"))
							.doOnSuccess(response -> {
								log.info("=== RESPONSE RECEIVED ===");
								if (response != null) {
									log.info("Response: processed={}, code={}, message={}",
											response.getProcessed(),
											response.getCode(),
											response.getMessage());
								} else {
									log.warn("Response is null");
								}
							})
							.doOnError(error -> {
								log.error("Error message: {}", error.getMessage());

								if (error instanceof WebClientResponseException webClientError) {
									log.error("HTTP Status: {}\nResponse body: {}\nRequest URI: {}",
											webClientError.getStatusCode(),
											webClientError.getResponseBodyAsString(),
											requireNonNull(webClientError.getRequest()).getURI());
								} else if (error instanceof ConnectException) {
									log.error("Connection refused - payment-service might not be reachable");
								}
							})
							.map(requireNonNullResult(PaymentStatus::getProcessed))
							.doOnSuccess(processed -> log.info("Mapped result to processed={}", processed))
							.doOnError(error -> log.error("Error mapping response", error))
							.onErrorReturn(false);
				});
	}

	public Flux<OrderDTO> makePayment() {
		Mono<CartDTO> cartDTO = cartService.getItemsInCart();
		return cartDTO.flatMapMany(itemsInCart -> {
			Double orderAmount = itemsInCart.getTotal();
			Payment payment = new Payment();
			payment.setUserName(userName);
			payment.setOrderAmount(orderAmount);
			return securityUtils.getAuthorize().flatMapMany(client -> {
						securityUtils.setToken(client, paymentApi);
						return paymentApi.apiBalancePost(payment).flatMapMany(response -> {
							if (requireNonNull(response.getProcessed())) {
								Order newOrder = new Order();
								newOrder.setUserName(userName);
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
													.then(orderService.findById(orderId));
										});
							} else {
								return Flux.error(new ResponseStatusException(PAYMENT_REQUIRED, "Платёж не осуществлён."));
							}
						});
					}
			);
		});
	}
}
