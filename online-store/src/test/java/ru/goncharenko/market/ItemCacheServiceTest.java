package ru.goncharenko.market;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.goncharenko.market.config.EmbeddedRedisConfiguration;
import ru.goncharenko.market.core.exception.ResourceNotFoundException;
import ru.goncharenko.market.item.dto.CartContext;
import ru.goncharenko.market.item.model.Cart;
import ru.goncharenko.market.item.model.CartItem;
import ru.goncharenko.market.item.model.Item;
import ru.goncharenko.market.item.repository.ItemRepository;
import ru.goncharenko.market.item.service.ItemCacheService;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(EmbeddedRedisConfiguration.class)
class ItemCacheServiceTest {
	@Autowired
	private ItemCacheService cacheService;

	@Autowired
	private ReactiveRedisTemplate<String, Item> redisTemplate;

	@MockitoBean
	private ItemRepository itemRepository;

	private static final String TEST_USER = "oleg";
	private static final String CART_CACHE_PREFIX = "cart:";
	private Item testItem;
	private Item testItem2;
	private Item testItem3;
	private Cart testCart;
	private CartItem testCartItem;

	@BeforeEach
	void setUp() {
		testItem = new Item(1L, "Мяч футбольный Adidas", "Официальный мяч для профессиональных матчей, размер 5", "/images/soccer_ball.jpg", 4600.0);
		testItem2 = new Item(2L, "Наушники беспроводные Sony", "Беспроводные наушники с шумоподавлением, время работы до 30 часов", "/images/sony_headphones.jpg", 12500.0);
		testItem3 = new Item(3L, "Книга \"Мастер и Маргарита\"", "Роман Михаила Булгакова в твердом переплете", "/images/master_margarita.jpg", 900.0);
		testCart = new Cart(1L, TEST_USER);
		testCartItem = new CartItem(1L, 1L, 1L, 2);

		redisTemplate.getConnectionFactory()
				.getReactiveConnection()
				.serverCommands()
				.flushAll()
				.block();
	}

	@Test
	void findCachedItemById_FirstCall_LoadFromDbAndCache() {
		when(itemRepository.findById(1L)).thenReturn(Mono.just(testItem));

		StepVerifier.create(cacheService.findCachedItemById(1L))
				.expectNext(testItem)
				.verifyComplete();

		// Проверяем, что был один вызов метода findById
		verify(itemRepository, times(1)).findById(1L);

		StepVerifier.create(cacheService.findCachedItemById(1L))
				.expectNextMatches(item ->
						item.getId().equals(testItem.getId()) &&
								item.getTitle().equals(testItem.getTitle()) &&
								item.getDescription().equals(testItem.getDescription()) &&
								item.getPrice().equals(testItem.getPrice())
				)
				.verifyComplete();

		// Проверяем, что больше не было вызова метода findById
		verify(itemRepository, times(1)).findById(1L);
	}

	@Test
	void findCachedItemById_ItemNotFound_ThrowsException() {
		when(itemRepository.findById(999L)).thenReturn(Mono.empty());

		StepVerifier.create(cacheService.findCachedItemById(999L))
				.expectError(ResourceNotFoundException.class)
				.verify();
	}

	@Test
	void getItemsPage_FirstCall_LoadFromDbAndCache() {
		String search = "мяч";
		Pageable pageable = PageRequest.of(0, 5, Sort.by("title"));
		List<Item> items = List.of(testItem);

		when(itemRepository.findByDescriptionOrTitleContainingIgnoreCase(search, pageable))
				.thenReturn(Flux.fromIterable(items));
		when(itemRepository.countByDescriptionOrTitleContainingIgnoreCase(search))
				.thenReturn(Mono.just(1L));

		expectNextMatches(search, pageable, items.size());

		verify(itemRepository, times(1))
				.findByDescriptionOrTitleContainingIgnoreCase(search, pageable);

		expectNextMatches(search, pageable, items.size());

		verify(itemRepository, times(1))
				.findByDescriptionOrTitleContainingIgnoreCase(search, pageable);
	}

	private void expectNextMatches(String search, Pageable pageable, int itemSize) {
		StepVerifier.create(cacheService.getItemsPage(search, pageable))
				.expectNextMatches(itemContext -> {
							Item item = itemContext.getItems().getFirst();
							return item.getId().equals(testItem.getId()) &&
									item.getTitle().equals(testItem.getTitle()) &&
									item.getDescription().equals(testItem.getDescription()) &&
									item.getPrice().equals(testItem.getPrice()) &&
									itemContext.getCount() == itemSize;
						}
				)
				.verifyComplete();
	}

	@Test
	void loadItemsFromCache_WhenCacheHit_ReturnsItems() {
		String cacheKey = CART_CACHE_PREFIX + TEST_USER;

		redisTemplate.opsForHash()
				.put(cacheKey, "1", testItem)
				.block();

		CartContext context = new CartContext(testCart, List.of(testCartItem));

		StepVerifier.create(cacheService.loadItemsFromCache(context))
				.expectNextMatches(map ->
						map.size() == 1 &&
								map.containsKey(1L) &&
								map.get(1L).getTitle().equals("Мяч футбольный Adidas")
				)
				.verifyComplete();
	}

	@Test
	void loadItemsFromCache_WhenCacheMiss_ReturnsEmptyMap() {
		CartContext context = new CartContext(testCart, List.of(testCartItem));

		StepVerifier.create(cacheService.loadItemsFromCache(context))
				.expectNext(Map.of())
				.verifyComplete();
	}

	@Test
	void loadItemsFromDbAndCache_LoadsFromDbAndStoresInCache() {
		String cacheKey = CART_CACHE_PREFIX + TEST_USER;
		List<CartItem> cartItems = List.of(testCartItem);
		List<Long> itemIds = List.of(1L);

		when(itemRepository.findAllByIdIn(itemIds))
				.thenReturn(Flux.just(testItem));

		CartContext context = new CartContext(testCart, cartItems);

		StepVerifier.create(cacheService.loadItemsFromDbAndCache(context))
				.expectNextMatches(map ->
						map.size() == 1 &&
								map.containsKey(1L)
				)
				.verifyComplete();

		verify(itemRepository, times(1)).findAllByIdIn(itemIds);

		redisTemplate.opsForHash()
				.entries(cacheKey)
				.collectList()
				.as(StepVerifier::create)
				.expectNextCount(1)
				.verifyComplete();
	}

	@Test
	void addItemInCartToCache_WhenCacheExists_AddsItem() {
		String cacheKey = CART_CACHE_PREFIX + TEST_USER;

		redisTemplate.opsForHash()
				.put(cacheKey, "1", testItem)
				.block();

		when(itemRepository.findById(3L)).thenReturn(Mono.just(testItem3));

		StepVerifier.create(cacheService.addItemInCartToCache(3L, TEST_USER))
				.verifyComplete();

		verify(itemRepository, times(1)).findById(3L);

		redisTemplate.opsForHash()
				.entries(cacheKey)
				.collectMap(
						entry -> Long.valueOf(entry.getKey().toString()),
						entry -> (Item) entry.getValue()
				)
				.as(StepVerifier::create)
				.expectNextMatches(map ->
						map.size() == 2 &&
								map.containsKey(3L) &&
								map.get(3L).getTitle().equals("Книга \"Мастер и Маргарита\"")
				)
				.verifyComplete();
	}

	@Test
	void addItemInCartToCache_WhenCacheDoesNotExist_SkipsAdding() {
		String cacheKey = CART_CACHE_PREFIX + TEST_USER;

		StepVerifier.create(cacheService.addItemInCartToCache(3L, TEST_USER))
				.verifyComplete();

		verify(itemRepository, never()).findById(anyLong());

		redisTemplate.hasKey(cacheKey)
				.as(StepVerifier::create)
				.expectNext(false)
				.verifyComplete();
	}

	@Test
	void removeItemInCartFromCache_WhenItemExists_RemovesItem() {
		String cacheKey = CART_CACHE_PREFIX + TEST_USER;

		redisTemplate.opsForHash()
				.putAll(cacheKey, Map.of(
						"1", testItem,
						"2", testItem2
				))
				.block();

		StepVerifier.create(cacheService.removeItemInCartFromCache(1L, TEST_USER))
				.verifyComplete();

		redisTemplate.opsForHash()
				.entries(cacheKey)
				.collectMap(
						entry -> Long.valueOf(entry.getKey().toString()),
						entry -> (Item) entry.getValue()
				)
				.as(StepVerifier::create)
				.expectNextMatches(map ->
						map.size() == 1 &&
								!map.containsKey(1L) &&
								map.containsKey(2L)
				)
				.verifyComplete();
	}
}
