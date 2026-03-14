package ru.goncharenko.market.core.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
	@Bean
	public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
		return http
				.authorizeExchange(exchanges -> exchanges
						.pathMatchers(HttpMethod.GET, "/items/**", "/images/**").permitAll()
						.pathMatchers("/login").permitAll()
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
