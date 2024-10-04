package com.ec.survey;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.filter.DelegatingFilterProxy;


public class SessionWebApplicationInitializer implements WebApplicationInitializer {


   
    private String redisHost;
    private String redisPort;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // Load properties from WEB-INF/spring.properties because Spring has not loaded properties yet
        loadPropertiesFromWebInf(servletContext);

        if (isRedisAvailable()) {
            System.out.println("Using Redis for HTTP Sessions.  Redis host is:"+redisHost); // Logger not available yet
            FilterRegistration.Dynamic sessionFilter = servletContext.addFilter(
                "springSessionRepositoryFilter", new DelegatingFilterProxy("springSessionRepositoryFilter")
            );
            sessionFilter.setInitParameter("targetBeanName", "springSessionRepositoryFilter");
            sessionFilter.setAsyncSupported(true);
            sessionFilter.addMappingForUrlPatterns(null, false, "/*");
            sessionFilter.setInitParameter("order", "1");
        }

        if (!isRedisAvailable()) {
            System.out.println("Redis host not configured.  Using Tomcat HTTP Sessions");
            FilterRegistration.Dynamic monitoringFilter = servletContext.addFilter(
                "monitoring", new net.bull.javamelody.MonitoringFilter()
            );
            monitoringFilter.setInitParameter("log", "true");
            monitoringFilter.setAsyncSupported(true);
            monitoringFilter.addMappingForUrlPatterns(null, false, "/*");
        }
    }

    private void loadPropertiesFromWebInf(ServletContext servletContext) {
        Properties properties = new Properties();
        try (InputStream input = servletContext.getResourceAsStream("/WEB-INF/spring.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find spring.properties in WEB-INF");
                return;
            }
            properties.load(input);

            redisHost = properties.getProperty("spring.redis.host", "");
            redisPort = properties.getProperty("spring.redis.port", "");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private boolean isRedisAvailable() {
        return redisHost != null && !redisHost.isEmpty();
    }
}
