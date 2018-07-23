package edu.maskleo.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisConfig {

    private static final String SEQ_KEY = "seq_key";

    public long getSequence(){
        RedisAtomicLong atomicLong = new RedisAtomicLong(SEQ_KEY, jedisConnectionFactory());
        if (-1 == atomicLong.getExpire()){
            atomicLong.expire(5, TimeUnit.SECONDS);
        }
        return atomicLong.incrementAndGet();
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(){
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<String,Object> redisTemplate(){
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }
}
