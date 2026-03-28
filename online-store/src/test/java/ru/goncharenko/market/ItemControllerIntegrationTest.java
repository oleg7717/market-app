package ru.goncharenko.market;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

@AutoConfigureWebTestClient
public class ItemControllerIntegrationTest extends SharedSecureConfig {
	@Autowired
	private WebTestClient webTestClient;
	@Test
	void getNonExistItem() {
		webTestClient.get().uri("/items/11")
				.exchange()
				.expectStatus().isNotFound()
				.expectBody()
				.returnResult();
	}
}
