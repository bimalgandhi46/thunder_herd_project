package com.example.cache.config;

import java.time.Duration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.concurrent.ThreadLocalRandom;

@Configuration
@EnableCaching
public class RedisConfig {
	@Bean

	public RedisCacheConfiguration cacheConfiguration() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule()); 
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); 
		GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(mapper); // Base TTL 
		Duration baseTtl = Duration.ofHours(6);
		int jitterSeconds = ThreadLocalRandom.current().nextInt(0, 300); 
		Duration ttlWithJitter = baseTtl.plusSeconds(jitterSeconds); 
		return RedisCacheConfiguration.defaultCacheConfig().entryTtl(ttlWithJitter).serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
}

@Bean
public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
	RedisTemplate<String, Object> template = new RedisTemplate<>();
	template.setConnectionFactory(connectionFactory);
	return template;
}
}