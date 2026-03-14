package ru.goncharenko.market.core.config.security.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static java.util.Objects.requireNonNull;
import static ru.goncharenko.market.core.ObjectsUtil.requireNonNullResult;

@Slf4j
@Component
public class SecurityUtils {
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
				.doOnNext(auth -> log.info("Authentication: {}", auth))
				.switchIfEmpty(Mono.error(new RuntimeException("No authentication found")));
	}
}
