package com.nuvolo.nuvoloapi.redis;

import com.nuvolo.nuvoloapi.redis.data.ShoppingSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final RedisTemplate<String, ShoppingSession> redisTemplate;

    public void save(String sessionId, ShoppingSession shoppingSession) {
        log.info("Saving shopping session with ID:{}", sessionId);
        redisTemplate.opsForValue().set(sessionId, shoppingSession);
        log.info("Shopping session with ID:{} saved successfully.", sessionId);
    }

    public ShoppingSession get(String sessionId) {
        log.info("Getting shopping session from redis database for session ID:{}", sessionId);
        return redisTemplate.opsForValue().get(sessionId);
    }

    public Boolean delete(String sessionId) {
        log.info("Deleting shopping session with ID:{}", sessionId);
        return redisTemplate.delete(sessionId);
    }

}
