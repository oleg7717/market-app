package ru.goncharenko.market;

import org.junit.jupiter.api.Test;
import ru.goncharenko.market.core.types.SortEnum;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ItemControllerIntegrationTest extends IntegrationTest {
	@Test
	void getItemsOnFirstPage() throws Exception {
		mockMvc.perform(get("/items")
						.param("search", "ни")
						.param("pageNumber", "1")
						.param("sort", SortEnum.NO.name())
						.param("pageSize", "5"))
				.andExpect(status().isOk());
	}

	@Test
	void getNonExistItem() throws Exception {
		mockMvc.perform(get("/items/11"))
				.andExpect(status().isNotFound());
	}
}
