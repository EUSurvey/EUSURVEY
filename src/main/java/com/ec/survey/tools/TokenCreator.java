package com.ec.survey.tools;

import com.ec.survey.model.ParticipationGroup;
import com.ec.survey.model.WebserviceTask;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.service.AttendeeService;
import com.ec.survey.service.FileService;
import com.ec.survey.service.ParticipationService;
import com.ec.survey.service.WebserviceService;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service("tokenCreator")
@Scope("prototype")
public class TokenCreator implements Runnable {

	protected static final Logger logger = Logger.getLogger(TokenCreator.class);

	@Resource(name = "participationService")
	protected ParticipationService participationService;
	
	@Resource(name = "fileService")
	protected FileService fileService;
	
	@Resource(name = "attendeeService")
	protected AttendeeService attendeeService;
	
	@Resource(name = "webserviceService")
	protected WebserviceService webserviceService;
	
	private int task;
	
	public int getTask() {
		return task;
	}
	public void setTask(int task) {
		this.task = task;
	}

	public void init(int task) {
		this.task = task;
	}
	
	@Override
	public void run() {
		try {
			
			webserviceService.setStarted(task);
			WebserviceTask webserviceTask = webserviceService.get(task);
			
			ParticipationGroup group = participationService.get(webserviceTask.getGroupId());
			
			String uid = UUID.randomUUID().toString();
			String filename = "tokens" + task + ".xml";
            
            java.io.File target = fileService.getSurveyExportFile(group.getSurveyUid(), uid);
            
            FileWriter outFile = new FileWriter(target);
            PrintWriter out = new PrintWriter(outFile);
           
            File f = new File();
	        f.setUid(uid);
	        f.setName(filename);
	        
	        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	        out.print("<tokenList id=\"" + group.getId().toString() + "\">");	        
	        
	        List<String> tokens = new ArrayList<>();
	        for (int i = 0; i < webserviceTask.getNumber(); i++) {
	        	String token = UUID.randomUUID().toString();
	        	tokens.add(token);
	        	out.println("<ual code=\"" + token + "\" />");
	        }
	        
	        out.print("</tokenList>");
	        
	        out.close();
	       
			attendeeService.addTokens(tokens, group.getId());        
	       
			fileService.add(f);
			webserviceTask.setResult(uid);
			webserviceTask.setDone(true);
			webserviceService.save(webserviceTask);
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			try {
				webserviceService.setError(task, e.getLocalizedMessage() != null ?  e.getLocalizedMessage() : e.toString());
			} catch (InterruptedException e1) {
				logger.error(e1.getLocalizedMessage(), e1);
			}
		}
	}
	
}
