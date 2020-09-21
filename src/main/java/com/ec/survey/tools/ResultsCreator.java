package com.ec.survey.tools;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.ec.survey.model.Export;
import com.ec.survey.model.Form;
import com.ec.survey.model.ResultFilter;
import com.ec.survey.model.WebserviceTask;
import com.ec.survey.model.survey.Element;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.service.AnswerService;
import com.ec.survey.service.ExportService;
import com.ec.survey.service.FileService;
import com.ec.survey.service.PDFService;
import com.ec.survey.service.ParticipationService;
import com.ec.survey.service.SurveyService;
import com.ec.survey.service.TranslationService;
import com.ec.survey.service.WebserviceService;
import com.ec.survey.tools.export.XmlExportCreator;
import com.lowagie.text.pdf.PdfReader;

@Service("resultsCreator")
@Scope("prototype")
public class ResultsCreator implements Runnable, BeanFactoryAware {

	protected static final Logger logger = Logger.getLogger(ResultsCreator.class);

	@Resource(name = "fileService")
	protected FileService fileService;

	@Resource(name = "exportService")
	protected ExportService exportService;

	@Resource(name = "webserviceService")
	protected WebserviceService webserviceService;

	@Resource(name = "answerService")
	protected AnswerService answerService;

	@Resource(name = "surveyService")
	protected SurveyService surveyService;

	@Resource(name = "sessionFactory")
	protected SessionFactory sessionFactory;

	@Resource(name = "participationService")
	protected ParticipationService participationService;

	@Resource(name = "translationService")
	protected TranslationService translationService;

	@Resource(name = "pdfService")
	protected PDFService pdfService;

	protected @Value("${export.fileDir}") String fileDir;

	private int task;

	public Integer getTask() {
		return task;
	}

	public void setTask(int task) {
		this.task = task;
	}

	private MessageSource resources;
	private Locale locale;

	private BeanFactory context;

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		context = beanFactory;
	}

	public void init(int task, MessageSource resources, Locale locale) {
		this.task = task;
		this.resources = resources;
		this.locale = locale;
	}

	@Override
	public void run() {
		try {
			webserviceService.setStarted(task);
			WebserviceTask webserviceTask = webserviceService.get(task);

			String uid = UUID.randomUUID().toString();

			Form form = new Form(resources);

			Survey survey = null;

			if (webserviceTask.getSurveyUid() != null && webserviceTask.getSurveyUid().length() > 0) {
				survey = surveyService.getSurveyByUniqueId(webserviceTask.getSurveyUid(), true, false);
			}

			if (survey == null) {
				survey = surveyService.getSurvey(webserviceTask.getSurveyId(), true, true);
			}

			if (survey == null) {
				webserviceService.setError(task, "Survey with id " + webserviceTask.getSurveyId() + " not found");
				return;
			}

			List<String> translations = translationService
					.getTranslationLanguagesForSurvey(webserviceTask.getSurveyId(), false);
			survey.setTranslations(translations);

			form.setSurvey(survey);

			Export export = new Export();
			export.setSurvey(survey);
			export.setDate(new Date());

			ResultFilter filter = new ResultFilter();

			if (webserviceTask.getContributionType() != null
					&& webserviceTask.getContributionType().equalsIgnoreCase("N")) {
				filter.setGeneratedFrom(webserviceTask.getStart());
				filter.setGeneratedTo(webserviceTask.getEnd());
			} else if (webserviceTask.getContributionType() != null
					&& webserviceTask.getContributionType().equalsIgnoreCase("U")) {
				filter.setOnlyReallyUpdated(true);
				filter.setUpdatedFrom(webserviceTask.getStart());
				filter.setUpdatedTo(webserviceTask.getEnd());
			} else { // A
				filter.setCreatedOrUpdated(true);
				filter.setUpdatedFrom(webserviceTask.getStart());
				filter.setUpdatedTo(webserviceTask.getEnd());
				filter.setGeneratedFrom(webserviceTask.getStart());
				filter.setGeneratedTo(webserviceTask.getEnd());
			}

			if (webserviceTask.getToken() != null && webserviceTask.getToken().length() > 0) {
				// this is a single answerSet export
				filter.setInvitation(webserviceTask.getToken());
			}

			export.setShowShortnames(webserviceTask.isShowIDs());
			export.setAddMeta(webserviceTask.isAddMeta());
			export.setResultFilter(filter);

			XmlExportCreator xmlExportCreator = (XmlExportCreator) context.getBean("xmlExportCreator");
			java.io.File target = fileService.getSurveyExportFile(webserviceTask.getSurveyUid(), uid);
			xmlExportCreator.init(0, form, null, target.getAbsolutePath(), resources, locale, "", "");

			if (webserviceTask.getExportType() != null && webserviceTask.getExportType().equals(2)) {
				xmlExportCreator.SimulateExportContent(false, export);
			} else {
				xmlExportCreator.ExportContent(false, export, true);
			}

			Map<Integer, String> uniqueCodesById = xmlExportCreator.getExportedUniqueCodes();
			Map<Integer, String> questionIdsByAnswerId = xmlExportCreator.getExportedQuestionsByAnswerId();
			Map<String, Element> questionsByUniqueId = survey.getElementsByUniqueId();

			if (uniqueCodesById.size() == 0) {
				webserviceTask.setEmpty(true);
			}

			// results_[type]-<alias>[_<start-date>][_to_<end-date>].zip
			String zipFileName = "results";
			if (webserviceTask.getExportType() != null) {
				if (webserviceTask.getExportType().equals(1)) {
					zipFileName += "_xml";
				} else if (webserviceTask.getExportType().equals(2)) {
					zipFileName += "_pdf";
				}
				zipFileName += "-" + survey.getShortname();
				if (webserviceTask.getStart() != null) {
					zipFileName += "_" + ConversionTools.getFullString4Webservice(webserviceTask.getStart());
				}
				if (webserviceTask.getEnd() != null) {
					zipFileName += "_" + ConversionTools.getFullString4Webservice(webserviceTask.getEnd());
				} else {
					zipFileName += "_" + ConversionTools.getFullString4Webservice(xmlExportCreator.getExportedNow());
				}
			}
			zipFileName += ".zip";

			String uid2 = UUID.randomUUID().toString();

			java.io.File temp = fileService.getSurveyExportFile(survey.getUniqueId(), uid2);
			final OutputStream out = new FileOutputStream(temp);
			final ArchiveOutputStream os = new ArchiveStreamFactory().createArchiveOutputStream("zip", out);

			File f = new File();
			f.setUid(uid2);
			f.setName(zipFileName);

			if (webserviceTask.getExportType() == null || webserviceTask.getExportType().equals(0)
					|| webserviceTask.getExportType().equals(1) || (webserviceTask.getExportType().equals(3)
							&& webserviceTask.getFileTypes() != null && webserviceTask.getFileTypes().contains("x"))) {
				os.putArchiveEntry(new ZipArchiveEntry("result.xml"));
				IOUtils.copy(new FileInputStream(target), os);
				os.closeArchiveEntry();
			}

			int invalidCounter = 0;

			for (Integer answerSetId : uniqueCodesById.keySet()) {

				String uniqueCode = uniqueCodesById.get(answerSetId);

				// this counter should stop creation of PDFs when there is an obvious problem
				if ((webserviceTask.getExportType() == null || webserviceTask.getExportType().equals(0)
						|| webserviceTask.getExportType().equals(2)
						|| (webserviceTask.getExportType().equals(3) && webserviceTask.getFileTypes() != null
								&& webserviceTask.getFileTypes().contains("p")))
						&& invalidCounter < 3) {
					java.io.File answerPDF = pdfService.createAnswerPDF(answerSetId, uniqueCodesById.get(answerSetId),
							survey.getUniqueId(), false);
					if (answerPDF != null) {
						// check validity of file
						PdfReader ReadInputPDF;

						try {
							ReadInputPDF = new PdfReader(answerPDF.getPath());
							if (ReadInputPDF.getInfo().containsKey("Subject")) {
								String subject = ReadInputPDF.getInfo().get("Subject").toString();
								if (!subject.contains(uniqueCodesById.get(answerSetId))) {
									// possibly an invalid pdf -> recreate
									answerPDF.delete();
									answerPDF = pdfService.createAnswerPDF(answerSetId,
											uniqueCodesById.get(answerSetId), survey.getUniqueId(), false);
								}
							} else {
								// older file without meta info -> recreate
								answerPDF.delete();
								answerPDF = pdfService.createAnswerPDF(answerSetId, uniqueCodesById.get(answerSetId),
										survey.getUniqueId(), false);
							}
							ReadInputPDF.close();
						} catch (Exception e) {
							// file seems to be corrupt -> recreate
							answerPDF.delete();
							answerPDF = pdfService.createAnswerPDF(answerSetId, uniqueCodesById.get(answerSetId),
									survey.getUniqueId(), false);
						}

						if (answerPDF != null) {
							os.putArchiveEntry(new ZipArchiveEntry(uniqueCode + Constants.PATH_DELIMITER + uniqueCode + ".pdf"));
							IOUtils.copy(new FileInputStream(answerPDF), os);
							os.closeArchiveEntry();
						} else {
							os.putArchiveEntry(new ZipArchiveEntry(uniqueCode + Constants.PATH_DELIMITER + uniqueCode + ".error.txt"));
							os.write("The PDF file could not be generated".getBytes());
							os.closeArchiveEntry();
							invalidCounter++;

							if (invalidCounter == 3) {
								os.putArchiveEntry(new ZipArchiveEntry("error.txt"));
								os.write("The PDF creation was stopped as the files could not be generated".getBytes());
								os.closeArchiveEntry();
							}
						}
					} else {
						os.putArchiveEntry(new ZipArchiveEntry(uniqueCode + Constants.PATH_DELIMITER + uniqueCode + ".error.txt"));
						os.write("The PDF file could not be generated".getBytes());
						os.closeArchiveEntry();
						invalidCounter++;

						if (invalidCounter == 3) {
							os.putArchiveEntry(new ZipArchiveEntry("error.txt"));
							os.write("The PDF creation was stopped as the files could not be generated".getBytes());
							os.closeArchiveEntry();
						}
					}
				}

				if ((webserviceTask.getExportType() != null && !webserviceTask.getExportType().equals(3))
						|| (webserviceTask.getFileTypes() != null && webserviceTask.getFileTypes().contains("u"))) {
					List<File> uploadedFiles = answerService.getUploadedFilesForAnswerset(answerSetId);
					for (File uploadedFile : uploadedFiles) {
						java.io.File uploadedFileIO = fileService.getSurveyFile(survey.getUniqueId(),
								uploadedFile.getUid());

						if (!uploadedFileIO.exists()) {
							uploadedFileIO = new java.io.File(fileDir + uploadedFile.getUid());
							if (uploadedFileIO.exists()) {
								fileService.logOldFileSystemUse(fileDir + uploadedFile.getUid());
							}
						}

						String folderName = uploadedFile.getUid();
						if (uploadedFile.getAnswerId() != null) {
							if (questionIdsByAnswerId.containsKey(uploadedFile.getAnswerId())) {
								String questionUniqueId = questionIdsByAnswerId.get(uploadedFile.getAnswerId());
								if (questionsByUniqueId.containsKey(questionUniqueId)) {
									folderName = questionsByUniqueId.get(questionUniqueId).getShortname();
								}
							}
						}

						os.putArchiveEntry(new ZipArchiveEntry(
								uniqueCode + "/Uploaded Files/" + folderName + Constants.PATH_DELIMITER + uploadedFile.getName()));
						IOUtils.copy(new FileInputStream(uploadedFileIO), os);
						os.closeArchiveEntry();
					}
				}
			}

			os.close();
			fileService.add(f);
			webserviceTask.setResult(uid2);

			webserviceTask.setDone(true);
			webserviceService.save(webserviceTask);

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			try {
				webserviceService.setError(task,
						e.getLocalizedMessage() != null ? e.getLocalizedMessage() : e.toString());
			} catch (Exception e1) {
				logger.error(e1.getLocalizedMessage(), e1);
			}
		}
		logger.debug("TokenCreator completed");
	}

}
