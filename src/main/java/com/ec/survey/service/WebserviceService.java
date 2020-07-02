package com.ec.survey.service;

import com.ec.survey.exception.MessageException;
import com.ec.survey.model.ParticipationGroup;
import com.ec.survey.model.ServiceRequest;
import com.ec.survey.model.WebserviceTask;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.ExportsRemover;
import com.ec.survey.tools.ResultsCreator;
import com.ec.survey.tools.TokenCreator;
import com.ec.survey.tools.Tools;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service("webserviceService")
@Configurable
public class WebserviceService extends BasicService {
		
	@Transactional
	public WebserviceTask get(int id)
	{
		Session session = sessionFactory.getCurrentSession();
		return (WebserviceTask) session.get(WebserviceTask.class, id);
	}
	
	@Transactional
	public void setError(int task, String error) throws InterruptedException {
		boolean saved = false;
		
		int counter = 1;
		
		while(!saved)
		{
			try {
				internalSetError(task, error);
				saved = true;
			} catch (org.hibernate.exception.LockAcquisitionException ex)
			{
				logger.info("lock on WebserviceTask table catched; retry counter: " + counter);
				counter++;
								
				if (counter > 60)
				{
					logger.error(ex.getLocalizedMessage(), ex);
					throw ex;
				}
				
				Thread.sleep(1000);
			}
		}		
	}
	
	public void internalSetError(int task, String error) {
		Session session;
		session = sessionFactory.getCurrentSession();
		
		if (error.length() > 250) error = error.substring(0, 250);
		
		SQLQuery query = session.createSQLQuery("UPDATE WEBSERVICETASK t SET t.WST_DONE = true, t.WST_ERROR = :error WHERE t.WST_ID = :id");
		query.setString("error", error);
		query.setInteger("id", task);
			
		query.executeUpdate();
	}
	
	@Transactional
	public Date setStarted(int taskid) throws InterruptedException
	{	
		int counter = 1;
		
		while(true)
		{
			try {
				return internalSetStarted(taskid);
			} catch (org.hibernate.exception.LockAcquisitionException ex)
			{
				logger.info("lock on WebserviceTask table catched; retry counter: " + counter);
				counter++;
								
				if (counter > 60)
				{
					logger.error(ex.getLocalizedMessage(), ex);
					throw ex;
				}
				
				Thread.sleep(1000);
			}
		}
	}
	
	private Date internalSetStarted(int taskid) {
		Session session;
		session = sessionFactory.getCurrentSession();
		
		Date started = new Date();
		
		SQLQuery query = session.createSQLQuery("UPDATE WEBSERVICETASK t SET t.WST_STARTED = :now WHERE t.WST_ID = :id");
		query.setTimestamp("now", started);
		query.setInteger("id", taskid);
		
		query.executeUpdate();
		session.flush();
		
		return started;
	}	
	
	@Transactional
	public void setDone(WebserviceTask task) throws InterruptedException
	{
		boolean saved = false;
		
		int counter = 1;
		
		while(!saved)
		{
			try {
				internalSetDone(task);
				saved = true;
			} catch (org.hibernate.exception.LockAcquisitionException ex)
			{
				logger.info("lock on WebserviceTask table catched; retry counter: " + counter);
				counter++;
								
				if (counter > 60)
				{
					logger.error(ex.getLocalizedMessage(), ex);
					throw ex;
				}
				
				Thread.sleep(1000);
			}
		}		
	}

	private void internalSetDone(WebserviceTask task) {
		Session session;
		session = sessionFactory.getCurrentSession();
		
		SQLQuery query = session.createSQLQuery("UPDATE WEBSERVICETASK t SET t.WST_DONE = true, t.WST_RESULT = :result WHERE t.WST_ID = :id");
		query.setString("result", task.getResult());
		query.setInteger("id", task.getId());
		
		query.executeUpdate();			
	}
	
	@Transactional
	public void save(WebserviceTask task) throws InterruptedException
	{
		boolean saved = false;
		
		int counter = 1;
		
		while(!saved)
		{
			try {
				internalSave(task);
				saved = true;
			} catch (org.hibernate.exception.LockAcquisitionException ex)
			{
				logger.info("lock on WebserviceTask table catched; retry counter: " + counter);
				counter++;
								
				if (counter > 60)
				{
					logger.error(ex.getLocalizedMessage(), ex);
					throw ex;
				}
				
				Thread.sleep(1000);
			}
		}		
	}
	
	private void internalSave(WebserviceTask task) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(task);
		session.flush();
	}
	
	public boolean startTask(WebserviceTask task, Locale locale) {		
		try {
			
			if (useworkerserver.equalsIgnoreCase("true") && isworkerserver.equalsIgnoreCase("false"))
			{
				logger.info("calling worker server for webservice task " + task.getId());
				
				try {				
					URL workerurl = new URL(workerserverurl + "worker/startwebservice/" + task.getId());
					URLConnection wc = workerurl.openConnection();
					BufferedReader in = new BufferedReader(new InputStreamReader(wc.getInputStream()));
					String inputLine;
					StringBuilder result = new StringBuilder();
					while ((inputLine = in.readLine()) != null) 
						result.append(inputLine);
					in.close();
					
					if (result.toString().equals("OK"))
					{
						return true;
					} else {
						logger.error("calling worker server for webservice task " + task.getId() + " returned" + result);
						return false;
					}
				
				} catch (ConnectException e) {
					logger.error(e.getLocalizedMessage(), e);
				}
			}
			
			logger.info("Starting task " + task.getId());
			
			switch (task.getType()) {
				case CreateTokens: 
					TokenCreator tokenCreator = (TokenCreator) context.getBean("tokenCreator");
					tokenCreator.init(task.getId());
					getTokenPool().execute(tokenCreator);	
					break;
				case CreateResults:
					ResultsCreator resultsCreator = (ResultsCreator) context.getBean("resultsCreator");
					resultsCreator.init(task.getId(), resources, locale);
					getPool().execute(resultsCreator);	
					break;
				case CreateResult:
					ResultsCreator resultsCreator2 = (ResultsCreator) context.getBean("resultsCreator");
					resultsCreator2.init(task.getId(), resources, locale);
					getPool().execute(resultsCreator2);	
					break;
				case DeleteOldExports:
					ExportsRemover exportsRemover = (ExportsRemover) context.getBean("exportsRemover");
					exportsRemover.init(task.getId());
					getPool().execute(exportsRemover);	
					break;
				default:
					throw new MessageException("Task type not supported");
			}
				
			logger.info(String.format("Task %s started successfully", task.getId()));
			return true;
		} catch (Exception ex) {
			logger.error(ex);
			logger.error(String.format("Task %s could not be started.", task.getId()));
			return false;
		}
	}
	
	@Transactional
	public boolean restartTask(WebserviceTask task) {	
		logger.info("Starting task " + task.getId());
		
		try {
			
			if (useworkerserver.equalsIgnoreCase("true") && isworkerserver.equalsIgnoreCase("false"))
			{
				logger.info("calling worker server for restarting webservice task " + task.getId());
				
				try {
					URL workerurl = new URL(workerserverurl + "worker/restartwebservice/" + task.getId());
					URLConnection wc = workerurl.openConnection();
					BufferedReader in = new BufferedReader(new InputStreamReader(wc.getInputStream()));
					String inputLine;
					StringBuilder result = new StringBuilder();
					while ((inputLine = in.readLine()) != null) 
						result.append(inputLine);
					in.close();
					
					if (result.toString().equals("OK"))
					{
						return true;
					} else {
						logger.error("calling worker server for restarting webservice task " + task.getId() + " returned" + result);
						return false;
					}
				} catch (ConnectException e) {
					logger.error(e.getLocalizedMessage(), e);
				}
			}
			
			task.setCounter(task.getCounter()+1);			
		
			Session session = sessionFactory.getCurrentSession();
			session.saveOrUpdate(task);
			
			switch (task.getType()) {
				case CreateTokens: 
					TokenCreator tokenCreator = (TokenCreator) context.getBean("tokenCreator");
					tokenCreator.init(task.getId());
					getTokenPool().execute(tokenCreator);	
					break;
				case CreateResults:
					ResultsCreator resultsCreator = (ResultsCreator) context.getBean("resultsCreator");
					resultsCreator.init(task.getId(), resources, null);
					getPool().execute(resultsCreator);	
					break;
				case CreateResult:
					ResultsCreator resultsCreator2 = (ResultsCreator) context.getBean("resultsCreator");
					resultsCreator2.init(task.getId(), resources, null);
					getPool().execute(resultsCreator2);
					break;
				default:
					throw new MessageException("Task type not supported");
			}
			
			logger.info(String.format("Task %s started successfully", task.getId()));
			return true;
		} catch (Exception ex) {
			logger.error(ex);
			logger.error(String.format("Task %s could not be started.", task.getId()));
			return false;
		}
	}
	
	@Transactional
	@SuppressWarnings("unchecked")
	public List<WebserviceTask> getTasksToRestart() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM WebserviceTask WHERE done = 0");
		return query.list();
	}

	@Transactional
	public ServiceRequest getServiceRequest(Integer userId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM ServiceRequest r WHERE r.userId = :userId").setInteger("userId", userId);	
		
		return (ServiceRequest) query.uniqueResult();
	}

	@Transactional
	public void increaseServiceRequest(Integer userId) {
				
		String disablewebservicelimit = settingsService.get("disablewebservicelimit");
		if (disablewebservicelimit != null && disablewebservicelimit.equalsIgnoreCase("true")) return;
		
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM ServiceRequest r WHERE r.userId = :userId").setInteger("userId", userId);	
		
		ServiceRequest req = (ServiceRequest) query.uniqueResult();
		
		if (req == null)
		{
			req = new ServiceRequest();
			req.setUserId(userId);
			req.setDate(new Date());
			req.setCounter(0);
		} else if (!Tools.isToday(req.getDate()))
		{
			req.setDate(new Date());
			req.setCounter(0);
		}
		
		req.setCounter(req.getCounter()+1);
		session.saveOrUpdate(req);
	}

	@Transactional
	public int getWaitingTokens(ParticipationGroup group) {
		Session session = sessionFactory.getCurrentSession();
		String sql = "SELECT SUM(WST_NUM) FROM WEBSERVICETASK WHERE WST_GROUP = :id AND WST_DONE = 0 AND type = 0";
		Query query = session.createSQLQuery(sql).setInteger("id", group.getId());
		return ConversionTools.getValue(query.uniqueResult());
	}
	
}
