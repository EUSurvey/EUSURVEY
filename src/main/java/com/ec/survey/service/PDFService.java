package com.ec.survey.service;

import com.ec.survey.exception.MessageException;
import com.ec.survey.model.AnswerSet;
import com.ec.survey.model.Draft;
import com.ec.survey.model.ResultFilter;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.tools.AnswerExecutor;
import com.ec.survey.tools.Constants;
import com.ec.survey.tools.PDFRenderer;
import com.ec.survey.tools.PDFRendererPoolFactory;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

@Service("pdfService")
@Configurable
public class PDFService extends BasicService {

	@Resource(name = "taskExecutor")
	private TaskExecutor taskExecutor;

	private @Value("${smtpserver}") String smtpServer;
	private @Value("${smtp.port}") String smtpPort;
	private @Value("${sender}") String sender;

	private int max = 15;

	private PDFRenderer getRenderer() {
		try {
			PDFRenderer renderer = null;
			renderer = PDFRendererPoolFactory.getInstance(max, sessionService).checkOut();
			int tries = 0;
			int maxretries = 10;
			while (renderer == null && tries < maxretries) {
				Thread.sleep(1000);
				renderer = PDFRendererPoolFactory.getInstance(max, sessionService).checkOut();
				tries++;
			}
			return renderer;

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return null;
	}

	public java.io.File createSurveyPDF(Survey survey, String lang, java.io.File target) throws IOException {
		String shortname = survey.getShortname();
		logger.debug("Starting PDF creation for survey " + shortname);
		FileOutputStream os = null;
		PDFRenderer renderer = null;
		try {

			if (target == null) {
				target = fileService.getSurveyPDFFile(survey.getUniqueId(), survey.getId(), lang);
			}
			if (target.exists() && target.length() > 0) {
				return target;
			}
			renderer = getRenderer();
			if (renderer == null) {
				throw new MessageException("Not possible to obtain PDFRenderer from pool");
			}
			os = new FileOutputStream(target);
			renderer.createPDF(pdfhost + "runner/preparesurvey/" + survey.getId() + "?lang=" + lang, os);
			return target;
		} catch (Exception ex) {
			logger.error(String.format("PDF creation for survey %s could not be started.", shortname));
			logger.error(ex.getLocalizedMessage(), ex);
		} finally {
			if (os != null)
				os.close();
			if (renderer != null)
				try {
					PDFRendererPoolFactory.getInstance(max, sessionService).checkIn(renderer);
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}
		}

		return null;
	}

	public java.io.File createPublishedAnswerPDF(AnswerSet answerSet) throws IOException {
		sessionService.initializeProxy();
		logger.info("Starting PDF creation for published answer set " + answerSet.getId());
		FileOutputStream os = null;
		PDFRenderer renderer = null;
		try {
			java.io.File folder = fileService.getSurveyExportsFolder(answerSet.getSurvey().getUniqueId());
			java.io.File target = new java.io.File(String.format("%s/publishedanswer%s.pdf", folder.getPath(), answerSet.getId()));

			if (!target.exists() || target.length() < 1024) {
				renderer = getRenderer();
				if (renderer == null) {
					throw new MessageException("Not possible to obtain PDFRenderer from pool");
				}
				os = new FileOutputStream(target);
				renderer.createPDF(pdfhost + "preparepublishedcontribution/" + answerSet.getId(), os);
			}

			return target;
		} catch (Exception ex) {
			logger.error(String.format("PDF creation for published answer %s could not be started.", answerSet.getUniqueCode()));
			logger.error(ex.getLocalizedMessage(), ex);
		} finally {
			if (os != null)
				os.close();
			if (renderer != null)
				try {
					PDFRendererPoolFactory.getInstance(max, sessionService).checkIn(renderer);
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}
		}

		return null;
	}

	public String createAnswerPDF(String code, String email) throws IOException {
		logger.info("starting creation of answer pdf for contribution " + code + " to be sent to " + email);

		if (useworkerserver.equalsIgnoreCase("true") && isworkerserver.equalsIgnoreCase("false")) {
			logger.info("calling worker server for creation of answer pdf for contribution" + code);

			URL workerurl;
			if (email != null) {
				workerurl = new URL(workerserverurl + "worker/createanswerpdf/" + code + "?email=" + email);
			} else {
				workerurl = new URL(workerserverurl + "worker/createanswerpdf/" + code);
			}

			try {
				URLConnection wc = workerurl.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(wc.getInputStream()));
				String inputLine;
				StringBuilder result = new StringBuilder();
				while ((inputLine = in.readLine()) != null)
					result.append(inputLine);
				in.close();

				if (!result.toString().equals("OK")) {
					logger.error("calling worker server for creation of answer pdf for contribution " + code + " returned " + result);
					return Constants.ERROR;
				}

				return "success";
			} catch (ConnectException e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}

		AnswerSet answerSet = answerService.get(code);
		AnswerExecutor export = (AnswerExecutor) context.getBean("answerExecutor");
		if (email != null) {
			export.init(answerSet, email, sender, serverPrefix);
		} else {
			export.init(answerService.get(code));
		}
		taskExecutor.execute(export);
		return "success";
	}

	public String createDraftAnswerPDF(String code, String email) throws IOException {
		logger.info("starting creation of draft answer pdf for contribution " + code + " to be sent to " + email);

		if (useworkerserver.equalsIgnoreCase("true") && isworkerserver.equalsIgnoreCase("false")) {
			logger.info("calling worker server for creation of draft answer pdf for contribution" + code);

			URL workerurl;
			if (email != null) {
				workerurl = new URL(workerserverurl + "worker/createdraftanswerpdf/" + code + "?email=" + email);
			} else {
				workerurl = new URL(workerserverurl + "worker/createdraftanswerpdf/" + code);
			}

			try {
				URLConnection wc = workerurl.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(wc.getInputStream()));
				String inputLine;
				StringBuilder result = new StringBuilder();
				while ((inputLine = in.readLine()) != null)
					result.append(inputLine);
				in.close();

				if (!result.toString().equals("OK")) {
					logger.error("calling worker server for creation of draft answer pdf for contribution " + code + " returned " + result);
					return Constants.ERROR;
				}

				return "success";
			} catch (ConnectException e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}

		Draft draft = answerService.getDraftByAnswerUID(code);
		AnswerExecutor export = (AnswerExecutor) context.getBean("answerExecutor");
		if (email != null) {
			export.init(draft.getAnswerSet(), email, sender, serverPrefix);
		} else {
			export.init(draft.getAnswerSet());
		}
		taskExecutor.execute(export);
		return "success";
	}

	public java.io.File createAnswerPDF(AnswerSet answerSet) throws IOException {
		return createAnswerPDF(answerSet.getId(), answerSet.getUniqueCode(), answerSet.getSurvey().getUniqueId(), answerSet.getIsDraft());
	}

	public java.io.File createAnswerPDF(Integer answerSetId, String uniqueCode, String surveyUid, boolean isDraft) throws IOException {
		logger.info("Starting PDF creation for answer set " + answerSetId != null ? answerSetId : uniqueCode);
		FileOutputStream os = null;
		PDFRenderer renderer = null;
		try {
			java.io.File target = null;
			java.io.File folder = fileService.getSurveyExportsFolder(surveyUid);
			target = new java.io.File(String.format("%s/%s%s.pdf", folder.getPath(), isDraft ? "draft" : Constants.ANSWER, uniqueCode));

			logger.info("starting PDF creation target is " + target.getAbsolutePath());
			renderer = getRenderer();
			if (renderer == null) {
				throw new MessageException("Not possible to obtain PDFRenderer from pool");
			}
			os = new FileOutputStream(target, false);
			logger.debug("starting PDF creation renderer is starting creating PDF ");
			renderer.createPDF(pdfhost + (isDraft ? "preparedraft/" : "preparecontribution/") + uniqueCode, os);

			logger.debug("starting PDF creation renderer is done and return the output target file");
			return target;
		} catch (Exception ex) {
			logger.error(String.format("PDF creation for answer %s could not be started.", uniqueCode));
			logger.error(ex.getLocalizedMessage(), ex);
		} finally {
			if (os != null)
				os.close();
			if (renderer != null)
				try {
					PDFRendererPoolFactory.getInstance(max, sessionService).checkIn(renderer);
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}
		}

		logger.debug("starting PDF creation renderer is NOT done and return null as target");
		return null;
	}

	public java.io.File createAllIndividualResultsPDF(Survey survey, ResultFilter filter, String uid) {
		try {
			Set<Integer> answerSets = answerService.getAllAnswerIds(survey.getId(), filter, 1, Integer.MAX_VALUE);

			java.io.File folder = fileService.getSurveyExportsFolder(survey.getUniqueId());
			java.io.File target = new java.io.File(String.format("%s/%s", folder.getPath(), uid));

			final OutputStream out = new FileOutputStream(target);
			final ArchiveOutputStream os = new ArchiveStreamFactory().createArchiveOutputStream("zip", out);

			for (int answerSetId : answerSets) {
				AnswerSet answerSet = answerService.get(answerSetId);
				java.io.File result = createPublishedAnswerPDF(answerSet);

				os.putArchiveEntry(new ZipArchiveEntry(answerSet.getId() + ".pdf"));
				IOUtils.copy(new FileInputStream(result), os);
				os.closeArchiveEntry();
			}

			os.close();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	public java.io.File createAllIndividualResultsPDF(Survey survey, ResultFilter filter) throws Exception {

		java.io.File target = null;

		Set<Integer> answerSets = answerService.getAllAnswerIds(survey.getId(), filter, 1, Integer.MAX_VALUE);

		target = fileService.createTempFile(UUID.randomUUID().toString(), ".tmp");

		final OutputStream out = new FileOutputStream(target);
		final ArchiveOutputStream os = new ArchiveStreamFactory().createArchiveOutputStream("zip", out);

		Map<String, Map<String, List<File>>> uploadedFiles = answerService.getAllUploadedFilesByContribution(survey.getId(), filter, 1, Integer.MAX_VALUE);

		int invalidCounter = 0;
		for (int answerSetId : answerSets) {
			AnswerSet answerSet = answerService.get(answerSetId);

			java.io.File result = createAnswerPDF(answerSet);

			if (result != null) {
				os.putArchiveEntry(new ZipArchiveEntry("PDFs/" + answerSet.getUniqueCode() + ".pdf"));
				IOUtils.copy(new FileInputStream(result), os);
				os.closeArchiveEntry();
			} else {
				invalidCounter++;
				if (invalidCounter >= 3) {
					throw new MessageException("too many invalid PDFs generated");
				}
			}
		}

		for (Entry<String, Map<String, List<File>>> entry : uploadedFiles.entrySet()) {
			for (String nicename : entry.getValue().keySet()) {
				for (File file : entry.getValue().get(nicename)) {
					java.io.File f = fileService.getSurveyFile(survey.getUniqueId(), file.getUid());
					if (!f.exists()) {
						f = new java.io.File(fileDir + file.getUid());
						if (f.exists()) {
							fileService.logOldFileSystemUse(fileDir + file.getUid());
						}
					}
					if (f.exists()) {
						os.putArchiveEntry(new ZipArchiveEntry(entry.getKey() + Constants.PATH_DELIMITER + nicename + Constants.PATH_DELIMITER + file.getName()));
						IOUtils.copy(new FileInputStream(f), os);
						os.closeArchiveEntry();
					}
				}
			}
		}

		os.close();

		return target;
	}

	public java.io.File createChartsPDF(Survey survey, String exportId) throws IOException {
		String shortname = survey.getShortname();
		logger.info("Starting PDF creation for results (charts) of survey " + shortname);
		FileOutputStream os = null;
		PDFRenderer renderer = null;
		try {

			java.io.File folder = fileService.getSurveyExportsFolder(survey.getUniqueId());
			java.io.File target = new java.io.File(String.format("%s/charts%s.pdf", folder.getPath(), UUID.randomUUID().toString()));

			renderer = getRenderer();
			if (renderer == null) {
				throw new MessageException("Not possible to obtain PDFRenderer from pool");
			}
			os = new FileOutputStream(target);
			renderer.createPDF(pdfhost + survey.getShortname() + "/management/preparecharts/" + survey.getId() + Constants.PATH_DELIMITER + exportId, os);

			return target;
		} catch (Exception ex) {
			logger.error(String.format("PDF creation for survey %s could not be started.", shortname));
			logger.error(ex.getLocalizedMessage(), ex);
		} finally {
			if (os != null)
				os.close();
			if (renderer != null)
				try {
					PDFRendererPoolFactory.getInstance(max, sessionService).checkIn(renderer);
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}
		}

		return null;
	}

	public java.io.File createStatisticsPDF(Survey survey, String exportId) throws IOException {
		String shortname = survey.getShortname();
		logger.info("Starting PDF creation for results (statistics) of survey " + shortname);
		FileOutputStream os = null;
		PDFRenderer renderer = null;
		try {

			java.io.File folder = fileService.getSurveyExportsFolder(survey.getUniqueId());
			java.io.File target = new java.io.File(String.format("%s/charts%s.pdf", folder.getPath(), UUID.randomUUID().toString()));

			renderer = getRenderer();
			if (renderer == null) {
				throw new MessageException("Not possible to obtain PDFRenderer from pool");
			}
			os = new FileOutputStream(target);
			renderer.createPDF(pdfhost + survey.getShortname() + "/management/preparestatistics/" + survey.getId() + Constants.PATH_DELIMITER + exportId, os);

			return target;
		} catch (Exception ex) {
			logger.error(String.format("PDF creation for survey %s could not be started.", shortname));
			logger.error(ex.getLocalizedMessage(), ex);
		} finally {
			if (os != null)
				os.close();
			if (renderer != null)
				try {
					PDFRendererPoolFactory.getInstance(max, sessionService).checkIn(renderer);
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}
		}

		return null;
	}

	public java.io.File createStatisticsQuizPDF(Survey survey, String exportId) throws IOException {
		String shortname = survey.getShortname();
		logger.info("Starting PDF creation for quiz results (statistics) of survey " + shortname);
		FileOutputStream os = null;
		PDFRenderer renderer = null;
		try {

			java.io.File folder = fileService.getSurveyExportsFolder(survey.getUniqueId());
			java.io.File target = new java.io.File(String.format("%s/statistics%s.pdf", folder.getPath(), UUID.randomUUID().toString()));

			renderer = getRenderer();
			if (renderer == null) {
				throw new MessageException("Not possible to obtain PDFRenderer from pool");
			}
			os = new FileOutputStream(target);
			renderer.createPDF(pdfhost + survey.getShortname() + "/management/preparestatisticsquiz/" + survey.getId() + Constants.PATH_DELIMITER + exportId, os);

			return target;
		} catch (Exception ex) {
			logger.error(String.format("PDF creation for survey %s could not be started.", shortname));
			logger.error(ex.getLocalizedMessage(), ex);
		} finally {
			if (os != null)
				os.close();
			if (renderer != null)
				try {
					PDFRendererPoolFactory.getInstance(max, sessionService).checkIn(renderer);
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}
		}

		return null;
	}

	public java.io.File createQuizPDF(AnswerSet answerSet) throws IOException {
		logger.info("Starting PDF creation for results (quiz) for contribution " + answerSet.getId());
		FileOutputStream os = null;
		PDFRenderer renderer = null;
		try {

			java.io.File folder = fileService.getSurveyExportsFolder(answerSet.getSurvey().getUniqueId());
			java.io.File target = new java.io.File(String.format("%s/quiz%s.pdf", folder.getPath(), UUID.randomUUID().toString()));

			renderer = getRenderer();
			
			if (renderer == null) {
				throw new MessageException("Not possible to obtain PDFRenderer from pool");
			}
			os = new FileOutputStream(target);
			renderer.createPDF(pdfhost + "preparequizresults/" + answerSet.getUniqueCode(), os);

			return target;
		} catch (Exception ex) {
			logger.error(String.format("PDF quiz result creation for contribution %s could not be started.", answerSet.getId()));
			logger.error(ex.getLocalizedMessage(), ex);
		} finally {
			if (os != null)
				os.close();
			if (renderer != null)
				try {
					PDFRendererPoolFactory.getInstance(max, sessionService).checkIn(renderer);
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}
		}

		return null;
	}

}
