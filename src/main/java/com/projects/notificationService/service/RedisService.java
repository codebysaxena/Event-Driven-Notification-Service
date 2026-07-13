package com.projects.notificationService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

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

    public void delete(String key){
        redisTemplate.delete(key);
    }
}

