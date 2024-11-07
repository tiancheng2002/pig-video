package com.zhu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

//Redis配置类
@Configuration
public class RedisConfiguration {

    //配置Redis的字符串序列化，默认的为jdk本身的序列化，存储时会乱码
    @Bean("myRedisTemplate")
    @SuppressWarnings("all")
    public RedisTemplate redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        //key序列化
        template.setKeySerializer(new StringRedisSerializer());
        //value序列化
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        //hash的key序列化
        template.setHashKeySerializer(new StringRedisSerializer());
        //hash的value序列化
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.setConnectionFactory(factory);
        return template;
    }

}
