package ru.goncharenko.market;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.goncharenko.market.config.TestSecurityConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestSecurityConfig.class})
public abstract class SharedSecureConfig {
	@MockitoBean
	private ReactiveClientRegistrationRepository clientRegistrationRepository;
}
