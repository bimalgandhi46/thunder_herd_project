package com.example.cache.config;

import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import io.lettuce.core.ReadFrom;

@Configuration
@Primary
public class SentinelRedisConnectionConfig {
	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration().master("mymaster")
				.sentinel("10.0.0.2", 26379).sentinel("10.0.0.3", 26379).sentinel("10.0.0.4", 26379);
		LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
				.readFrom(ReadFrom.REPLICA_PREFERRED).commandTimeout(Duration.ofSeconds(3)).build();
		return new LettuceConnectionFactory(sentinelConfig, clientConfig);
	}
}
