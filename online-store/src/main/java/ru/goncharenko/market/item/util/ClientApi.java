package ru.goncharenko.market.item.util;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.goncharenko.market.payment.client.DefaultApi;

@Component
public class ClientApi {
	@Bean
	public DefaultApi defaultApi() {
		return new DefaultApi();
	}
}
