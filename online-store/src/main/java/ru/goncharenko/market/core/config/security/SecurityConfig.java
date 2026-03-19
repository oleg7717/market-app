package ru.goncharenko.market.core.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.net.URI;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {
	@Bean
	public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
		return http
				.authorizeExchange(exchanges -> exchanges
						.pathMatchers(HttpMethod.GET, "/items/**", "/images/**").permitAll()
						.pathMatchers("/login", "/logout", "/confirm-logout").permitAll()
						.anyExchange().authenticated()
				)
				.anonymous(anonymous -> anonymous
						.principal("anonymous")
						.authorities("ROLE_GUEST")
				)
				.formLogin(Customizer.withDefaults())
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessHandler((exchange, authentication) ->
								exchange.getExchange().getSession()
										.flatMap(WebSession::invalidate)
										.then(Mono.defer(() -> {
											ServerHttpResponse response = exchange.getExchange().getResponse();
											response.setStatusCode(HttpStatus.FOUND); // 302 для редиректа
											response.getHeaders().setLocation(URI.create("/items"));
											return response.setComplete();
										}))
						)
				)
				.csrf(csrf -> csrf.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
				)
				.build();
	}

	@Bean
	public UserDetailsRepositoryReactiveAuthenticationManager authenticationManager(
			ReactiveUserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		UserDetailsRepositoryReactiveAuthenticationManager authManager =
				new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
		authManager.setPasswordEncoder(passwordEncoder);
		return authManager;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
	}
}
