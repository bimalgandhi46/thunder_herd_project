package com.example.cache.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@EnableCaching
public class RedisConfig {
	@Bean
	public RedisCacheConfiguration cacheConfiguration()
	ObjectMapper mapper = new ObjectMapper(); 
	mapper.registerModule(new JavaTimeModule()); 
	mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	{
		return RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith( RedisSerializationContext.SerializationPair.fromSerializer(serializer) );
	}
}
