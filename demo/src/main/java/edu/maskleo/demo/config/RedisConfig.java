package edu.maskleo.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Configuration
public class RedisConfig {

    private static final String SEQ_KEY = "seq_key";

    @Value("${spring.redis.sentinel.nodes}")
    private String redisHosts;
    @Value("${spring.redis.sentinel.master:master}")
    private String master;

    public long getSequence(){
        RedisAtomicLong atomicLong = new RedisAtomicLong(SEQ_KEY, jedisConnectionFactory());
        if (-1 == atomicLong.getExpire()){
            atomicLong.expire(5, TimeUnit.MINUTES);
        }
        return atomicLong.incrementAndGet();
    }

    @Autowired
    private RedisSentinelConfiguration redisSentinelConfiguration;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(){
        return new JedisConnectionFactory(redisSentinelConfiguration);
    }

    @Bean
    public RedisSentinelConfiguration redisSentinelConfiguration(){
        RedisSentinelConfiguration configuration = new RedisSentinelConfiguration();
        String[] host = redisHosts.split(",");
        for(String redisHost : host){
            String[] item = redisHost.split(":");
            String ip = item[0];
            String port = item[1];
            configuration.addSentinel(new RedisNode(ip, Integer.parseInt(port)));
        }
        configuration.setMaster(master);
        return configuration;
    }


    @Bean
    public RedisTemplate<String,Object> redisTemplate(){
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }
}
