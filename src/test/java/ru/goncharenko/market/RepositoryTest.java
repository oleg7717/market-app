package ru.goncharenko.market;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import reactor.test.StepVerifier;
import ru.goncharenko.market.core.types.SortEnum;
import ru.goncharenko.market.item.model.Item;
import ru.goncharenko.market.item.repository.ItemRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RepositoryTest extends IntegrationTest {
	@Autowired
	private ItemRepository itemRepository;

	@Test
	void findByPageSize_shouldReturnPageItems() {
		Pageable pageable = PageRequest.of(0, 5, Sort.by(SortEnum.PRICE.getFieldName()));

		StepVerifier.create(itemRepository.findAllBy(pageable)
						.collectList()
						.zipWith(itemRepository.count())
				)
				.assertNext(tuple -> {
					List<Item> items = tuple.getT1();
					Long total = tuple.getT2();

					assertNotNull(items);
					assertEquals(5, items.size());
					assertTrue(total > 0);

					if (!items.isEmpty()) {
						Item first = items.getFirst();
						assertNotNull(first.getId());
						assertNotNull(first.getTitle());
					}
				})
				.verifyComplete();
	}

	@Test
	void findBySearch_shouldReturnFilteredItems() {
		String searchTerm = "книга";
		Pageable pageable = PageRequest.of(0, 10);

		StepVerifier.create(
						itemRepository.findByDescriptionContainingIgnoreCaseOrTitleContainingIgnoreCase(
										searchTerm, searchTerm, pageable)
								.collectList()
								.zipWith(itemRepository.countByDescriptionContainingIgnoreCaseOrTitleContainingIgnoreCase(
										searchTerm, searchTerm))
				)
				.assertNext(tuple -> {
					List<Item> items = tuple.getT1();
					Long total = tuple.getT2();

					assertNotNull(items);
					assertEquals(total, items.size());

					// Проверяем, что все найденные товары содержат поисковый термин
					items.forEach(item -> {
						boolean matches = (item.getTitle() != null &&
								item.getTitle().toLowerCase().contains(searchTerm)) ||
								(item.getDescription() != null &&
										item.getDescription().toLowerCase().contains(searchTerm));
						assertTrue(matches, "Item should contain search term: " + searchTerm);
					});
				})
				.verifyComplete();
	}

	@Test
	void findById_shouldReturnItem() {
		Long existingId = 1L; // Предполагаем, что такой ID существует в тестовой БД

		StepVerifier.create(itemRepository.findById(existingId))
				.assertNext(item -> {
					assertNotNull(item);
					assertEquals(existingId, item.getId());
					assertNotNull(item.getTitle());
					assertNotNull(item.getPrice());
				})
				.verifyComplete();
	}

	@Test
	void findById_shouldReturnEmptyForNonExistentItem() {
		Long nonExistentId = 11L;

		StepVerifier.create(itemRepository.findById(nonExistentId))
				.expectNextCount(0)
				.verifyComplete();
	}

	@Test
	void count_shouldReturnTotalNumberOfItems() {
		StepVerifier.create(itemRepository.count())
				.assertNext(count -> {
					assertNotNull(count);
					assertTrue(count > 0);
					System.out.println("Total items in database: " + count);
				})
				.verifyComplete();
	}

	@Test
	void findAllBy_withSorting_shouldReturnSortedItems() {
		// Сортировка по цене по возрастанию
		Pageable priceAscPageable = PageRequest.of(0, 10, Sort.by("price").ascending());

		StepVerifier.create(itemRepository.findAllBy(priceAscPageable).collectList())
				.assertNext(items -> {
					assertFalse(items.isEmpty());
					// Проверяем, что цены идут по возрастанию
					for (int i = 0; i < items.size() - 1; i++) {
						assertTrue(items.get(i).getPrice() <= items.get(i + 1).getPrice());
					}
				})
				.verifyComplete();

		// Сортировка по названию по убыванию
		Pageable titleDescPageable = PageRequest.of(0, 10, Sort.by("title").descending());

		StepVerifier.create(itemRepository.findAllBy(titleDescPageable).collectList())
				.assertNext(items -> {
					assertFalse(items.isEmpty());
					// Проверяем, что названия идут по убыванию
					for (int i = 0; i < items.size() - 1; i++) {
						assertTrue(items.get(i).getTitle()
								.compareTo(items.get(i + 1).getTitle()) >= 0);
					}
				})
				.verifyComplete();
	}
}