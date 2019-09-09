package com.ec.survey;

import com.ec.survey.service.ExportService;
import com.ec.survey.service.ParticipationService;
import com.ec.survey.service.WebserviceService;
import com.mysql.jdbc.AbandonedConnectionCleanupThread;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Set;

public class ShutdownListener implements ServletContextListener {
	private static final Logger logger = Logger.getLogger(ShutdownListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("Context initialized.");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {	
		logger.info("Context destroyed.");
		try {
			AbandonedConnectionCleanupThread.shutdown();
		} catch (Exception e) {
		}
		deregisterJdbcDrivers();
	}

	@SuppressWarnings("deprecation")
	private void deregisterJdbcDrivers() {

		try
		{
			 BeanFactory bf = ContextLoader.getCurrentWebApplicationContext();
		        if (bf instanceof ConfigurableApplicationContext) {
		            ((ConfigurableApplicationContext)bf).close();
		        }
		}
		catch (Exception e)
		{
		}		
		
		try
		{
			final Enumeration<Driver> drivers = DriverManager.getDrivers();
			while (drivers.hasMoreElements()) {
				final Driver driver = drivers.nextElement();
				try {
					DriverManager.deregisterDriver(driver);
					logger.info("Deregistered '" + driver + "' JDBC driver.");
				} catch (SQLException e) {
					logger.warn("Failed to deregister '" + driver + "' JDBC driver.");
				}
			}}
		catch (Exception e)
		{
		}

		try
		{
			// Get a reference to the Scheduler and shut it down
			WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
			ThreadPoolTaskScheduler scheduler = (ThreadPoolTaskScheduler) context.getBean("taskScheduler");
			scheduler.shutdown();

			// Sleep for a bit so that we don't get any errors
			logger.info("shutdown task scheduler completed.");
		}
		catch (Exception e)
		{
		}
		
		try
		{
			// Get a reference to the Scheduler and shut it down
			WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
			ExportService exportService = (ExportService) context.getBean("exportService");
			exportService.getPool().shutdown();
			exportService.getPDFPool().shutdown();

			// Sleep for a bit so that we don't get any errors
			logger.info("shutdown task scheduler completed.");
		}
		catch (Exception e)
		{
		}
		
		try
		{
			// Get a reference to the Scheduler and shut it down
			WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
			WebserviceService webserviceService = (WebserviceService) context.getBean("webserviceService");
			webserviceService.getPool().shutdown();
			webserviceService.getTokenPool().shutdown();

			// Sleep for a bit so that we don't get any errors
			logger.info("shutdown task scheduler completed.");
		}
		catch (Exception e)
		{
		}
		
		try
		{
			// Get a reference to the Scheduler and shut it down
			WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
			ParticipationService participationService = (ParticipationService) context.getBean("participationService");
			participationService.getPool().shutdown();

			// Sleep for a bit so that we don't get any errors
			logger.info("shutdown task scheduler completed.");
		}
		catch (Exception e)
		{
		}

		try {
			Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
	        Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
	        for(Thread t:threadArray) {
	            if(t.getName().contains("Abandoned connection cleanup thread")) {
	                synchronized(t) {
	                    t.stop(); //don't complain, it works
	                }
	            }
	        }
        }
        catch (Exception e)
		{
		}
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
	}
}
