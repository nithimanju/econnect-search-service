package com.e_connect.search.configs;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

@Configuration
public class RedisConfig {

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory();
    return lettuceConnectionFactory;
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory);
    redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    return redisTemplate;
  }

  @Bean
  public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
    RedisCacheManager redisCacheManager = RedisCacheManager.builder(redisConnectionFactory)
        .withCacheConfiguration("RecentRequestCache",
            RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5))
                .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())))
        .build();
    return redisCacheManager;
  }
}
