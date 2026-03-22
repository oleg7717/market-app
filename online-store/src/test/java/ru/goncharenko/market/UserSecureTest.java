package ru.goncharenko.market;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.reactive.server.WebTestClient;

@AutoConfigureWebTestClient
public class UserSecureTest extends SharedSecureConfig {
	@Autowired
	private WebTestClient webTestClient;

	@Test
	public void shouldAccessToPublicEndpoint() {
		webTestClient.get()
				.uri("/items")
				.exchange()
				.expectStatus().isOk();
	}

	@Test
	public void shouldDenyAccessToSecureEndpoint() {
		webTestClient.get()
				.uri("/cart/items")
				.headers(headers -> headers.setBasicAuth("user", "wrongPassword"))
				.exchange()
				.expectStatus().isUnauthorized();
	}

	@Test
	@WithAnonymousUser
	public void shouldDenyAccessToSecureEndpointForAnonym() {
		webTestClient.get()
				.uri("/orders")
				.exchange()
				.expectStatus().isUnauthorized();
	}

	@Test
	public void shouldAccessToSecureEndpointForValidUser() {
		webTestClient.get()
				.uri("/orders")
				.headers(headers -> headers.setBasicAuth("Hugh.Jackman", "Lipton"))
				.exchange()
				.expectStatus().isOk();
	}

	@Test
	public void notFoundPageForValidUserWithNoAccessToOrder() {
		webTestClient.get()
				.uri("/orders/4")
				.headers(headers -> headers.setBasicAuth("Hugh.Jackman", "Lipton"))
				.exchange()
				.expectStatus().isNotFound();
	}
}
