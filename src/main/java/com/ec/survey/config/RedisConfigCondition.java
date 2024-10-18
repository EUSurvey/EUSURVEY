package com.ec.survey.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class RedisConfigCondition implements Condition {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfigCondition.class);

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String  redisHost = loadRedisHost(context);
        return redisHost != null && !redisHost.isEmpty();
    }

    private String loadRedisHost(ConditionContext context) {
        Properties properties = new Properties();

        try {
            InputStream inputStream = context.getResourceLoader().getResource("./WEB-INF/spring.properties").getInputStream();
            properties.load(inputStream);
            return properties.getProperty("spring.redis.host");      
        } catch (IOException e) {
            logger.error("Error loading Redis host from spring.properties", e);
            return null;
        }
    }
}
