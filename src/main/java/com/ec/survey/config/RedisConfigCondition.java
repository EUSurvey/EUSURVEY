package com.ec.survey.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class RedisConfigCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String  redisHost = loadPropertiesFromWebInf(context);
        return redisHost != null && !redisHost.isEmpty();
    }

    private String loadPropertiesFromWebInf(ConditionContext context) {
        Properties properties = new Properties();

        try {
            System.out.println(context.getResourceLoader().getResource("./WEB-INF/spring.properties").getFile().getAbsolutePath());
            InputStream inputStream = context.getResourceLoader().getResource("./WEB-INF/spring.properties").getInputStream();
            
            if (inputStream != null) {
                properties.load(inputStream);
                return properties.getProperty("spring.redis.host");
            } else {
                System.out.println("spring.properties file not found in /WEB-INF");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
