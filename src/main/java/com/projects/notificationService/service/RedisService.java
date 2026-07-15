package com.projects.notificationService.service;

import com.projects.notificationService.constants.RedisKeys;
import com.projects.notificationService.dto.NotificationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    ObjectMapper objectMapper;

    public <T> T get(String key, Class<T> entity){
        try {
            Object obj = redisTemplate.opsForValue().get(key);
            if(obj == null) return null;
            return objectMapper.readValue(obj.toString(), entity);
        }
        catch (Exception e){
            System.out.println("Exception: " + e);
            return null;
        }
    }

    public void set(String key, Object entity, Long ttl){
        try {
            String json = objectMapper.writeValueAsString(entity);
            redisTemplate.opsForValue().set(key, json, ttl, TimeUnit.SECONDS);
        }
        catch (Exception e){
            System.out.println("Exception: " + e);
        }
    }

    public Boolean isRateLimit(NotificationEvent event, int maxAllowed){
        String rateLimitKey = RedisKeys.RATELIMIT + event.getUserId() + ":" + event.getType();
        // rateLimitKey = "rateLimit:101:PAYMENT_FAILED"

        Long count = redisTemplate.opsForValue().increment(rateLimitKey);

        if(count == 1){
            redisTemplate.expire(rateLimitKey, Duration.ofMinutes(1));
        }

        return count <= maxAllowed;
    }

    public boolean isUniqueEvent(NotificationEvent event, Duration lockDuration) {
        String redisKey = "notif:" + event.getType() + ":" + event.getEventId();

        try {
            // Execute atomic check-and-set
            Boolean result = redisTemplate.opsForValue()
                    .setIfAbsent(redisKey, "processed", lockDuration);

            // Convert Object Boolean to primitive boolean safely
            return Boolean.TRUE.equals(result);

        } catch (Exception e) {
            // Fallback: If Redis crashes, log error and allow the event to process
            // (Fail-open strategy so your business logic doesn't stop working)
            System.out.println("Redis connection failed during duplicate check for key: " + redisKey);
            return true;
        }
    }

    public void delete(String key){
        redisTemplate.delete(key);
    }
}

