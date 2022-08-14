package com.ec.survey.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.net.URL;
import java.net.URLClassLoader;

public final class TestHibernateConfigurator {
	
	private static SessionFactory sessionFactory; 
	
	public static void InitHibernate()
	{
		
		ClassLoader cl=ClassLoader.getSystemClassLoader();
		URL[] urls = ((URLClassLoader)cl).getURLs();

		for (URL url : urls) {
			System.out.println("CLASSPATH " + url.getFile());
		}
		
		
		String HibConfig ="hibernate.cfg.MySql.LOCAL" + System.getProperty("user.name").toUpperCase()  +  ".xml";
		//String HibConfig ="/WEB-INF/hibernate.cfg.MySql.LOCAL.xml";
		System.out.println("My Hibernate config " + HibConfig);

		Configuration config = new Configuration();
		
		config.configure(HibConfig);		
		ServiceRegistry sr= new StandardServiceRegistryBuilder().applySettings(
				config.getProperties()).build();
				
		sessionFactory = config.buildSessionFactory(sr);		
	}
	
	public static Session getSession()
	{
		try {
			return sessionFactory.openSession();	
		} catch (Exception e) {
			return null;
		}		
	}
	
	public static void close(){
		sessionFactory.close();
	}

}
