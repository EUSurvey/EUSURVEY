package com.ec.survey.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jmimemagic.Magic;

import org.apache.catalina.connector.ClientAbortException;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.LocalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.model.survey.base.File;
import com.ec.survey.exception.ForbiddenURLException;
import com.ec.survey.exception.InvalidURLException;

@Controller
@RequestMapping("/files")
public class FileController extends BasicController {

	@RequestMapping(value = "/{uid}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView file(@PathVariable String uid, HttpServletRequest request, HttpServletResponse response)
			throws ForbiddenURLException, InvalidURLException {

		if (uid != null && uid.length() > 0) {
			File file = null;
			try {

				file = fileService.get(uid);
				
				java.io.File f = new java.io.File(fileDir + file.getUid());
				
				if (f.exists())
				{
					fileService.logOldFileSystemUse(fileDir + file.getUid());
					
					//check if it is an uploaded file
					Survey survey = surveyService.getSurveyForUploadedFile(file.getId());
					if (survey != null && !(survey.getPublication().isShowContent()
						&& survey.getPublication().getShowUploadedDocuments())) {
						User user = sessionService.getCurrentUser(request);
						if (user == null)
							throw new ForbiddenURLException();

						sessionService.upgradePrivileges(survey, user, request);
						if (!survey.getOwner().getId().equals(user.getId())
								&& user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
								&& user.getLocalPrivileges().get(LocalPrivilege.AccessResults) < 1
								&& (user.getLocalPrivileges().get(LocalPrivilege.AccessDraft) < 1
										|| !survey.getIsDraft())) {
							throw new ForbiddenURLException();
						}
					}

					response.setContentLength((int) f.length());
					response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

					String type = getMimeType(f);
										
					if (file.getName().toLowerCase().endsWith("pdf")) {
						response.setContentType("application/pdf");
					} else if (file.getName().toLowerCase().endsWith("xml")) {
						response.setContentType("application/xml");
					} else if (file.getName().toLowerCase().endsWith("xls")
							|| file.getName().toLowerCase().endsWith("xlsx")) {
						response.setContentType("application/msexcel");
					} else if (file.getName().toLowerCase().endsWith("doc")
							|| file.getName().toLowerCase().endsWith("docx")) {
						response.setContentType("application/msword");
					} else if (type != null && type.length() > 0) {
						response.setContentType(type);
					}

					FileCopyUtils.copy(new FileInputStream(f), response.getOutputStream());
				}

			} catch (FileNotFoundException | ClientAbortException iv) {
				//ignore
			} catch (ForbiddenURLException fe1) {
				throw fe1;
			} catch (Exception e1) {
				logger.error(e1.getLocalizedMessage(), e1);
			}

		}
		throw new InvalidURLException();
	}

	private String getMimeType(java.io.File f) {
		try {
			return Magic.getMagicMatch(f, false).getMimeType();
		} catch (Exception e) {
			// unknown type
			return "";
		}
	}
	
	@RequestMapping(value = "/withcomment/{uid}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView fileWithComment(@PathVariable String uid, HttpServletRequest request,
			HttpServletResponse response) {

		if (uid != null && uid.length() > 0) {
			File file;
			try {
				file = fileService.get(uid);

				return new ModelAndView("filewithcomment", "file", file);
			} catch (Exception e1) {
				logger.error(e1);
			}
		}

		return null;
	}

	@RequestMapping(value = "/{surveyuid}/{uid}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView surveyfile(@PathVariable String surveyuid, @PathVariable String uid, HttpServletRequest request,
			HttpServletResponse response) throws ForbiddenURLException {

		if (uid != null && uid.length() > 0) {
			File file = null;
			boolean zipped = false;
			try {

				file = fileService.get(uid);
				
				java.io.File f = fileService.getSurveyFile(surveyuid, uid);

				if (!f.exists()) {
					// might be an export file
					f = fileService.getSurveyExportFile(surveyuid, uid);

					if (f.exists()) {
						java.io.File zip = new java.io.File(f.getPath() + ".zip");
						if (zip.exists()) {
							// not if coming from the file management console
							String fromfmc = request.getParameter("fromfmc");
							if (fromfmc == null) {
								f = zip;
								zipped = true;
							}
						}
					}
				}

				if (!f.exists()) {
					// fallback to old file system
					f = new java.io.File(fileDir + file.getUid());
					fileService.logOldFileSystemUse(fileDir + file.getUid());
				}

				if (f.exists()) {
					// check if it is an uploaded file
					Survey survey = surveyService.getSurveyForUploadedFile(file.getId());
					if (survey != null && !(survey.getPublication().isShowContent()
							&& survey.getPublication().getShowUploadedDocuments())) {
						User user = sessionService.getCurrentUser(request);
						if (user == null)
							throw new ForbiddenURLException();

						sessionService.upgradePrivileges(survey, user, request);
						if (!survey.getOwner().getId().equals(user.getId())
								&& user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) < 2
								&& user.getLocalPrivileges().get(LocalPrivilege.AccessResults) < 1
								&& user.getLocalPrivileges().get(LocalPrivilege.AccessDraft) < 1
								|| !survey.getIsDraft()) {
							throw new ForbiddenURLException();
						}
					}

					response.setContentLength((int) f.length());

					if (zipped) {
						response.setHeader("Content-Disposition",
								"attachment; filename=\"" + file.getName() + ".zip\"");
					} else {
						response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
					}

					String type = getMimeType(f);

					if (zipped) {
						response.setContentType("application/zip");
					} else if (file.getName().toLowerCase().endsWith("pdf")) {
						response.setContentType("application/pdf");
					} else if (file.getName().toLowerCase().endsWith("xml")) {
						response.setContentType("application/xml");
					} else if (file.getName().toLowerCase().endsWith("xls")
							|| file.getName().toLowerCase().endsWith("xlsx")) {
						response.setContentType("application/msexcel");
					} else if (file.getName().toLowerCase().endsWith("doc")
							|| file.getName().toLowerCase().endsWith("docx")) {
						response.setContentType("application/msword");
					} else if (type != null && type.length() > 0) {
						response.setContentType(type);
					}

					FileCopyUtils.copy(new FileInputStream(f), response.getOutputStream());					
				}

			} catch (ClientAbortException ce) {
				// ignore
			} catch (ForbiddenURLException fe1) {
				throw fe1;
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}

		}

		return null;
	}
}
