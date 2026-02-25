package ru.goncharenko.market;

import org.junit.jupiter.api.Test;

public class ItemControllerIntegrationTest extends IntegrationTest {
	@Test
	void getNonExistItem() {
		webTestClient.get().uri("/items/11")
				.exchange()
				.expectStatus().isNotFound()
				.expectBody()
				.returnResult();
	}
}
