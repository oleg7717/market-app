package ru.goncharenko.market.item.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.goncharenko.market.payment.client.DefaultApi;

@Slf4j
@Component
public class ClientApi {
	@Value("${application.webClient.baseUrl:http://localhost:8081}")
	private String baseUrl;

	@Bean
	public DefaultApi paymentApi() {
		DefaultApi defaultApi = new DefaultApi();
		log.info("Set base url for payment service: {}", baseUrl);
		defaultApi.getApiClient().setBasePath(baseUrl);
		return defaultApi;
	}
}
