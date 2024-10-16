package com.ec.survey.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.ec.survey.model.KeyValue;

import edu.emory.mathcs.backport.java.util.Collections;

@Service("ecService")
public class ECService extends BasicService {
	
	private List<String> linesDGS = null;
	private List<String> linesAEX = null;
	
	private void parse() {
		InputStream inputStream = servletContext
				.getResourceAsStream("/WEB-INF/Content/EC/dgs.txt");
				
		linesDGS = new ArrayList<>();
		
		try {
			String text = IOUtils.toString(inputStream, "UTF-8");
			
			Scanner scanner = new Scanner(text);
			while (scanner.hasNextLine()) {
			  String line = scanner.nextLine();
			  linesDGS.add(line);			 
			}
			scanner.close();		
			
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		
		inputStream = servletContext
				.getResourceAsStream("/WEB-INF/Content/EC/aex.txt");
				
		linesAEX = new ArrayList<>();
		
		try {
			String text = IOUtils.toString(inputStream, "UTF-8");
			
			Scanner scanner = new Scanner(text);
			while (scanner.hasNextLine()) {
			  String line = scanner.nextLine();
			  linesAEX.add(line);			 
			}
			scanner.close();		
			
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage(), e);
		}	
	}
	
	public List<KeyValue> GetEntities(String term, Boolean isDGs) {
		
		if (linesDGS == null) {
			parse();
		}
		
		List<String> lines = isDGs ? linesDGS : linesAEX;
		
		List<KeyValue> dgs = new ArrayList<>();
		
		boolean topEntities = "dgs".equals(term) || "aex".equals(term);
		String prefix = term + ".";
		
		KeyValue last = null;
		
		for (String line : lines) {
		  
		  if (topEntities && !line.contains(".")) {
			  last = new KeyValue(line, "0");
			  dgs.add(last);
		  }
		  
		  if (last != null && line.contains(last.getKey() + ".")) {
			  //we are a child -> flag parent as having children
			  last.setValue("0");
		  }
		  
		  if (!topEntities && line.startsWith(prefix)) {
			  if (StringUtils.countMatches(line, ".") == StringUtils.countMatches(prefix, ".")) {					 
				  last = new KeyValue(line, "1");
				  dgs.add(last);
			  } else {
				  // there might be a child without a direct parent					  
				  String parent = line.substring(0, line.lastIndexOf("."));
				  
				  if (!lines.contains(parent)) {
					  last = new KeyValue(line, "1");
					  dgs.add(last);
				  }  
			  }
		  }
		}	
		
		return dgs;
	}

	public String[] getDepartments(boolean dgs, boolean aex) {
		if (linesDGS == null) {
			parse();
		}
		
		List<String> result = new ArrayList<>();
		if (dgs) Collections.addAll(result, linesDGS.toArray());
		if (aex) Collections.addAll(result, linesAEX.toArray());
		
		return result.toArray(new String[0]);
	}
	
	public String[] getDepartments() {
		return getDepartments(true, true);
	}
}
