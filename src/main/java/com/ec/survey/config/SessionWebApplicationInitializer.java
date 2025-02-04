package com.ec.survey.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.filter.DelegatingFilterProxy;

public class SessionWebApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        String redisHost = null;
        String redisPort = null;

        // Load properties from WEB-INF/spring.properties because Spring has not loaded properties yet
        Properties properties = new Properties();
        try (InputStream input = servletContext.getResourceAsStream("/WEB-INF/spring.properties")) {
            if (input == null) {
                servletContext.log("Sorry, unable to find spring.properties in WEB-INF");
            } else {
                properties.load(input);
                redisHost = properties.getProperty("spring.redis.host", "");
                redisPort = properties.getProperty("spring.redis.port", "");
            }
        } catch (IOException ex) {
            servletContext.log("Error loading properties from spring.properties", ex);
        }

        boolean isRedisAvailable = redisHost != null && !redisHost.isEmpty();

        if (isRedisAvailable) {
            servletContext.log("Using Redis for HTTP Sessions. Redis server: " + redisHost + ":" + redisPort);
            FilterRegistration.Dynamic sessionFilter = servletContext.addFilter(
                "springSessionRepositoryFilter", new DelegatingFilterProxy("springSessionRepositoryFilter")
            );
            sessionFilter.setInitParameter("targetBeanName", "springSessionRepositoryFilter");
            sessionFilter.setAsyncSupported(true);
            sessionFilter.addMappingForUrlPatterns(null, false, "/*");
            sessionFilter.setInitParameter("order", "1");
        } else {
//            servletContext.log("Redis host not configured. Using Tomcat HTTP Sessions and JavaMelody.");
//            FilterRegistration.Dynamic monitoringFilter = servletContext.addFilter(
//                "monitoring", new net.bull.javamelody.MonitoringFilter()
//            );
//            monitoringFilter.setInitParameter("log", "true");
//            monitoringFilter.setAsyncSupported(true);
//            monitoringFilter.addMappingForUrlPatterns(null, false, "/*");
        }
    }
}
