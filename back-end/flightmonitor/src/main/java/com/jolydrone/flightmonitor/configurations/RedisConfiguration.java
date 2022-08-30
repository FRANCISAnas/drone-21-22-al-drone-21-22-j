package com.jolydrone.flightmonitor.configurations;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import static com.jolydrone.flightmonitor.configurations.LockRedisConfiguration.getJedisConnectionFactory;

@Configuration
public class RedisConfiguration {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.database}")
    private int database;

    @Value("${spring.redis.password}")
    private String password;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return getJedisConnectionFactory(host, port, database, password);
    }

    @Qualifier("dataTemplate")
    @Bean("dataTemplate")
    public RedisTemplate<String, Object> dataTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        //template.setDefaultSerializer(new StringRedisSerializer());
        return template;
    }

    @Qualifier("redisTemplate")
    @Bean("redisTemplate")
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        //template.setDefaultSerializer(new StringRedisSerializer());
        return template;
    }
}
