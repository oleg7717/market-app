package ru.goncharenko.market;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.goncharenko.market.core.types.SortEnum;
import ru.goncharenko.market.item.model.Item;
import ru.goncharenko.market.item.repository.ItemRepository;
import ru.goncharenko.market.order.model.Order;
import ru.goncharenko.market.order.repository.OrderRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RepositoryTest extends IntegrationTest {
	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Test
	void findByPageSize_shouldReturnPageItems() {
		Pageable pageable = PageRequest.of(0, 5, Sort.by(SortEnum.PRICE.getFieldName()));
		Page<Item> pageData = itemRepository.findAll(pageable);

		assertNotNull(pageData);
		assertEquals(5, pageData.getContent().size());
		Optional<Item> first = pageData.stream().findFirst();
		assertTrue(first.isPresent());
		first.ifPresent(item -> {
			assertEquals(3L, item.getId());
			assertEquals("Книга \"Мастер и Маргарита\"", item.getTitle());
		});
	}

	@Test
	void findAll_shouldReturnAllItems() {
		List<Order> orders = orderRepository.findAllOrders();

		assertEquals(5, orders.size());
		Optional<Order> orderFour = orders.stream().filter(u -> u.getId() == 4L).findFirst();
		assertTrue(orderFour.isPresent());
		assertEquals(4, orderFour.orElse(null).getOrderItems().size());
	}
}
