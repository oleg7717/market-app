package ru.goncharenko.market.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;

@TestConfiguration
public class TestSecurityConfig {
	@Bean
	@Primary
	public ReactiveClientRegistrationRepository testClientRegistrationRepository() {
		return new InMemoryReactiveClientRegistrationRepository();
	}
}