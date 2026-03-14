package ru.goncharenko.market.core.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;

import java.net.URI;

@Configuration
@EnableWebFluxSecurity
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
				.formLogin(formLogin -> formLogin
						.authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler("/items"))
				)
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessHandler((exchange, authentication) -> {
							ServerHttpResponse response = exchange.getExchange().getResponse();
							response.setStatusCode(HttpStatus.FOUND);
							response.getHeaders().setLocation(URI.create("/items"));
							return response.setComplete();
						})
				)
				.csrf(ServerHttpSecurity.CsrfSpec::disable)
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
