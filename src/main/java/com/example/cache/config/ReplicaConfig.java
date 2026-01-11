package com.example.cache.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import io.lettuce.core.ReadFrom;

@Configuration
public class ReplicaConfig {
    @Bean(name = "replicaRedisTemplate")
    public RedisTemplate<String, Object> replicaRedisTemplate(RedisConnectionFactory sentinelConnectionFactory) {

        // Force Lettuce to read from replicas
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .readFrom(ReadFrom.REPLICA_PREFERRED)
                .build();

        // Reuse the sentinel configuration from Spring Boot
        LettuceConnectionFactory replicaFactory =
                new LettuceConnectionFactory(
                        ((LettuceConnectionFactory) sentinelConnectionFactory).getSentinelConfiguration(),
                        clientConfig
                );

        replicaFactory.afterPropertiesSet();

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(replicaFactory);

        // Serialization
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }

}
