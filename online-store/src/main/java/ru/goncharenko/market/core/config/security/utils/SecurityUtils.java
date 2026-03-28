package ru.goncharenko.market.core.config.security.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.goncharenko.market.payment.client.DefaultApi;

import static java.util.Objects.requireNonNull;
import static ru.goncharenko.market.core.ObjectsUtil.requireNonNullResult;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityUtils {
	private final ReactiveOAuth2AuthorizedClientManager manager;

	public Mono<String> getCurrentUsername() {
		return ReactiveSecurityContextHolder.getContext()
				.map(context -> requireNonNull(context.getAuthentication()).getName())
				.doOnNext(username -> log.info("Current user: {}", username))
				.switchIfEmpty(Mono.just("anonymous"))
				.onErrorResume(e -> {
					log.error("Error getting security context", e);
					return Mono.just("anonymous");
				});
	}

	public Mono<Authentication> getCurrentAuthentication() {
		return ReactiveSecurityContextHolder.getContext()
				.map(requireNonNullResult(SecurityContext::getAuthentication))
				.doOnNext(auth -> log.info("Authentication name: {}", auth.getName()))
				.switchIfEmpty(Mono.error(new RuntimeException("No authentication found")));
	}

	public Mono<OAuth2AuthorizedClient> getAuthorize() {
		return manager.authorize(OAuth2AuthorizeRequest
				.withClientRegistrationId("market")
				.principal("system") // У client_credentials нет имени пользователя, поэтому используется system
				.build()
		);
	}

	public void setToken(OAuth2AuthorizedClient client, DefaultApi clientApi) {
		String token = client.getAccessToken().getTokenValue();
		log.info("Token value: {}", token);
		clientApi.getApiClient().setBearerToken(token);
	}
}
