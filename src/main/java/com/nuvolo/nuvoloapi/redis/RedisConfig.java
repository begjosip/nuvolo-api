package com.nuvolo.nuvoloapi.redis;

import com.nuvolo.nuvoloapi.redis.data.ShoppingSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /* Redis data will handle key values as ((String)sessionId, ShoppingSession ) */
    @Bean
    public RedisTemplate<String, ShoppingSession> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ShoppingSession> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
