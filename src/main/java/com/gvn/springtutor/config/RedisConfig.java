package com.gvn.springtutor.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
@SuppressWarnings({ "deprecation", "removal" })
public class RedisConfig extends CachingConfigurerSupport {

    // @Bean
    // public RedisTemplate<String, Object> redisTemplate(
    // RedisConnectionFactory connectionFactory,
    // ObjectMapper redisObjectMapper) {
    // RedisTemplate<String, Object> template = new RedisTemplate<>();
    // template.setConnectionFactory(connectionFactory);

    // // Gunakan GenericJackson2JsonRedisSerializer yang mendukung Jackson 2
    // // Suppress warning deprecation karena di version baru class ini deprecated
    // // namun kita butuh support default typing yang ada di class ini.
    // GenericJackson2JsonRedisSerializer serializer = new
    // GenericJackson2JsonRedisSerializer(redisObjectMapper);

    // template.setKeySerializer(new StringRedisSerializer());
    // template.setValueSerializer(serializer);
    // template.setHashKeySerializer(new StringRedisSerializer());
    // template.setHashValueSerializer(serializer);

    // template.afterPropertiesSet();
    // return template;
    // }

    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper;
    }

    // @Bean
    // public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory
    // redisConnectionFactory) {
    // RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    // redisTemplate.setConnectionFactory(redisConnectionFactory);
    // redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
    // return redisTemplate;
    // }

    @Bean
    public RedisCacheManager cacheManager(
            RedisConnectionFactory factory,
            ObjectMapper redisObjectMapper) {

        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        serializer.setObjectMapper(redisObjectMapper);

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(serializer))
                .disableCachingNullValues();

        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }
}