package com.ec.survey.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.ec.survey.tools.ConversionTools;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

@Service
public class BasicService implements BeanFactoryAware {

	@Resource(name="sessionFactory")
	protected SessionFactory sessionFactory;
	
	@Resource(name = "answerService")
	protected AnswerService answerService;
	
	@Resource(name = "participationService")
	protected ParticipationService participationService;
	
	@Resource(name = "surveyService")
	protected SurveyService surveyService;
	
	@Resource(name = "exportService")
	protected ExportService exportService;	
	
	@Resource(name = "fileService")
	protected FileService fileService;
	
	@Resource(name = "skinService")
	protected SkinService skinService;
	
	@Resource(name = "sessionService")
	protected SessionService sessionService;
	
	@Resource(name = "translationService")
	protected TranslationService translationService;
	
	@Resource(name = "activityService")
	protected ActivityService activityService;
		
	@Resource(name = "administrationService")
	protected AdministrationService administrationService;
	
	@Resource(name="mailService")
	protected MailService mailService;
	
	@Resource(name="systemService")
	protected SystemService systemService;
	
	@Resource(name="settingsService")
	protected SettingsService settingsService;
	
	@Resource(name="archiveService")
	protected ArchiveService archiveService;
		
	@Resource(name = "reportingServiceProxy")
	protected ReportingServiceProxy reportingService;
	
	@Autowired
	protected MessageSource resources;
	
	protected @Value("${export.tempFileDir}") String tempFileDir;
	protected @Value("${export.fileDir}") String fileDir;
	protected @Value("${archive.fileDir}") String archiveFileDir;
	
	protected @Value("${filesystem.surveys}") String surveysDir;
	protected @Value("${filesystem.users}") String usersDir;
	protected @Value("${filesystem.archive}") String archiveDir;
	
	private @Value("${export.poolSize}") String poolSize;
	protected @Value("${export.timeout}") String exporttimeout;
	private @Value("${mail.mailPoolSize}") String mailPoolSize;
	protected @Value("${pdfserver.prefix}") String pdfhost;
	protected @Value("${server.prefix}") String serverPrefix;	
	protected @Value("${isworkerserver}") String isworkerserver;
	protected @Value("${useworkerserver}") String useworkerserver;
	protected @Value("${workerserverurl}") String workerserverurl;
	protected @Value("${host.executing.task:@null}") String hostExecutingTask;	
	protected @Value("${contextpath}") String contextpath;	
	protected @Value("${oss}") String oss;
	protected @Autowired ServletContext servletContext;
	
	protected @Value("${enablereportingdatabase}") String enablereportingdatabase;
	
	private ExecutorService pool;
	private ExecutorService pdfpool;
	private ExecutorService answerpool;	
	private ExecutorService mailPool;
	private ExecutorService tokenPool;
	
	protected static final Logger logger = Logger.getLogger(BasicService.class);
	
	protected BeanFactory context;
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		context = beanFactory;		
	}
	
	protected boolean isReportingDatabaseEnabled() {
		return enablereportingdatabase != null && enablereportingdatabase.equalsIgnoreCase("true");
	}

	/**
	 * Returns true if this server SHALL execute worker tasks
	 */
	public boolean isHost2ExecuteWorkerTask() {
		if (useworkerserver.equalsIgnoreCase("true") && isworkerserver.equalsIgnoreCase("true")) {
			return true;
		}
		if (useworkerserver.equalsIgnoreCase("false") && isworkerserver.equalsIgnoreCase("false")) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true if this server SHALL execute scheduled tasks
	 */
	protected boolean isHost2ExecuteScheduledTask() {
		if (useworkerserver.equalsIgnoreCase("true") && isworkerserver.equalsIgnoreCase("true")) {
			return false;
		}

		// if nothing specified then assume that it's OK
		if (StringUtils.isEmpty(hostExecutingTask)) {
			logger.debug("The property host.executing.task= is empty and scheduler will be executed");
			return true;
		}

		Enumeration<NetworkInterface> ipAddresses = null;
		try {
			ipAddresses = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e1) {
			logger.error(e1);
			e1.printStackTrace();
			return true;
		}

		for (NetworkInterface netint : Collections.list(ipAddresses)) {
			Enumeration<InetAddress> inetAddrs = netint.getInetAddresses();
			for (InetAddress inetAddr : Collections.list(inetAddrs)) {
				if (StringUtils.contains(inetAddr.getHostName().toLowerCase(), hostExecutingTask.toLowerCase())) {
					return true;
				}

			}
		}

		if (StringUtils.isEmpty(hostExecutingTask)) {
			logger.warn(
					"Unable to determine if should be execute the Task on this host, no server name set in hostExecutingTask property");
		} else {
			logger.debug("no server name found with this value: " + hostExecutingTask);
		}

		return false;
	}
	
	private LinkedBlockingQueue< Runnable> taskQueue = new LinkedBlockingQueue<>();
	
	protected List< Runnable> running = Collections.synchronizedList(new ArrayList<Runnable>());
	
	public Executor getAnswerPool() {
		if (answerpool == null) {
			answerpool = new ThreadPoolExecutor(10, 10,
		            0L, TimeUnit.MILLISECONDS,
		            taskQueue,
		            Executors.defaultThreadFactory()) 
		  {
				
		        @Override
		        protected void beforeExecute(Thread t, Runnable r) {
		            super.beforeExecute(t, r);
		            running.add(r);
		        }

		        @Override
		        protected void afterExecute(Runnable r, Throwable t) {
		            super.afterExecute(r, t);
		            running.remove(r);
		        }
			};
		}
		return answerpool;
	}
	
	public ExecutorService getPool() {
		if (pool == null) {
			pool = Executors.newFixedThreadPool(ConversionTools.getInt(poolSize), new MyThreadFactory());
		}
		return pool;
	}
	
	public ExecutorService getPDFPool() {
		if (pdfpool == null) {
			pdfpool = Executors.newFixedThreadPool(ConversionTools.getInt(poolSize), new MyThreadFactory());
		}
		return pdfpool;
	}
	
	public ExecutorService getMailPool() {
		if (mailPool == null) {
			mailPool = Executors.newFixedThreadPool(ConversionTools.getInt(mailPoolSize), new MyThreadFactory());
		}
		return mailPool;
	}
	
	public ExecutorService getTokenPool() {
		if (tokenPool == null) {
			tokenPool = Executors.newFixedThreadPool(1, new MyThreadFactory());
		}
		
		return tokenPool;
	}
	
	public class MyThreadFactory implements ThreadFactory {
		   public Thread newThread(Runnable r) {
		     Thread t =  new Thread(r);
		     t.setPriority(Thread.MIN_PRIORITY);
		     return t;
		   }
		 }	

	public String getFileDir() {
		return fileDir;
	}	

	public String getFileUIDFromUrl(String url)
	{
		return url.substring(url.lastIndexOf("/")+1);
	}
	
	public boolean isOss(){
		return (!StringUtils.isEmpty(oss) && oss.equalsIgnoreCase("true")); 
	}

}
