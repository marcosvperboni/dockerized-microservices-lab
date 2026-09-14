package com.marcosperboni.orderservice.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

	public static final String ORDERS_CACHE = "orders";

	@Bean
	public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
		// GenericJackson2JsonRedisSerializer builds its own internal (classic Jackson 2)
		// ObjectMapper, separate from Boot 4's Jackson 3 bean, so java.time types need
		// their module registered explicitly here or serialization of Instant fails.
		GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer()
				.configure(mapper -> mapper.registerModule(new JavaTimeModule())
						.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));

		RedisCacheConfiguration ordersCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofMinutes(5))
				.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

		return builder -> builder.withCacheConfiguration(ORDERS_CACHE, ordersCacheConfig);
	}
}
