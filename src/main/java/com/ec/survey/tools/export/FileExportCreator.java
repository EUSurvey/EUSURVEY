package com.ec.survey.tools.export;

import com.ec.survey.model.FileFilter;
import com.ec.survey.model.FileResult;
import com.ec.survey.model.administration.User;
import com.ec.survey.service.FileService;
import com.ec.survey.service.MailService;
import com.ec.survey.tools.Constants;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("fileExportCreator")
@Scope("prototype")
public class FileExportCreator implements Runnable {

	protected static final Logger logger = Logger.getLogger(FileExportCreator.class);

	@Resource(name = "fileService")
	private FileService fileService;

	@Resource(name = "mailService")
	private MailService mailService;

	public @Autowired ServletContext servletContext;

	private @Value("${sender}") String sender;
	private @Value("${smtpserver}") String smtpServer;
	private @Value("${smtp.port}") String smtpPort;
	private @Value("${server.prefix}") String host;
	private @Value("${export.fileDir}") String fileDir;
	protected @Value("${filesystem.archive}") String archiveDir;

	private FileFilter filter;
	private String[] files;
	private User user;

	public void init(FileFilter filter, String[] files, User user) {
		this.filter = filter;
		this.files = files;
		this.user = user;
	}

	private String getFileName(java.io.File source, FileResult fileresult) {
		String name = source.getName();
		if (fileresult != null && fileresult.getFileName() != null && fileresult.getFileName().length() > 0
				&& !name.startsWith(Constants.ANSWER) && !name.startsWith("survey") && !name.startsWith("export")) {
			String name2 = fileresult.getFileName();
			if (!name.equals(name2)) {
				name += "_" + name2;
			}
		}
		return name;
	}

	@Transactional
	public void run() {
		java.io.File file;
		try {
			String tempuid = UUID.randomUUID().toString();

			java.io.File folder = fileService.getUsersFolder(user.getId());
			file = new java.io.File(String.format("%s/files%s.%s", folder.getPath(), tempuid, "zip"));

			final OutputStream out = new FileOutputStream(file);
			final ArchiveOutputStream os = new ArchiveStreamFactory().createArchiveOutputStream("zip", out);

			if (files == null) {
				List<FileResult> fileresults = fileService.getFiles2(filter);
				for (FileResult fileresult : fileresults) {
					java.io.File source = new java.io.File(fileresult.getFilePath());
					if (source.exists() && !source.equals(file)) {
						os.putArchiveEntry(new ZipArchiveEntry(getFileName(source, fileresult)));
						IOUtils.copy(new FileInputStream(source), os);
						os.closeArchiveEntry();
					}
				}
			} else {
				for (String path : files) {
					java.io.File source = new java.io.File(path);
					if (source.exists()) {
						FileResult fileresult = fileService.getFileResult(source.toPath(), null,
								new java.io.File(archiveDir).toPath());

						os.putArchiveEntry(new ZipArchiveEntry(getFileName(source, fileresult)));
						IOUtils.copy(new FileInputStream(source), os);
						os.closeArchiveEntry();
					}
				}
			}

			os.close();
			out.close();

			String path = host + "administration/files/download/" + user.getId() + "/" + tempuid;
			String body = "The file download has been created. You can download it here:<br /><a href='" + path + "'>"
					+ path + "</a>";

			InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
			String text = org.apache.commons.io.IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", body)
					.replace("[HOST]", host);

			mailService.SendHtmlMail(user.getEmail(), sender, sender, "Files download generated", text, null);

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

}
