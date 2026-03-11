package ru.goncharenko.market.payment.client;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ClientApi {
	@Bean
	public DefaultApi defaultApi() {
		return new DefaultApi();
	}
}
