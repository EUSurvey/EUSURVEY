package com.ec.survey.service;

import com.ec.survey.model.FtpEndPoint;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.io.File;
import java.io.IOException;

@Service("ftpClient")
public class FtpClient {

	protected static final Logger logger = Logger.getLogger(FtpClient.class);
	private final ProducerTemplate producerTemplate;
	private final ConsumerTemplate consumerTemplate;
	private @Value("${mt.fileDir}") String fileDir;
	private @Value("${mt.ftp.source.url}") String ftpSourceURL;

	@Autowired
	FtpClient(ProducerTemplate producerTemplate, ConsumerTemplate consumerTemplate) {
		this.producerTemplate = producerTemplate;
		this.consumerTemplate = consumerTemplate;
	}

	public String getFile(FtpEndPoint ftpEndPoint) {
		
		return this.consumerTemplate.receiveBody(ftpEndPoint.toCamelConsumerString(), String.class);
	}

	public String getFtpSourceURL() {
		return ftpSourceURL;
	}

	@PostConstruct
	public void init() {
		File folder = new File(fileDir);
		if (!folder.exists())
			folder.mkdirs();
	}

	public void putFile(FtpEndPoint ftpEndPoint, String fileName, String fileContent) {

		String header = Exchange.FILE_NAME;
		Object body = null;
		try {
			File temp = new File(fileDir + fileName);
			if (!temp.createNewFile()) {
				throw new IllegalStateException("Can not create file :" + fileDir + fileName);
			}

			FileUtils.writeStringToFile(temp, fileContent);
			body = temp;
		} catch (IOException e) {

		}

		if (body != null) {
			try {
				this.producerTemplate.sendBodyAndHeader(ftpEndPoint.toCamelString(), body, header, fileName);	
			} catch (Exception e) {
				logger.error("Error on FtpClient Put file " + e);
				throw e;
			}
			
		}
	}

}
