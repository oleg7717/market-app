package ru.goncharenko.market.core.config.security;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class SavedRequestFilter implements WebFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		String path = exchange.getRequest().getPath().toString();

		if (path.startsWith("/items")) {
			String query = exchange.getRequest().getURI().getQuery();
			String fullUrl = path + (query != null ? "?" + query : "");

			return exchange.getSession()
					.doOnNext(session -> session.getAttributes().put("SAVED_REQUEST_URL", fullUrl))
					.then(chain.filter(exchange));
		}

		return chain.filter(exchange);
	}
}
