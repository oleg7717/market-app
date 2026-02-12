package ru.goncharenko.market;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.goncharenko.market.item.controller.ItemController;
import ru.goncharenko.market.item.mapper.ItemMapper;
import ru.goncharenko.market.item.model.Cart;
import ru.goncharenko.market.item.model.Item;
import ru.goncharenko.market.item.repository.ItemRepository;
import ru.goncharenko.market.item.service.ItemService;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = ItemRepository.class,
		includeFilters = @ComponentScan.Filter(
				type = FilterType.ASSIGNABLE_TYPE,
				classes = {ItemController.class, ItemService.class, ItemMapper.class}
		))
@AutoConfigureMockMvc
public class ItemControllerTest {
	private static AutoCloseable closeable;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ItemRepository repository;

	@BeforeEach
	void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
	}

	@Test
	public void searchStringParse() throws Exception {
		List<Item> userList = Collections.singletonList(new Item(1L, "Мяч футбольный Adidas", "", "",1L, new Cart()));
		when(repository.findByDescriptionOrTitleContainingIgnoreCase(anyString(), any(Pageable.class)))
				.thenReturn(new PageImpl<>(userList, PageRequest.of(0, 10), userList.size()));

		// Проверяем ответ контроллера на поиск по подстроке и тегу
		mockMvc.perform(get("/items")
						.param("search", "ни")
						.param("pageNumber", "1")
						.param("pageSize", "2"))
				.andExpect(status().isOk());

		// Проверяем вызов метода поиска товаров
		verify(repository, times(1))
				.findByDescriptionOrTitleContainingIgnoreCase(anyString(), any(Pageable.class));
	}

	@AfterAll
	static void closeUp() throws Exception {
		closeable.close();
	}
}
