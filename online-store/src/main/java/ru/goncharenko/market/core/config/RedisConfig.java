package ru.goncharenko.market.core.config;

import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class RedisConfig {
	@Bean
	public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
		RedisSerializationContext<String, Object> serializationContext =
				RedisSerializationContext
						.<String, Object>newSerializationContext(new StringRedisSerializer())
						.key(new StringRedisSerializer())
						.value(new Jackson2JsonRedisSerializer<>(Object.class))
						.hashKey(new StringRedisSerializer())
						.hashValue(new Jackson2JsonRedisSerializer<>(Object.class))
						.build();

		return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
	}

	@Bean
	public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
		return builder -> builder
				.withCacheConfiguration("item",
						RedisCacheConfiguration.defaultCacheConfig()
								.entryTtl(Duration.of(5, ChronoUnit.MINUTES))
								.serializeValuesWith(
										RedisSerializationContext.SerializationPair.fromSerializer(
												new GenericJackson2JsonRedisSerializer()
										)
								));
	}
}
