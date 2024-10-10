package com.ec.survey.controller;

import com.ec.survey.exception.*;
import com.ec.survey.model.*;
import com.ec.survey.model.administration.EcasUser;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.attendees.Attendee;
import com.ec.survey.model.attendees.Invitation;
import com.ec.survey.model.survey.*;
import com.ec.survey.service.*;
import com.ec.survey.tools.*;
import org.apache.commons.io.IOUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Validator;
import org.owasp.esapi.errors.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mobile.device.Device;
import org.springframework.orm.hibernate5.HibernateOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/runner")
public class RunnerController extends BasicController {

	@Resource(name = "validCodesService")
	private ValidCodesService validCodesService;

	@Resource(name = "taskExecutor")
	private TaskExecutor taskExecutor;

	@Resource(name = "mailService")
	private MailService mailService;

	@Resource(name = "pdfService")
	private PDFService pdfService;

	private @Value("${smtpserver}") String smtpServer;
	private @Value("${smtp.port}") String smtpPort;

	@PostMapping(value = "/checksession")
	public @ResponseBody String checkSession() {
		return "OK";
	}

	@GetMapping(value = "/confirmation")
	public ModelAndView confirmation(HttpServletRequest request, HttpServletResponse response, Locale locale,
			Model modelMap) throws InvalidURLException {
		String code = request.getParameter("code");
		String lang = request.getParameter("surveylanguage");

		if (code == null || lang == null)
			throw new InvalidURLException();

		AnswerSet answerSet = answerService.get(code);

		if (answerSet == null)
			throw new InvalidURLException();

		Survey survey = surveyService.getSurvey(answerSet.getSurveyId(), lang);

		ModelAndView result = new ModelAndView("thanks", Constants.UNIQUECODE, code);

		if (!survey.isAnonymous() && answerSet.getResponderEmail() != null) {
			result.addObject("participantsemail", answerSet.getResponderEmail());
		}

		result.addObject("isthankspage", true);
		result.addObject("runnermode", true);

		Form form = new Form(resources, surveyService.getLanguage(lang),
				translationService.getActiveTranslationsForSurvey(survey.getId()), contextpath);
		form.setSurvey(survey);
		
		if (survey.getIsOPC()) {
			result.addObject("opcredirection", form.getFinalConfirmationLink(opcredirect, lang, answerSet));
		}

		result.addObject("form", form);
		result.addObject("text", survey.getConfirmationPage());
		if (answerSet.getSurvey().getConfirmationPageLink() != null && survey.getConfirmationPageLink()
				&& survey.getConfirmationLink() != null && survey.getConfirmationLink().length() > 0) {
			result.addObject("redirect", form.getFinalConfirmationLink(lang, answerSet));
		}
		result.addObject("surveyprefix", survey.getId());

		return result;
	}

	@RequestMapping(value = "/invited/{group}/{unique}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView invited(@PathVariable String group, @PathVariable String unique, HttpServletRequest request,
			Locale locale, Integer draftSurveyId, Device device)
			throws WeakAuthenticationException, NotAgreedToPsException, NotAgreedToTosException, FrozenSurveyException {

		boolean readonlyMode = false;
		String p = request.getParameter("readonly");
		if (p != null && p.equalsIgnoreCase("true") && unique != null && unique.length() > 0) {
			User user = sessionService.getCurrentUser(request, false, true);
			if (user != null && user.getFormPrivilege() > 1) {
				readonlyMode = true;
			}
		}

		ParticipationGroup participationGroup = null;
		Invitation invitation = null;
		try {
			participationGroup = participationService.get(Integer.parseInt(group));
			invitation = attendeeService.getInvitationByUniqueId(unique);

			if (invitation != null && invitation.getAnswers() > 0) {

				ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
				model.addObject(Constants.MESSAGE,
						resources.getMessage("error.InvitationUsed", null, "The invitation was already used.", locale));
				AnswerSet aws = answerService.getByInvitationCode(invitation.getUniqueId());
				Date answerDate;
				if (aws != null) {
					if (aws.getSurvey().getIsDelphi())
					{
						return new ModelAndView("redirect:/editcontribution/" + aws.getUniqueCode());
					}
					
					answerDate = aws.getDate();
					String[] args = new String[] { ConversionTools.getFullString(answerDate) };
					model.addObject("messageComplement", resources.getMessage("message.SubmittedOn", args,
							"Submitted on: " + ConversionTools.getFullString(answerDate), locale));
				}
				model.addObject("skipErrorLabel", true);
				model.addObject("noMenu", true);
				model.addObject("runnermode", true);

				Survey survey = surveyService.getSurveyByUniqueId(participationGroup.getSurveyUid(), false, false);
				if (survey.getDownloadContribution()) {
					model.addObject("caseidforpdfdownload", invitation.getUniqueId());
				}
				if (survey.getChangeContribution()) {
					model.addObject("caseidforchangecontribution", invitation.getUniqueId());
				}

				if (!survey.isAnonymous() && aws != null && aws.getResponderEmail() != null) {
					model.addObject("participantsemail", aws.getResponderEmail());
				}

				return model;
			}

			if (invitation != null && (invitation.getDeactivated() != null && invitation.getDeactivated() || participationGroup == null)) {
				ModelAndView model = new ModelAndView("error/generic");
				model.addObject("message", resources.getMessage("error.AccessDisabled", null, "The access has been disabled.", locale));
				model.addObject("noMenu", true);
				model.addObject("runnermode", true);
				return model;
			}

			if (invitation != null && (invitation.getDeactivated() == null || !invitation.getDeactivated())
					&& participationGroup != null && invitation.getParticipationGroupId().equals(participationGroup.getId())) {
				if (!participationGroup.getActive()) {
					ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
					model.addObject(Constants.MESSAGE, resources.getMessage("error.InvitationDeactivated", null,
							"The access for this guest-list has not yet been activated. Please try again later.",
							locale));
					model.addObject("noMenu", true);
					model.addObject("runnermode", true);
					return model;
				}

				Survey survey = surveyService.getSurveyByUniqueId(participationGroup.getSurveyUid(), false, false);

				// fallback for old surveys without unique ID
				if (survey == null) {
					survey = surveyService.getSurvey(participationGroup.getSurveyId(), false, true);
					// this is the base/draft survey, but we need the active one
					survey = surveyService.getSurvey(survey.getShortname(), false, true, false, false, null, true,
							true);
				}

				if (survey != null) {

					Survey draftSurvey = null;

					if (draftSurveyId != null && draftSurveyId > 0) {
						draftSurvey = surveyService.getSurvey(draftSurveyId);
					} else {
						draftSurvey = surveyService.getSurveyByUniqueId(survey.getUniqueId(), false, true);
					}

					if (!draftSurvey.getIsActive()
							|| SurveyHelper.isDeactivatedOrEndDateExceeded(draftSurvey, surveyService)) {

						if (draftSurvey.getIsFrozen()) {
							throw new FrozenSurveyException();
						}	
						
						if (draftSurvey.getIsDeleted() || draftSurvey.getArchived()) {
							throw new InvalidURLException();
						}

						if (!readonlyMode)
							return getEscapePageModel(draftSurvey, request, device);
					}

					if (SurveyHelper.isMaxContributionReached(survey, answerService)) {
						return getMaxAnswersReachedPageModel(survey, request, device);
					}

					if (draftSurvey.getIsFrozen()) {
						throw new FrozenSurveyException();
					}
					
					if (!draftSurvey.getLanguage().getCode().equals(survey.getLanguage().getCode())) {
						//this can happen as a result of a known issue
						SurveyHelper.synchronizeSurvey(survey, draftSurvey.getLanguage().getCode(), translationService, draftSurvey.getLanguage(), true);
					}

					Form f = new Form(survey, translationService.getActiveTranslationsForSurvey(survey.getId()),
							survey.getLanguage(), resources, contextpath);

					String lang = request.getParameter("surveylanguage");

					if (lang != null) {

						if (f.translationIsValid(lang)) {
							List<Translations> translations = translationService
									.getTranslationsForSurvey(survey.getId(), true);
							for (Translations trans : translations) {
								if (trans.getLanguage().getCode().equalsIgnoreCase(lang)
										&& (!trans.getComplete() || !trans.getActive())) {
									return new ModelAndView("redirect:/runner/invited/" + group + Constants.PATH_DELIMITER + unique);
								}
							}

							Survey translated = SurveyHelper.createTranslatedSurvey(f.getSurvey().getId(), lang,
									surveyService, translationService, true);
							f.setSurvey(translated);
							f.setLanguage(surveyService.getLanguage(lang));
						} else {
							return new ModelAndView("redirect:/runner/invited/" + group + Constants.PATH_DELIMITER + unique);
						}
					}

					String wcag = request.getParameter("wcag");
					if (wcag != null && wcag.equalsIgnoreCase("enabled")) {
						f.setWcagCompliance(true);
					} else if (wcag != null && wcag.equalsIgnoreCase("disabled")) {
						f.setWcagCompliance(false);
					}

					ModelAndView model = new ModelAndView("runner/runner", "form", f);
					surveyService.initializeSkin(f.getSurvey());
					model.addObject("submit", !readonlyMode);
					model.addObject("runnermode", true);
					model.addObject("invitation", invitation.getId());
					model.addObject("participationGroup", participationGroup.getId());
					model.addObject("readonlyMode", readonlyMode);

					String uniqueCode = unique;
					String draftid = request.getParameter("draftid");
					if (draftid != null && draftid.trim().length() > 0) {
						try {
							Draft draft = answerService.getDraft(draftid);

							ModelAndView err = testDraftAlreadySubmitted(draft, locale);
							if (err != null)
								return err;

							f.getAnswerSets().add(draft.getAnswerSet());
							f.setWcagCompliance(
									draft.getAnswerSet().getWcagMode() != null && draft.getAnswerSet().getWcagMode());

							SurveyHelper.recreateUploadedFiles(draft.getAnswerSet(), f.getSurvey(),
									fileService, answerExplanationService);
							uniqueCode = draft.getAnswerSet().getUniqueCode();

							if (lang == null) {
								lang = draft.getAnswerSet().getLanguageCode();
								Survey translated = SurveyHelper.createTranslatedSurvey(f.getSurvey().getId(), lang,
										surveyService, translationService, true);
								f.setSurvey(translated);
								f.setLanguage(surveyService.getLanguage(lang));
							}

							Set<String> invisibleElements = new HashSet<>();
							SurveyHelper.validateAnswerSet(draft.getAnswerSet(), answerService, invisibleElements,
									resources, locale, null, null, true, null, fileService);
							model.addObject("invisibleElements", invisibleElements);

							model.addObject("draftid", draftid);
						} catch (Exception e) {
							logger.error(e.getLocalizedMessage(), e);
						}
					} else {
						try {
							Draft draft = answerService.getDraftForInvitation(invitation.getUniqueId());
							if (draft != null) {
								ModelAndView err = testDraftAlreadySubmitted(draft, locale);
								if (err != null)
									return err;

								f.getAnswerSets().add(draft.getAnswerSet());
								SurveyHelper.recreateUploadedFiles(draft.getAnswerSet(), f.getSurvey(),
										fileService, answerExplanationService);
								uniqueCode = draft.getAnswerSet().getUniqueCode();

								if (lang == null) {
									lang = draft.getAnswerSet().getLanguageCode();
									Survey translated = SurveyHelper.createTranslatedSurvey(f.getSurvey().getId(), lang,
											surveyService, translationService, true);
									f.setSurvey(translated);
									f.setLanguage(surveyService.getLanguage(lang));
								}

								Set<String> invisibleElements = new HashSet<>();
								SurveyHelper.validateAnswerSet(draft.getAnswerSet(), answerService, invisibleElements,
										resources, locale, null, null, true, null, fileService);
								model.addObject("invisibleElements", invisibleElements);

								model.addObject("draftid", draft.getUniqueId());
							} else {
								if (survey.getIsQuiz() && request.getParameter("startQuiz") == null) {
									model = new ModelAndView("runner/quiz", "form", f);
									model.addObject("isquizpage", true);
								} else if (survey.getIsQuiz()) {
									// the start date for the time limit starts when the user left the quiz start page
									sessionService.SetUniqueCodeForForm(request, survey.getId(), uniqueCode);
									sessionService.setFormStartDate(request, f, uniqueCode);
								} else if (survey.getIsDelphi() && survey.getIsDelphiShowStartPage() && request.getParameter("startDelphi") == null) {
									model = new ModelAndView("runner/delphi", "form", f);
									model.addObject("isdelphipage", true);
								}
							}
						} catch (Exception e) {
							logger.error(e.getLocalizedMessage(), e);
						}
					}

					model.addObject(Constants.UNIQUECODE, uniqueCode);
					request.getSession().setAttribute(Constants.UNIQUECODE, uniqueCode);

					return model;
				} else {
					Survey draft = surveyService.getSurveyByUniqueId(participationGroup.getSurveyUid(), false, true);

					if (draft != null) {
						if (!draft.getIsDeleted() && !draft.getArchived()) {
							return getEscapePageModel(draft, request, device);
						} else {
							throw new InvalidURLException();
						}
					} else {
						ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
						model.addObject(Constants.MESSAGE, resources.getMessage("error.SurveyNotActive", null,
								"The survey is not active at the moment. Please try again later.", locale));
						model.addObject("noMenu", true);
						return model;
					}
				}
			}
		} catch (FrozenSurveyException fe) {
			throw fe;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
		model.addObject(Constants.MESSAGE,
				resources.getMessage("error.WrongURL", null, "The url you entered was not correct.", locale));
		model.addObject("noMenu", true);
		model.addObject("runnermode", true);
		return model;
	}

	@PostMapping(value = "/{shortname}/{token}")
	public ModelAndView runnerTokenPost(@PathVariable String shortname, @PathVariable String token,
			HttpServletRequest request, Locale locale, Device device)
			throws InvalidURLException, ForbiddenURLException, FrozenSurveyException {

		Survey survey = surveyService.getSurveyByShortname(shortname, false, null, request, true, true, true, true);

		if (survey == null) {
			survey = surveyService.getSurvey(shortname, false, true, false, false, null, true, true);
		}

		if (survey == null) {
			Survey draft = surveyService.getSurvey(shortname, true, false, false, false, null, true, true);
			if (draft != null && !draft.getIsDeleted() && !draft.getArchived()) {

				return getEscapePageModel(draft, request, device);

			} else {
				throw new InvalidURLException();
			}
		}

		if (survey.getIsFrozen()) {
			throw new FrozenSurveyException();
		}

		try {

			// check for token
			Invitation invitation = attendeeService.getInvitationByUniqueId(token);
			if (invitation != null) {
				ParticipationGroup participationGroup = participationService.get(invitation.getParticipationGroupId());

				if (participationGroup != null && participationGroup.getSurveyUid().equals(survey.getUniqueId())) {
					return invitedPOST(participationGroup.getId().toString(), token, request, locale, device);
				}
			}

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		throw new ForbiddenURLException();
	}

	@PostMapping(value = "/invited/{group}/{unique}")
	public ModelAndView invitedPOST(@PathVariable String group, @PathVariable String unique, HttpServletRequest request,
			Locale locale, Device device) {
		try {
			String participationGroupId = request.getParameter("participationGroup");
			String invitationId = request.getParameter("invitation");
			String uniqueCode = request.getParameter(Constants.UNIQUECODE);

			ParticipationGroup participationGroup = participationService.get(Integer.parseInt(participationGroupId));
			Invitation invitation = attendeeService.getInvitation(Integer.parseInt(invitationId));

			Survey survey = surveyService.getSurveyByUniqueId(participationGroup.getSurveyUid(), false, false);

			if (survey == null) {
				survey = surveyService.getSurvey(participationGroup.getSurveyId(), false, true);
				// this is the base/draft survey, but we need the active one
				survey = surveyService.getSurvey(survey.getShortname(), false, true, false, false, null, true, true);
			}

			Attendee attendee = attendeeService.get(invitation.getAttendeeId());

			if (!survey.getIsDelphi() && invitation.getAnswers() > 0) {
				ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
				model.addObject(Constants.MESSAGE,
						resources.getMessage("error.InvitationUsed", null, "The invitation was already used.", locale));
				model.addObject("skipErrorLabel", true);
				AnswerSet aws = answerService.getByInvitationCode(invitation.getUniqueId());
				Date answerDate;
				if (aws != null) {
					answerDate = aws.getDate();
					String[] args = new String[] { ConversionTools.getFullString(answerDate) };
					model.addObject("messageComplement", resources.getMessage("message.SubmittedOn", args,
							"Submitted on: " + ConversionTools.getFullString(answerDate), locale));
				}
				return model;
			}
			
			if (SurveyHelper.isDeactivatedOrEndDateExceeded(survey, surveyService)) {
				return getEscapePageModel(survey, request, device);
			}

			if (SurveyHelper.isMaxContributionReached(survey, answerService)) {
				return getMaxAnswersReachedPageModel(survey, request, device);
			}

			String lang = locale.getLanguage();
			if (request.getParameter("language.code") != null && request.getParameter("language.code").length() == 2) {
				lang = request.getParameter("language.code");
			}

			if (!Tools.validUniqueCode(uniqueCode)) {
				return new ModelAndView("redirect:/errors/500.html");
			}

			User user = sessionService.getCurrentUser(request, false, false);

			if (!survey.getIsDelphi()) {
				ModelAndView err = testDraftAlreadySubmittedByUniqueCode(uniqueCode, locale);
				if (err != null)
					return err;
			}

			AnswerSet answerSet = answerService.automaticParseAnswerSet(request, survey, uniqueCode, false, lang, user);

			if (survey != null) {
				survey = surveyService.getSurvey(survey.getId(), lang);
			}

			String newlang = request.getParameter("newlang");
			String newlangpost = request.getParameter("newlangpost");
			String newcss = request.getParameter("newcss");
			String newviewpost = request.getParameter("newviewpost");

			Set<String> invisibleElements = new HashSet<>();
			Map<Element, String> validation = SurveyHelper.validateAnswerSet(answerSet, answerService,
					invisibleElements, resources, locale, request.getParameter("draftid"), request, false, user,
					fileService);

			if (newlangpost != null && newlangpost.equalsIgnoreCase("true")) {
				survey = surveyService.getSurvey(survey.getId(), newlang);
				Form f = new Form(survey, translationService.getActiveTranslationsForSurvey(survey.getId()),
						survey.getLanguage(), resources, contextpath);
				sessionService.setFormStartDate(request, f, uniqueCode);
				f.getAnswerSets().add(answerSet);
				f.setWcagCompliance(answerSet.getWcagMode() != null && answerSet.getWcagMode());

				ModelAndView model = new ModelAndView("runner/runner", "form", f);
				surveyService.initializeSkin(f.getSurvey());
				model.addObject("submit", true);
				model.addObject("runnermode", true);
				model.addObject(Constants.UNIQUECODE, uniqueCode);
				model.addObject("invitation", invitation.getId());
				model.addObject("participationGroup", participationGroup.getId());
				model.addObject("invisibleElements", invisibleElements);

				return model;
			} else if (newviewpost != null && newviewpost.equalsIgnoreCase("true")) {
				survey = surveyService.getSurvey(survey.getId(), newlang);
				Form f = new Form(survey, translationService.getActiveTranslationsForSurvey(survey.getId()),
						survey.getLanguage(), resources, contextpath);
				sessionService.setFormStartDate(request, f, uniqueCode);
				f.getAnswerSets().add(answerSet);
				if (newcss != null && newcss.equalsIgnoreCase("wcag")) {
					answerSet.setWcagMode(true);
				} else if (newcss != null && newcss.equalsIgnoreCase("standard")) {
					answerSet.setWcagMode(false);
				}
				f.setWcagCompliance(answerSet.getWcagMode() != null && answerSet.getWcagMode());
				ModelAndView model = new ModelAndView("runner/runner", "form", f);
				surveyService.initializeSkin(f.getSurvey());
				model.addObject("submit", true);
				model.addObject("runnermode", true);
				model.addObject(Constants.UNIQUECODE, uniqueCode);
				model.addObject("invitation", invitation.getId());
				model.addObject("participationGroup", participationGroup.getId());
				model.addObject("invisibleElements", invisibleElements);

				return model;
			}

			if (!validation.isEmpty()) {
				// load form
				Form f = new Form(survey, translationService.getActiveTranslationsForSurvey(survey.getId()),
						survey.getLanguage(), resources, contextpath);
				sessionService.setFormStartDate(request, f, uniqueCode);
				f.getAnswerSets().add(answerSet);
				f.setWcagCompliance(answerSet.getWcagMode() != null && answerSet.getWcagMode());

				f.setValidation(validation);

				ModelAndView model = new ModelAndView("runner/runner", "form", f);
				surveyService.initializeSkin(f.getSurvey());
				model.addObject("submit", true);
				model.addObject(Constants.UNIQUECODE, uniqueCode);
				model.addObject(Constants.MESSAGE, resources.getMessage("error.CheckValidation", null,
						"Please check for validation errors.", locale));
				model.addObject("invitation", invitation.getId());
				model.addObject("participationGroup", participationGroup.getId());

				SurveyHelper.recreateUploadedFiles(answerSet, survey, fileService, answerExplanationService);

				return model;
			}

			if (answerSet.getSurvey().getCaptcha() && !checkCaptcha(request)) {
				Form f = new Form(survey, translationService.getActiveTranslationsForSurvey(survey.getId()),
						survey.getLanguage(), resources, contextpath);
				sessionService.setFormStartDate(request, f, uniqueCode);
				f.getAnswerSets().add(answerSet);
				ModelAndView model = new ModelAndView("runner/runner", "form", f);
				surveyService.initializeSkin(f.getSurvey());
				model.addObject("submit", true);
				model.addObject(Constants.UNIQUECODE, uniqueCode);
				model.addObject("wrongcaptcha", "true");
				model.addObject("invitation", invitation.getId());
				model.addObject("participationGroup", participationGroup.getId());
				return model;
			}

			invitation.setAnswers(invitation.getAnswers() + 1);

			answerSet.setInvitationId(invitation.getUniqueId());

			String email = "";

			switch (participationGroup.getType()) {
			case ECMembers:
				EcasUser ecasUser = participationGroup.getEcasUser(invitation.getAttendeeId());
				if (ecasUser != null) {
					email = ecasUser.getEmail();
				}
				break;
			case Token:
				email = invitation.getUniqueId();
				break;
			default:
				if (attendee != null) {
					email = attendee.getEmail();
				}
			}

			if (email != null) {
				if (survey.isAnonymous()) {
					answerSet.setResponderEmail(Tools.md5hash(email));
				} else {
					answerSet.setResponderEmail(email);
				}
			}

			boolean hibernateOptimisticLockingFailureExceptionCatched = false;

			try {
				saveAnswerSet(answerSet, fileDir, request.getParameter("draftid"), -1, request);
				attendeeService.update(invitation);
			} catch (HibernateOptimisticLockingFailureException | ConstraintViolationException he) {
				logger.info(he.getLocalizedMessage(), he);
				hibernateOptimisticLockingFailureExceptionCatched = true;
			}

			if (hibernateOptimisticLockingFailureExceptionCatched) {
				Form f = new Form(survey, translationService.getActiveTranslationsForSurvey(survey.getId()),
						survey.getLanguage(), resources, contextpath);
				sessionService.setFormStartDate(request, f, uniqueCode);
				f.getAnswerSets().add(answerSet);
				ModelAndView model = new ModelAndView("runner/runner", "form", f);
				surveyService.initializeSkin(f.getSurvey());
				model.addObject("submit", true);
				model.addObject(Constants.UNIQUECODE, uniqueCode);
				model.addObject("invitation", invitation.getId());
				model.addObject("participationGroup", participationGroup.getId());
				model.addObject("holf", "true");
				return model;
			}

			if (survey.getIsQuiz()) {
				ModelAndView result = new ModelAndView("runner/quizResult", Constants.UNIQUECODE, answerSet.getUniqueCode());
				Form form = new Form(resources, surveyService.getLanguage(locale.getLanguage().toUpperCase()),
						translationService.getActiveTranslationsForSurvey(answerSet.getSurvey().getId()), contextpath);
				sessionService.setFormStartDate(request, form, uniqueCode);
				form.setSurvey(survey);
				form.getAnswerSets().add(answerSet);
				result.addObject(form);
				result.addObject("surveyprefix", survey.getId());
				result.addObject("quiz", QuizHelper.getQuizResult(answerSet, invisibleElements));
				result.addObject("isquizresultpage", true);
				result.addObject("invisibleElements", invisibleElements);
				return result;
			}

			ModelAndView result = new ModelAndView("thanks", Constants.UNIQUECODE, answerSet.getUniqueCode());
			
			if (survey.getIsECF()) {
				result.addObject("isECF", true);
				Set<ECFProfile> profiles = this.ecfService.getECFProfiles(survey);
				result.addObject("ecfProfiles", profiles.stream().sorted().collect(Collectors.toList()));
				result.addObject("ecfIndividualResult", this.ecfService.getECFIndividualResult(survey, answerSet));
			}
			
			if (survey.getSendConfirmationEmail() && email != null && email.contains("@")) {
				surveyService.sendNotificationEmail(survey, answerSet, email);
				result.addObject("notificationemailtext", surveyService.getNotificationEmailText(survey, email, locale));
			}
			
			survey = surveyService.getSurvey(survey.getId(), lang);

			if (!survey.isAnonymous() && answerSet.getResponderEmail() != null) {
				result.addObject("participantsemail", answerSet.getResponderEmail());
			}

			result.addObject("runnermode", true);

			Form form = new Form(resources, surveyService.getLanguage(lang),
					translationService.getActiveTranslationsForSurvey(survey.getId()), contextpath);
			form.setSurvey(survey);			

			if (survey.getIsOPC()) {
				result.addObject("opcredirection", form.getFinalConfirmationLink(opcredirect, lang, answerSet));
			}

			if(!survey.getConfirmationPageLink()){
				form.getAnswerSets().add(answerSet);
			}

			result.addObject("form", form);
			result.addObject("text", survey.getConfirmationPage());
			if (survey.getConfirmationPageLink() != null && survey.getConfirmationPageLink()
					&& survey.getConfirmationLink() != null && survey.getConfirmationLink().length() > 0) {
				result.addObject("redirect", form.getFinalConfirmationLink(lang, answerSet));
			}
			result.addObject("isthankspage", true);
			result.addObject("surveyprefix", survey.getId());

			return result;

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return null;
	}

	@RequestMapping(value = "/{shortname}/{token}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView runnerToken(@PathVariable String shortname, @PathVariable String token,
			HttpServletRequest request, Locale locale, Device device)
			throws InvalidURLException, ForbiddenURLException, FrozenSurveyException {
		
		if (token.length() > 0 && !Tools.isUUID(token)) {
			throw new ForbiddenURLException();
		}
		
		Survey survey = surveyService.getSurvey(shortname, false, true, false, false, null, true, true);
	
		if (survey == null) {
			Survey draft = surveyService.getSurvey(shortname, true, false, false, false, null, true, true);
			if (draft != null && !draft.getIsDeleted() && !draft.getArchived()) {
				if (draft.getIsFrozen()) {
					throw new FrozenSurveyException();
				}	
				return getEscapePageModel(draft, request, device);

			} else {
				throw new InvalidURLException();
			}
		}
		
		if (survey.getIsFrozen()) {
			throw new FrozenSurveyException();
		}	

		try {

			// check for token
			Invitation invitation = attendeeService.getInvitationByUniqueId(token);
			if (invitation != null) {
				ParticipationGroup participationGroup = participationService.get(invitation.getParticipationGroupId());

				if (participationGroup != null && participationGroup.getSurveyUid().equals(survey.getUniqueId())) {
					return invited(participationGroup.getId().toString(), token, request, locale, survey.getId(),
							device);
				} else {
					ModelAndView model = new ModelAndView("error/generic");
					model.addObject("message", resources.getMessage("error.AccessDisabled", null, "The access has been disabled.", locale));
					model.addObject("noMenu", true);
					model.addObject("runnermode", true);
					return model;
				}
			}

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		throw new ForbiddenURLException();
	}

	@RequestMapping(value = "/preparesurvey/{id}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView preparesurvey(@PathVariable String id, HttpServletRequest request, Locale locale) {

		Survey survey = surveyService.getSurvey(Integer.parseInt(id), false, true);

		if (survey != null) {

			Form f = new Form(survey, translationService.getTranslationsForSurvey(survey.getId(), true),
					survey.getLanguage(), resources, contextpath);

			String lang = request.getParameter("lang");

			if (lang != null && !f.getLanguage().getCode().equalsIgnoreCase(lang)) {
				if (f.translationIsValid(lang)) {
					Survey translated = SurveyHelper.createTranslatedSurvey(f.getSurvey().getId(), lang, surveyService,
							translationService, true);
					f.setSurvey(translated);
					f.setLanguage(surveyService.getLanguage(lang));
				}
			}

			SurveyHelper.calcTableWidths(f.getSurvey(), f);

			ModelAndView model = new ModelAndView("runner/runner", "form", f);
			surveyService.initializeSkin(f.getSurvey());
			model.addObject("action", "pdf");
			model.addObject("forpdf", "true");
			f.setForPDF(true);
			return model;

		}

		return null;
	}

	@RequestMapping(value = "/contactform/{uidorshortname}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView contactform(@PathVariable String uidorshortname, HttpServletRequest request,
			HttpServletResponse response, Locale locale, Device device) throws InvalidURLException {
		Survey survey = surveyService.getSurvey(uidorshortname, false, true, false, false, null, true, true);

		if (survey == null) {
			survey = surveyService.getSurvey(uidorshortname, true, true, false, false, null, true, true);
		}

		if (survey == null || !survey.getContact().startsWith("form:")) {
			throw new InvalidURLException();
		}

		ModelAndView model = new ModelAndView("runner/contactForm", Constants.SURVEY, survey);
		model.addObject("USER", request.getSession().getAttribute("USER"));
		return model;

	}

	@PostMapping(value = "/contactform/{uidorshortname}")
	public String contactformPOST(@PathVariable String uidorshortname, HttpServletRequest request, ModelMap model)
			throws Exception {
		Survey survey = surveyService.getSurvey(uidorshortname, false, true, false, false, null, true, true);

		if (survey == null) {
			survey = surveyService.getSurvey(uidorshortname, true, true, false, false, null, true, true);
		}

		if (survey == null || !survey.getContact().startsWith("form:")) {
			throw new InvalidURLException();
		}

		if (!checkCaptcha(request)) {
			model.put("contactFormReason", request.getParameter("contactreason"));
			model.put("contactFormName", request.getParameter("name"));
			model.put("contactFormMail", request.getParameter(Constants.EMAIL));
			model.put("contactFormSubject", request.getParameter("subject"));
			model.put("contactFormMessage", request.getParameter(Constants.MESSAGE));
			model.put("survey", survey);
			model.put("wrongcaptcha", true);
			return "runner/contactForm";
		}

		String reason = ConversionTools.removeHTML(request.getParameter("contactreason"), true);
		String name = ConversionTools.removeHTML(request.getParameter("name"), true);
		String email = ConversionTools.removeHTML(request.getParameter(Constants.EMAIL), true);
		String subject = ConversionTools.removeHTML(request.getParameter("subject"), true);
		String message = ConversionTools.removeHTML(request.getParameter(Constants.MESSAGE), true);
		String[] uploadedfiles = request.getParameterValues("uploadedfile");

		StringBuilder body = new StringBuilder();
		body.append("You have received a message from the Contact Form for the survey below<br/><hr /><br />");

		body.append("<table>");

		String link = serverPrefix + "runner/" + survey.getShortname();

		body.append("<tr><td>Published survey link:</td><td><a href='").append(link).append("'>").append(link)
				.append("</a></td></tr>");
		body.append("<tr><td>&nbsp;</td><td>&nbsp;</td></tr>");

		body.append("<tr><td>Alias:</td><td>").append(survey.getShortname()).append("</td></tr>");
		body.append("<tr><td>Name:</td><td>").append(survey.cleanTitle()).append("</td></tr>");
		body.append("<tr><td>&nbsp;</td><td>&nbsp;</td></tr>");
		body.append("<tr><td colspan='2'><hr /></td></tr>");
		body.append("<tr><td>&nbsp;</td><td>&nbsp;</td></tr>");

		body.append("<tr><td>Contact Reason:</td><td>").append(reason).append("</td></tr>");
		body.append("<tr><td>&nbsp;</td><td>&nbsp;</td></tr>");

		body.append("<tr><td>Name:</td><td>").append(name).append("</td></tr>");
		body.append("<tr><td>Email address:</td><td>").append(email).append("</td></tr>");
		body.append("<tr><td>Subject:</td><td>").append(subject).append("</td></tr>");

		body.append("</table>");

		body.append("<br />Message text:<br />").append(message).append("<br /><br />");

		InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
		String text = IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", body).replace("[HOST]", serverPrefix);

		java.io.File attachment1 = null;
		java.io.File attachment2 = null;
		if (uploadedfiles != null) {
			if (uploadedfiles.length > 0) {
				attachment1 = fileService.getTemporaryFile(uploadedfiles[0]);
				if (uploadedfiles.length > 1) {
					attachment2 = fileService.getTemporaryFile(uploadedfiles[1]);
				}
			}
		}

		String owneremail = survey.getContact().replace("form:", "");
		mailService.SendHtmlMail(owneremail, sender, sender, subject, text, attachment1, attachment2, null, true);

		model.put("messagesent", true);
		model.put("survey", survey);
		return "runner/contactForm";
	}

	@RequestMapping(value = "/{uidorshortname}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView runner(@PathVariable String uidorshortname, HttpServletRequest request,
			HttpServletResponse response, Locale locale, Device device)
			throws InvalidURLException, WeakAuthenticationException, FrozenSurveyException,
			NotAgreedToTosException, NotAgreedToPsException {

		ModelAndView modelReturn = new ModelAndView();
		boolean internalUsersOnly = false;
		boolean readonlyMode = false;

		if (isShowEcas())
			modelReturn.getModelMap().put("showecas", true);
		if (isCasOss())
			modelReturn.getModelMap().put("casoss", true);

		if (request.getRequestURL().toString().endsWith(Constants.PATH_DELIMITER)) {
			modelReturn.setViewName("redirect:/runner/" + uidorshortname);
			return modelReturn;
		}

		boolean isDraft = request.getParameter("draft") != null
				&& request.getParameter("draft").equalsIgnoreCase("true");

		String p = request.getParameter("readonly");
		if (p != null && p.equalsIgnoreCase("true")) {
			p = request.getParameter("draftid");
			if (p != null && p.length() > 0) {
				User user = sessionService.getCurrentUser(request, false, true);
				if (user != null && user.getFormPrivilege() > 1) {
					readonlyMode = true;
				}
			}
		}
		String lang = request.getParameter("surveylanguage");

		Survey survey = surveyService.getSurvey(uidorshortname, isDraft, true, false, true, lang, true, true);

		if (survey == null && readonlyMode) {
			survey = surveyService.getSurvey(uidorshortname, isDraft, false, false, true, lang, true, true);
		}

		if (survey == null && !isDraft) {
			Survey draft = surveyService.getSurvey(uidorshortname, true, false, false, false, null, true, true);
			if (draft != null && !draft.getIsDeleted() && !draft.getArchived()) {
				if (draft.getIsFrozen()) {
					throw new FrozenSurveyException();
				}				
				return getEscapePageModel(draft, request, device);
			} else {
				throw new InvalidURLException();
			}
		}

		if (survey != null) {
			if (SurveyHelper.isDeactivatedOrEndDateExceeded(survey, surveyService) && !readonlyMode) {
				return getEscapePageModel(survey, request, device);
			}

			String urllang = request.getParameter("surveylanguage");
			if (urllang != null && urllang.length() > 0) {
				boolean validTranslationFound = false;
				List<Translations> translations = translationService.getTranslationsForSurvey(survey.getId(), true);
				for (Translations trans : translations) {
					if (trans.getLanguage().getCode().equalsIgnoreCase(urllang) && trans.getComplete()
							&& trans.getActive()) {
						validTranslationFound = true;
						break;
					}
				}

				if (!validTranslationFound) {
					modelReturn.setViewName("redirect:/runner/" + uidorshortname);
					return modelReturn;
				}
			}

			if (SurveyHelper.isMaxContributionReached(survey, answerService)) {
				return getMaxAnswersReachedPageModel(survey, request, device);
			}
			
			if (survey.getSecurity().startsWith("secured")) {
				if (survey.getEcasSecurity()) {
					try {
						// if passwords are also permitted we have to keep the session due to CSRF
						if (survey.getPassword() == null || survey.getPassword().trim().length() == 0) {
							// If not logged in for runner: auto logout
							if (request.getSession().getAttribute("RUNNER_LOGIN") == null && survey.getIsEVote()) {
								request.getSession().invalidate();
							}
						}						
						
						User user = sessionService.getCurrentUser(request, false, false);

						boolean ecasauthenticated = request.getSession().getAttribute("ECASSURVEY") != null && request
								.getSession().getAttribute("ECASSURVEY").toString().startsWith(uidorshortname);

						if (readonlyMode
								|| (user != null && user.getType().equalsIgnoreCase(User.ECAS) && ecasauthenticated)) {
							// if the user already submitted, show error page
							int contributionsCount = answerService.userContributionsToSurvey(survey, user);
							if (contributionsCount > 0 && (survey.getAllowedContributionsPerUser() == 1
									|| survey.getAllowedContributionsPerUser() <= contributionsCount)) {
								
								if (survey.getIsDelphi())
								{
									AnswerSet aws = answerService.getUserContributionToSurvey(survey, user);
									return new ModelAndView("redirect:/editcontribution/" + aws.getUniqueCode());
								}
								
								request.getSession().removeAttribute("ECASSURVEY");
								modelReturn.setViewName(Constants.VIEW_ERROR_GENERIC);
								modelReturn.addObject("runnermode", true);

								if (survey.getAllowedContributionsPerUser() == 1) {
									modelReturn.addObject(Constants.MESSAGE, resources.getMessage("error.UserAlreadySubmitted",
											null,
											"This account has already been used to submit a contribution. Multiple submission is prohibited.",
											locale));
								} else {
									modelReturn.addObject(Constants.MESSAGE, resources.getMessage("error.UserAlreadySubmitted2",
											null,
											"You have submitted the maximum number of contributions this survey allows. Further contributions are not allowed.",
											locale));
								}

								return modelReturn;
							}

							if (readonlyMode
									|| (survey.getEcasMode() != null && survey.getEcasMode().equalsIgnoreCase("all"))) {
								return loadSurvey(survey, request, locale, uidorshortname, false, readonlyMode);
							} else {
								if (user.getGlobalPrivileges().get(GlobalPrivilege.ECAccess) > 0) {
									return loadSurvey(survey, request, locale, uidorshortname, false, readonlyMode);
								} else {
									internalUsersOnly = true;
								}
							}
						}

					} catch (NotAgreedToTosException e) {
						// ignore
					}
				}

				String uniqueCode = (String) request.getSession().getAttribute(Constants.UNIQUECODE);

				if (uniqueCode == null || !validCodesService.checkValid(uniqueCode, survey.getUniqueId())) {
					modelReturn.setViewName("runner/surveyLogin");
					modelReturn.addObject(Constants.SHORTNAME, uidorshortname);
					modelReturn.addObject("surveyname", survey.cleanTitle());
					
					if (survey.getIsOPC()) {
						modelReturn.addObject("allowIndex", true);
					}

					if (survey.getContact().startsWith("form:")) {
						modelReturn.addObject("contact", true);
					}

					if (lang != null && lang.length() > 0) {
						modelReturn.addObject("lang", lang);
					}

					if (internalUsersOnly) {
						modelReturn.addObject("internalUsersOnly", true);
					}

					String draftid = request.getParameter("draftid");

					if (survey.getEcasSecurity()) {
						modelReturn.addObject("ecasurl", ecashost);
						modelReturn.addObject("require2fa", require2fa);
						modelReturn.addObject("ecassecurity", true);

						String serviceurl = serverPrefix + "auth/surveylogin?survey=" + uidorshortname;
						if (draftid != null && draftid.length() > 0) {
							serviceurl += "?draftid=" + draftid;
						}
						if (lang != null && lang.length() > 0) {
							serviceurl += "?surveylanguage=" + lang;
						}

						modelReturn.addObject("serviceurl", serviceurl);

						if (survey.getPassword() == null || survey.getPassword().trim().length() == 0) {
							modelReturn.addObject("hidepassword", true);
						}
					}

					modelReturn.addObject("draftid", draftid);
					return modelReturn;
				}
			}
		}

		return loadSurvey(survey, request, locale, uidorshortname, false, readonlyMode);
	}

	private ModelAndView getEscapePageModel(Survey survey, HttpServletRequest request, Device device,
			Boolean escapeOrMaxReachedAnswers) {
		if (escapeOrMaxReachedAnswers && survey.getEscapePageLink() != null && survey.getEscapePageLink()
				&& survey.getEscapeLink() != null && survey.getEscapeLink().length() > 0) {
			return new ModelAndView("redirect:" + survey.getEscapeLink());
		} else {
			ModelAndView model;
			if ((device.isMobile() || device.isTablet())
					&& (enableresponsive != null && enableresponsive.equalsIgnoreCase("true"))) {
				model = new ModelAndView("escaperesponsive");
			} else {
				model = new ModelAndView("escape");
			}

			Form f = new Form(survey, translationService.getActiveTranslationsForSurvey(survey.getId()),
					survey.getLanguage(), resources, contextpath);

			String lang = request.getParameter("surveylanguage");

			if (lang != null && f.translationIsValid(lang)) {
				survey = SurveyHelper.createTranslatedSurvey(f.getSurvey().getId(), lang, surveyService,
						translationService, true);
				f.setSurvey(survey);
				f.setLanguage(surveyService.getLanguage(lang));
			}

			String wcag = request.getParameter("wcag");
			if (wcag != null && wcag.equalsIgnoreCase("enabled")) {
				f.setWcagCompliance(true);
			} else if (wcag != null && wcag.equalsIgnoreCase("disabled")) {
				f.setWcagCompliance(false);
			}

			if (escapeOrMaxReachedAnswers) {
				model.addObject("text", survey.getEscapePage());
			} else {

				if (survey.getIsUseMaxNumberContributionLink() && survey.getMaxNumberContributionLink() != null
						&& survey.getMaxNumberContributionLink().length() > 0) {
					return new ModelAndView("redirect:" + survey.getMaxNumberContributionLink());
				}

				model.addObject("text", survey.getMaxNumberContributionText());
			}

			model.addObject("form", f);
			model.addObject("runnermode", true);
			model.addObject("escapemode", true);

			List<Integer> publishedSurveys = surveyService.getAllPublishedSurveyVersions(survey.getId());
			if (!publishedSurveys.isEmpty()) {
				model.addObject("oncepublished", true);
			}

			return model;
		}
	}

	private ModelAndView getEscapePageModel(Survey survey, HttpServletRequest request, Device device) {
		return this.getEscapePageModel(survey, request, device, true);
	}

	private ModelAndView getMaxAnswersReachedPageModel(Survey survey, HttpServletRequest request, Device device) {
		return this.getEscapePageModel(survey, request, device, false);
	}

	private ModelAndView loadSurvey(Survey survey, HttpServletRequest request, Locale locale, String action,
			boolean passwordauthenticated, boolean readonlyMode) throws WeakAuthenticationException {
		if (survey != null) {
			String draftid = request.getParameter("draftid");

			if (draftid != null) {
				try {
					Draft draft = answerService.getDraft(draftid);
					ModelAndView err = testDraftAlreadySubmitted(draft, locale);
					if (err != null)
						return err;
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}
			} else if (survey.getEcasSecurity()) {
				try {
					draftid = answerService.getDraftForEcasLogin(survey, request);
				} catch (NotAgreedToTosException e) {
					// ignore
				} catch (NotAgreedToPsException e) {
					// ignore
				}
			}

			Form f = new Form(survey, translationService.getActiveTranslationsForSurvey(survey.getId()),
					survey.getLanguage(), resources, contextpath);
			String lang = request.getParameter("surveylanguage");

			if (lang != null) {

				if (f.translationIsValid(lang)) {
					Survey translated = SurveyHelper.createTranslatedSurvey(f.getSurvey().getId(), lang, surveyService,
							translationService, true);
					f.setSurvey(translated);
					f.setLanguage(surveyService.getLanguage(lang));
				} else {
					lang = null;
				}
			}

			String wcag = request.getParameter("wcag");
			if (wcag != null && wcag.equalsIgnoreCase("enabled")) {
				f.setWcagCompliance(true);
			} else if (wcag != null && wcag.equalsIgnoreCase("disabled")) {
				f.setWcagCompliance(false);
			}

			ModelAndView model = new ModelAndView("runner/runner", "form", f);
			surveyService.initializeSkin(f.getSurvey());

			model.addObject("submit", !readonlyMode);
			model.addObject("action", action);
			model.addObject("runnermode", true);
			model.addObject("readonlyMode", readonlyMode);

			if (passwordauthenticated) {
				model.addObject("passwordauthenticated", true);
			}

			// this code will be used as an identifier (for uploaded files etc)
			String uniqueCode = UUID.randomUUID().toString();
			if (survey.getIsDelphi() && request.getParameter("startDelphi") != null) {
				String originalUniqueCode = request.getParameter("originalUniqueCode");
				if (originalUniqueCode != null)
				{
					uniqueCode = originalUniqueCode;
				}
			} else if (survey.getTimeLimit().length() > 0) {
				String oldUniqueCode = (String)request.getSession().getAttribute(Constants.UNIQUECODE + survey.getId());
				if (oldUniqueCode != null) {
					uniqueCode = oldUniqueCode;
				}
			}

			if (draftid != null && draftid.trim().length() > 0) {
				try {
					Draft draft = answerService.getDraft(draftid);

					if (draft == null) {
						model.addObject(Constants.MESSAGE, resources.getMessage("error.DraftIDInvalid", null,
								"The given draft ID is not valid!", locale));
					} else {
						f.getAnswerSets().add(draft.getAnswerSet());
						f.setWcagCompliance(
								draft.getAnswerSet().getWcagMode() != null && draft.getAnswerSet().getWcagMode());

						SurveyHelper.recreateUploadedFiles(draft.getAnswerSet(), survey, fileService, answerExplanationService);
						uniqueCode = draft.getAnswerSet().getUniqueCode();
						model.addObject("draftid", draftid);

						if (lang == null) {
							lang = draft.getAnswerSet().getLanguageCode();
							Survey translated = SurveyHelper.createTranslatedSurvey(f.getSurvey().getId(), lang,
									surveyService, translationService, true);
							f.setSurvey(translated);
							f.setLanguage(surveyService.getLanguage(lang));
						}
						validCodesService.revalidate(uniqueCode, survey);
						Set<String> invisibleElements = new HashSet<>();
						SurveyHelper.validateAnswerSet(draft.getAnswerSet(), answerService, invisibleElements,
								resources, locale, null, null, true, null, fileService);
						model.addObject("invisibleElements", invisibleElements);
					}
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}
			} else {
				if (survey.getIsQuiz() && request.getParameter("startQuiz") == null) {
					model = new ModelAndView("runner/quiz", "form", f);
					model.addObject("isquizpage", true);
					model.addObject("runnermode", true);
				} else if (survey.getIsQuiz()) {
					// the start date for the time limit starts when the user left the quiz start page
					sessionService.SetUniqueCodeForForm(request, survey.getId(), uniqueCode);
					sessionService.setFormStartDate(request, f, uniqueCode);
				} else if (survey.getIsDelphi() && survey.getIsDelphiShowStartPage() && request.getParameter("startDelphi") == null) {
					model = new ModelAndView("runner/delphi", "form", f);
					model.addObject("isdelphipage", true);
					model.addObject("runnermode", true);
					
					String originalUniqueCode = request.getParameter("originalUniqueCode");
					if (originalUniqueCode != null)
					{
						uniqueCode = originalUniqueCode;
					}
				}

				validCodesService.add(uniqueCode, survey);
			}

			request.getSession().setAttribute(Constants.UNIQUECODE, uniqueCode);
			
			if (survey.getIsOPC()) {
				model.addObject("allowIndex", true);
			}
					
			model.addObject(Constants.UNIQUECODE, uniqueCode);
			return model;
		}

		ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
		model.addObject(Constants.MESSAGE,
				resources.getMessage("error.WrongURL", null, "The url you entered was not correct.", locale));
		return model;
	}

	@RequestMapping(value = "/delete/{id}/{uniqueCode}/{surveyUID}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody String delete(@PathVariable String id, @PathVariable String uniqueCode,
			@PathVariable String surveyUID, HttpServletRequest request) {
		try {

			String fileName = request.getParameter("fileName");
			fileName = Ucs2Utf8.unconvert(fileName, request.getCharacterEncoding());
			if (fileName.contains(Constants.PATH_DELIMITER) || fileName.contains("\\") || fileName.contains("*")) {
				throw new ValidationException("Invalid file name: " + fileName, "Invalid file name: " + fileName);
			}

			final int questionId = Integer.parseInt(id);
			final Element question = surveyService.getElement(questionId);
			final String questionUid = question.getUniqueId();
			final Survey survey = surveyService.getSurveyByUniqueId(surveyUID, false, true);
			final boolean isDelphi = survey.getIsDelphi() && (question != null) && (question.isDelphiElement());

			File parentDirectory = null;
			if (isDelphi) {
				final com.ec.survey.model.survey.base.File fileInDatabase = answerExplanationService.getExplanationFile(
						survey.getUniqueId(), uniqueCode, questionUid, fileName);
				if (fileInDatabase != null) {
					final AnswerSet answerSet = answerService.get(uniqueCode);
					answerExplanationService.removeFileFromExplanation(answerSet.getId(), questionUid,
							fileInDatabase.getUid());
					fileService.deleteFileFromDiskAndDatabase(survey.getUniqueId(), fileInDatabase);
				}
				parentDirectory = fileService.deleteUploadedExplanationFileAndReturnParentDirectoryIfSuccessful(
						survey.getUniqueId(), uniqueCode, questionUid, fileName);
			} else {
				final String validFileName = validateDeleteParameters(questionId, uniqueCode, fileName, surveyUID);
				final File file = new File(validFileName);
				if (file.exists() && file.delete()) {
					parentDirectory = file.getParentFile();
				}
			}

			if (parentDirectory != null) {
				String files = getFiles(parentDirectory);
				return "{\"success\": true, \"files\": [" + files + "]}";
			} else {
				return "{\"success\": false}";
			}
		} catch (org.owasp.esapi.errors.ValidationException vex) {
			return "{\"success\": false}";
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}

		return "{\"success\": false}";
	}

	private String validateDeleteParameters(int questionId, String uniqueCode, String fileName, String surveyUID)
			throws ValidationException, IOException {

		Validator validator = ESAPI.validator();

		java.io.File basePath = fileService.getSurveyUploadsFolder(surveyUID, false);
		String folderPath = basePath + Constants.PATH_DELIMITER + uniqueCode + Constants.PATH_DELIMITER + questionId;
		String canonicalPath = new File(folderPath).getCanonicalPath();
		boolean validDirectoryPath = validator.isValidDirectoryPath(
				"check directory path in RunnerController.delete method", canonicalPath,
				basePath, false);
		if (!validDirectoryPath) {
			throw new ValidationException("Invalid folder path: " + folderPath, "Invalid folder path: " + folderPath);
		}
		return folderPath + Constants.PATH_DELIMITER + fileName;
	}

	@PostMapping(value = "/upload/{id}/{uniqueCode}")
	public void upload(@PathVariable String id, @PathVariable String uniqueCode, HttpServletRequest request,
			HttpServletResponse response) {

		PrintWriter writer = null;
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			writer = response.getWriter();
		} catch (IOException ex) {
			logger.error(ex.getLocalizedMessage(), ex);
		}

		String filename;
		java.io.File tempFile = null;

		try {
			if (!Tools.isUUID(uniqueCode)) {
				throw new MessageException("invalid unique code");
			}

			if (!Tools.isInteger(id)) {
				throw new MessageException("invalid id");
			}

			if (request instanceof DefaultMultipartHttpServletRequest) {
				DefaultMultipartHttpServletRequest r = (DefaultMultipartHttpServletRequest) request;
				filename = r.getFile("qqfile").getOriginalFilename();
				is = r.getFile("qqfile").getInputStream();
			} else {
				filename = request.getHeader("X-File-Name");
				is = request.getInputStream();
			}

			filename = FileUtils.cleanFilename(java.net.URLDecoder.decode(filename, "UTF-8"));

			boolean wrongextension = false;

			Element element = surveyService.getElement(Integer.parseInt(id));
			if (element instanceof Upload) {
				Upload upload = (Upload) element;
				if (upload.getExtensions() != null && upload.getExtensions().length() > 0) {
					List<String> extensions = Arrays.asList(upload.getExtensions().split(";"));
					String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
					if (!extensions.contains(extension)) {
						wrongextension = true;
					}
				}
			}

			String surveyuid = request.getParameter(Constants.SURVEY);

			if (surveyuid == null) {
				Survey survey = surveyService.getSurveyForQuestion(element.getUniqueId());
				surveyuid = survey.getUniqueId();
			}

			tempFile = fileService.getLocalTemporaryFile();
			fos = new FileOutputStream(tempFile);
			IOUtils.copy(is, fos);
			fos.close();

			java.io.File folder;
			java.io.File directory;
			if ((element instanceof Question) && (((Question)element).getIsDelphiQuestion())) {
				folder = fileService.getSurveyExplanationUploadsFolder(surveyuid, false);
				directory = new java.io.File(String.format("%s/%s/%s", folder.getPath(), uniqueCode, element.getUniqueId()));

			} else {
				folder = fileService.getSurveyUploadsFolder(surveyuid, false);
				directory = new java.io.File(String.format("%s/%s/%s", folder.getPath(), uniqueCode, id));
			}
			
			// we try 3 times to create the folders
			boolean error = false;
			if (!directory.exists() && !directory.mkdirs() && !directory.exists() && !directory.mkdirs()
					&& !directory.exists() && !directory.mkdirs()) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				writer.print("{\"success\": false}");
				logger.error("not possible to create folder: " + directory.getPath());
				error = true;
			}

			if (!error) {
				if (!wrongextension) {
					java.io.File file = new java.io.File(directory.getPath() + Constants.PATH_DELIMITER + filename);

					int counter = 0;

					while (!error) {
						try {
							org.apache.commons.io.FileUtils.copyFile(tempFile, file);
							break;
						} catch (IOException e) {
							counter++;
							if (counter > 4) {
								response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
								writer.print("{\"success\": false}");
								logger.error("not possible to copy file to: " + file.getPath());
								error = true;
							} else {
								logger.error(
										"not possible to copy file to: " + file.getPath() + " - attempt " + counter);
							}
						}
					}
				}

				if (!error) {
					String files = getFiles(directory);
					response.setStatus(HttpServletResponse.SC_OK);
					response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");
					writer.print("{\"success\": true, \"files\": [" + files + "], \"wrongextension\": " + wrongextension
							+ "}");
				}
			}
		} catch (Exception ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			writer.print("{\"success\": false}");
			logger.error(ex.getMessage(), ex);
		} finally {
			try {
				if (fos != null)
					fos.close();
				is.close();
			} catch (IOException ignored) {
				// ignore
			}

			if (tempFile != null) {
				tempFile.delete();
			}
		}

		writer.flush();
		writer.close();
	}

	private String getFiles(java.io.File directory) {
		StringBuilder files = new StringBuilder();

		File[] listFiles = directory.listFiles();
		if (listFiles != null) {
			for (java.io.File f : listFiles) {
				if (files.length() > 0)
					files.append(",");
				files.append("\"").append(f.getName()).append("\"");
			}
		}
		return files.toString();
	}

	@PostMapping(value = "/draft/{mode}")
	public ModelAndView processDraftSubmit(@PathVariable String mode, HttpServletRequest request, Locale locale,
			HttpServletResponse response, Device device) {
		try {
			boolean hibernateOptimisticLockingFailureExceptionCatched = false;

			String participationGroupId = request.getParameter("participationGroup");
			String invitationId = request.getParameter("invitation");

			Survey survey = surveyService.getSurvey(Integer.parseInt(request.getParameter("survey.id")), false, true);
			String uniqueCode = request.getParameter(Constants.UNIQUECODE);

			ModelAndView err = testDraftAlreadySubmittedByUniqueCode(uniqueCode, locale);
			if (err != null)
				return err;

			if (!Tools.validUniqueCode(uniqueCode)) {
				return new ModelAndView("redirect:/errors/500.html");
			}

			String uid = UUID.randomUUID().toString();

			AnswerSet answerSet = null;

			String lang = locale.getLanguage();
			if (request.getParameter("language.code") != null && request.getParameter("language.code").length() == 2) {
				lang = request.getParameter("language.code");
			}

			if (!mode.equalsIgnoreCase("preview")) {
				User user = sessionService.getCurrentUser(request, false, false);

				String draftid = request.getParameter("draftid");
				Draft draft = null;
				if (draftid != null) {
					draft = answerService.getDraft(draftid);
					if (draft != null) {
						SurveyHelper.parseAndMergeAnswerSet(request, survey, uniqueCode, draft.getAnswerSet(),
								lang, user, fileService);
						draft.getAnswerSet().setIsDraft(true); // this also sets the
																// ISDRAFT flag of
																// the answers
																// inside the
																// answerset
						uid = draft.getUniqueId();
					}
				}

				if (draft == null && invitationId != null && invitationId.trim().length() > 0) {
					Invitation invitation = attendeeService.getInvitation(Integer.parseInt(invitationId));
					draft = answerService.getDraftForInvitation(invitation.getUniqueId());
					if (draft != null) {
						SurveyHelper.parseAndMergeAnswerSet(request, survey, uniqueCode, draft.getAnswerSet(),
								lang, user, fileService);
						draft.getAnswerSet().setIsDraft(true);
						uid = draft.getUniqueId();
					}
				}

				if (draft == null) {
					draft = answerService.getDraftByAnswerUID(uniqueCode);
					if (draft != null) {
						SurveyHelper.parseAndMergeAnswerSet(request, survey, uniqueCode, draft.getAnswerSet(),
								lang, user, fileService);
						draft.getAnswerSet().setIsDraft(true);
						uid = draft.getUniqueId();
					}
				}

				if (draft == null) {
					draft = new Draft();
					draft.setUniqueId(uid);

					answerSet = SurveyHelper.parseAnswerSet(request, survey, uniqueCode, false, lang, user,
							fileService);
					answerSet.setIsDraft(true);
					draft.setAnswerSet(answerSet);
				}

				if (invitationId != null && invitationId.trim().length() > 0) {
					Invitation invitation = attendeeService.getInvitation(Integer.parseInt(invitationId));
					uid = invitation.getUniqueId();

					if (invitation.getAttendeeId() != 0) {

						ParticipationGroup group = participationService.get(Integer.parseInt(participationGroupId));

						if (group.getType() == ParticipationGroupType.ECMembers) {
							EcasUser ecasUser = group.getEcasUser(invitation.getAttendeeId());

							if (survey.isAnonymous()) {
								draft.getAnswerSet().setResponderEmail(Tools.md5hash(ecasUser.getEmail()));
							} else {
								draft.getAnswerSet().setResponderEmail(ecasUser.getEmail());
							}
						} else {

							Attendee attendee = attendeeService.get(invitation.getAttendeeId());

							if (attendee != null) {
								if (survey.isAnonymous()) {
									draft.getAnswerSet().setResponderEmail(Tools.md5hash(attendee.getEmail()));
								} else {
									draft.getAnswerSet().setResponderEmail(attendee.getEmail());
								}
							} else {
								logger.error("Attendee " + invitation.getAttendeeId() + " referenced by invitation "
										+ invitation.getId() + " not found!");
							}
						}
					}

					draft.getAnswerSet().setInvitationId(invitation.getUniqueId());
				} else if (survey.getEcasSecurity() && request.getParameter("passwordauthenticated") == null
						&& user != null) {
					draft.getAnswerSet().setResponderEmail(user.getEmail());
				}

				//check that all readonly mandatory questions are answered
				Question q = SurveyHelper.getFirstUnansweredMandatoryReadonlyQuestion(draft.getAnswerSet());
				if (q != null) {
					String errorMessage = "Save as draft rejected as the draft contribution would be missing a value for this mandatory read-only question: " + q.getUniqueId();
					logger.error(errorMessage);
					ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
					model.addObject(Constants.MESSAGE, errorMessage);
					return model;
				}
								
				try {
					answerService.saveDraft(draft, true);
					uid = draft.getUniqueId();
				} catch (HibernateOptimisticLockingFailureException | ConstraintViolationException he) {
					logger.info(he.getLocalizedMessage(), he);
					hibernateOptimisticLockingFailureExceptionCatched = true;
				}
			}

			String url = "";
			if (invitationId != null && invitationId.trim().length() > 0) {
				// the draft comes from an invitation
				Invitation invitation = attendeeService.getInvitation(Integer.parseInt(invitationId));

				ParticipationGroup group = participationService.get(Integer.parseInt(participationGroupId));

				if (group.getType() == ParticipationGroupType.Token) {
					url = serverPrefix + "runner/" + survey.getUniqueId() + Constants.PATH_DELIMITER + invitation.getUniqueId();
				} else {
					url = serverPrefix + "runner/invited/" + participationGroupId + Constants.PATH_DELIMITER + invitation.getUniqueId();
				}
			} else if (mode.equalsIgnoreCase("test")) {
				url = serverPrefix + survey.getShortname() + "/management/test?draftid=" + uid;
			} else if (mode.equalsIgnoreCase("runner")) {
				User user = sessionService.getCurrentUser(request, false, false);
				if (survey.getEcasSecurity() && user != null) {
					url = serverPrefix + "runner/" + survey.getUniqueId();
				} else {
					url = serverPrefix + "runner/" + survey.getUniqueId() + "?draftid=" + uid;
				}
			}

			Cookie cookie = new Cookie("draft" + survey.getId(), uid);
			cookie.setMaxAge(10 * 24 * 60 * 60); // cookies are valid for 10 days
			cookie.setPath("/survey");
			response.addCookie(cookie);

			if (hibernateOptimisticLockingFailureExceptionCatched) {
				if (url.contains("?")) {
					return new ModelAndView("redirect:" + url + "&holf=1");
				} else {
					return new ModelAndView("redirect:" + url + "?holf=1");
				}
			}

			boolean passwordauthenticated = request.getParameter("passwordauthenticated") != null && request.getParameter("passwordauthenticated").equalsIgnoreCase("true");
			return new ModelAndView("redirect:" + "/runner/draftinfo/" + uid + (passwordauthenticated ? "?passwordauthenticated=true" : ""));

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return new ModelAndView("redirect:/errors/500.html");
	}

	@GetMapping(value = "/draftinfo/{draftid}")
	public ModelAndView DraftSubmit(@PathVariable String draftid, HttpServletRequest request, Locale locale,
			HttpServletResponse response, Device device) throws Exception {

		Draft draft = answerService.getDraft(draftid);

		if (draft == null) {
			throw new InvalidURLException();
		}

		Survey survey = draft.getAnswerSet().getSurvey();
		String mode = survey.getIsDraft() ? "test" : "runner";
		String invitationId = draft.getAnswerSet().getInvitationId();
		String uniqueCode = draft.getAnswerSet().getUniqueCode();
		String lang = draft.getAnswerSet().getLanguageCode();
		
		boolean passwordauthenticated = request.getParameter("passwordauthenticated") != null && request.getParameter("passwordauthenticated").equalsIgnoreCase("true");
		
		String url = answerService.getDraftURL(draft.getAnswerSet(), draftid, passwordauthenticated);

		ModelAndView result = new ModelAndView("thanksdraftrunner", "url", url);

		if (mode.equalsIgnoreCase("test")) {
			result = new ModelAndView("thanksdraft", "url", url);
		} else {
			result.addObject("runnermode", true);
			Form f = new Form(resources, surveyService.getLanguage(lang),
					translationService.getTranslationsForSurvey(survey.getId(), false), contextpath);
			result.addObject("form", f);
		}

		result.addObject("isdraftinfopage", true);
		result.addObject("surveyTitle", survey.cleanTitle());
		result.addObject("surveyID", survey.getId());
		result.addObject(Constants.UNIQUECODE, uniqueCode);
		result.addObject("downloadContribution", survey.getDownloadContribution());

		if (invitationId != null && invitationId.trim().length() > 0) {
			result.addObject("surveyprefix", survey.getId() + "." + uniqueCode);
		} else {
			result.addObject("surveyprefix", survey.getId());
		}

		if (survey.getEcasSecurity() && survey.getConfirmationPageLink()) {
			request.getSession().invalidate();
		}
		
		User user;
		try {
			user = sessionService.getCurrentUser(request, false, false);
			if (user != null && user.getType().equalsIgnoreCase("ECAS")) {
				result.addObject("isecasuser", true);
			}
		} catch (NotAgreedToTosException | WeakAuthenticationException | NotAgreedToPsException e1) {
			// ignore
		}			

		return result;
	}

	@PostMapping(value = "/{uidorshortname}")
	public ModelAndView processSubmit(@PathVariable String uidorshortname, HttpServletRequest request,
			HttpServletResponse response, Locale locale, Model modelMap, Device device) {
		boolean hibernateOptimisticLockingFailureExceptionCatched = false;
		String email = null;

		try {

			if (request.getParameter("redirectFromCheckPassword") != null
					&& request.getParameter("redirectFromCheckPassword").equalsIgnoreCase("true")
					&& request.getParameter("password") != null && request.getParameter(Constants.SHORTNAME) != null) {
				Survey survey = surveyService.getSurvey(uidorshortname, false, true, false, false, null, true, true);
				Survey draftsurvey = surveyService.getSurvey(uidorshortname, true, false, false, false, null, true,
						false);
				String password = request.getParameter("password");

				if (survey != null && survey.getSecurity().startsWith("secured")) {

					if (survey.getPassword() != null && survey.getPassword().trim().length() > 0
							&& survey.getPassword().equals(password)) {
						// authenticated
						
						request.getSession().setAttribute("passwordauthentication", true);
						
						loadSurvey(survey, request, locale, uidorshortname, true, false);
						return new ModelAndView("redirect:/runner/" + uidorshortname + "?" + request.getQueryString() + "&pw=true");
					}

					// check for token
					Invitation invitation = attendeeService.getInvitationByUniqueId(password);
					if (invitation != null) {
						ParticipationGroup participationGroup = participationService
								.get(invitation.getParticipationGroupId());

						if (participationGroup != null
								&& participationGroup.getSurveyUid().equals(draftsurvey.getUniqueId())) {
							return new ModelAndView(
									"redirect:/runner/invited/" + participationGroup.getId() + Constants.PATH_DELIMITER + password);
						}
					}

					String draftid = request.getParameter("draftid");

					ModelAndView model = new ModelAndView("runner/surveyLogin");
					if (survey.getEcasSecurity()) {
						model.addObject("ecasurl", ecashost);
						model.addObject("ecassecurity", true);
						model.addObject("require2fa", require2fa);

						if (draftid != null && draftid.length() > 0) {
							model.addObject("serviceurl",
									serverPrefix + "auth/surveylogin?survey=" + uidorshortname + "#" + draftid);
						} else {
							model.addObject("serviceurl", serverPrefix + "auth/surveylogin?survey=" + uidorshortname);
						}
					}

					if (draftid != null && draftid.length() > 0) {
						model.addObject("draftid", draftid);
					}

					model.addObject(Constants.SHORTNAME, uidorshortname);
					model.addObject("surveyname", survey.cleanTitle());

					if (survey.getContact().startsWith("form:")) {
						model.addObject("contact", true);
					}

					model.addObject(Constants.ERROR, resources.getMessage("error.PasswordInvalid", null,
							"You did not provide a valid password!", locale));
					return model;

				}

				return new ModelAndView("redirect:/errors/500.html");
			}

			if (request.getParameter("survey.id") == null) {
				logger.error("survey id parameter missing in processSubmit for survey " + uidorshortname);
				return new ModelAndView("redirect:/errors/500.html");
			}

			Survey lastestPublishedSurvey = surveyService.getSurvey(uidorshortname, false, true, false, false, null,
					true, true);
			if (SurveyHelper.isMaxContributionReached(lastestPublishedSurvey, answerService)) {
				return getMaxAnswersReachedPageModel(lastestPublishedSurvey, request, device);
			}

			Survey origsurvey = surveyService.getSurvey(Integer.parseInt(request.getParameter("survey.id")), false,
					true);
			String uniqueCode = request.getParameter(Constants.UNIQUECODE);

			if (origsurvey.getSecurity().startsWith("secured")) {
				if (!validCodesService.checkValid(uniqueCode, origsurvey.getUniqueId())) {
					ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
					model.addObject(Constants.MESSAGE, resources.getMessage("error.NoInvitation", null,
							"You are not authorized to submit to this survey without a proper invitation.", locale));
					return model;
				}
			} else {
				// check if uniqueCode is valid
				if (!Tools.validUniqueCode(uniqueCode)) {
					return new ModelAndView("redirect:/errors/500.html");
				}
			}

			if (SurveyHelper.isDeactivatedOrEndDateExceeded(origsurvey, surveyService)) {
				return getEscapePageModel(origsurvey, request, device);
			}

			if (SurveyHelper.isMaxContributionReached(origsurvey, answerService)) {
				return getMaxAnswersReachedPageModel(origsurvey, request, device);
			}

			String lang = locale.getLanguage();
			if (request.getParameter("language.code") != null && request.getParameter("language.code").length() == 2) {
				lang = request.getParameter("language.code");
			}

			User user = sessionService.getCurrentUser(request, false, false);
			if (!origsurvey.getIsDelphi()) {
				ModelAndView err = testDraftAlreadySubmittedByUniqueCode(uniqueCode, locale);
				if (err != null)
					return err;
			}

			AnswerSet answerSet = answerService.automaticParseAnswerSet(request, origsurvey, uniqueCode, false, lang, user);

			String newlang = request.getParameter("newlang");
			String newlangpost = request.getParameter("newlangpost");
			String newcss = request.getParameter("newcss");
			String newviewpost = request.getParameter("newviewpost");

			Set<String> invisibleElements = new HashSet<>();
			Map<Element, String> validation = SurveyHelper.validateAnswerSet(answerSet, answerService,
					invisibleElements, resources, locale, request.getParameter("draftid"), request, false, user,
					fileService);

			if (newlangpost != null && newlangpost.equalsIgnoreCase("true")) {
				Survey survey = surveyService.getSurvey(origsurvey.getId(), newlang);
				Form f = new Form(survey, translationService.getActiveTranslationsForSurvey(survey.getId()),
						survey.getLanguage(), resources, contextpath);
				sessionService.setFormStartDate(request, f, uniqueCode);
				f.getAnswerSets().add(answerSet);
				f.setWcagCompliance(answerSet.getWcagMode() != null && answerSet.getWcagMode());

				ModelAndView model = new ModelAndView("runner/runner", "form", f);
				surveyService.initializeSkin(f.getSurvey());
				model.addObject("submit", true);
				model.addObject("runnermode", true);
				model.addObject(Constants.UNIQUECODE, uniqueCode);
				model.addObject("invisibleElements", invisibleElements);

				return model;
			} else if (newviewpost != null && newviewpost.equalsIgnoreCase("true")) {
				Survey survey = origsurvey;
				survey = surveyService.getSurvey(survey.getId(), newlang);
				Form f = new Form(survey, translationService.getActiveTranslationsForSurvey(survey.getId()),
						survey.getLanguage(), resources, contextpath);
				sessionService.setFormStartDate(request, f, uniqueCode);
				f.getAnswerSets().add(answerSet);
				if (newcss != null && newcss.equalsIgnoreCase("wcag")) {
					answerSet.setWcagMode(true);
				} else if (newcss != null && newcss.equalsIgnoreCase("standard")) {
					answerSet.setWcagMode(false);
				}
				f.setWcagCompliance(answerSet.getWcagMode() != null && answerSet.getWcagMode());
				ModelAndView model = new ModelAndView("runner/runner", "form", f);
				surveyService.initializeSkin(f.getSurvey());
				model.addObject("submit", true);
				model.addObject("runnermode", true);
				model.addObject(Constants.UNIQUECODE, uniqueCode);
				model.addObject("invisibleElements", invisibleElements);

				return model;
			}

			if (origsurvey.getEcasSecurity() && request.getParameter("passwordauthenticated") == null && user != null) {
				if (!origsurvey.getIsEVote()) {
					answerSet.setResponderEmail(user.getEmail());
					email = user.getEmail();
				}				

				if (user.getType().equalsIgnoreCase(User.ECAS)) {
					// if the user already submitted, show error page
					int contributionsCount = answerService.userContributionsToSurvey(origsurvey, user);
					if (contributionsCount > 0) {
						if (origsurvey.getAllowedContributionsPerUser() == 1
								|| origsurvey.getAllowedContributionsPerUser() <= contributionsCount) {
							request.getSession().removeAttribute("ECASSURVEY");
							ModelAndView modelReturn = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
							modelReturn.addObject("runnermode", true);

							if (origsurvey.getAllowedContributionsPerUser() == 1) {
								modelReturn.addObject(Constants.MESSAGE, resources.getMessage("error.UserAlreadySubmitted",
										null,
										"This account has already been used to submit a contribution. Multiple submission is prohibited.",
										locale));
							} else {
								modelReturn.addObject(Constants.MESSAGE, resources.getMessage("error.UserAlreadySubmitted2",
										null,
										"You have submitted the maximum number of contributions this survey allows. Further contributions are not allowed.",
										locale));
							}

							return modelReturn;
						}
					}
				}
			}

			if (!validation.isEmpty()) {
				Survey survey = origsurvey;
				if (request.getParameter("language.code") != null
						&& request.getParameter("language.code").length() == 2) {
					survey = surveyService.getSurvey(origsurvey.getId(), lang);
				}
				Form f = new Form(survey, translationService.getActiveTranslationsForSurvey(survey.getId()),
						survey.getLanguage(), resources, contextpath);
				sessionService.setFormStartDate(request, f, uniqueCode);
				f.getAnswerSets().add(answerSet);
				f.setWcagCompliance(answerSet.getWcagMode() != null && answerSet.getWcagMode());
				f.setValidation(validation);

				SurveyHelper.recreateUploadedFiles(answerSet, survey, fileService, answerExplanationService);

				ModelAndView model = new ModelAndView("runner/runner", "form", f);
				surveyService.initializeSkin(f.getSurvey());
				model.addObject("submit", true);
				model.addObject("runnermode", true);
				model.addObject(Constants.UNIQUECODE, uniqueCode);
				model.addObject(Constants.MESSAGE, resources.getMessage("error.CheckValidation", null,
						"Please check for validation errors.", locale));

				return model;
			}

			if (answerSet.getSurvey().getCaptcha() && !checkCaptcha(request)) {
				Survey survey = origsurvey;
				if (request.getParameter("language.code") != null
						&& request.getParameter("language.code").length() == 2) {
					survey = surveyService.getSurvey(origsurvey.getId(), lang);
				}
				Form f = new Form(survey, translationService.getActiveTranslationsForSurvey(survey.getId()),
						survey.getLanguage(), resources, contextpath);
				sessionService.setFormStartDate(request, f, uniqueCode);
				f.getAnswerSets().add(answerSet);
				ModelAndView model = new ModelAndView("runner/runner", "form", f);
				surveyService.initializeSkin(f.getSurvey());
				model.addObject("submit", true);
				model.addObject("runnermode", true);
				model.addObject(Constants.UNIQUECODE, uniqueCode);
				model.addObject("wrongcaptcha", "true");
				return model;
			}

			try {
				saveAnswerSet(answerSet, fileDir, request.getParameter("draftid"), -1, request);
			} catch (InvalidEmailException ie) {
				Survey survey = origsurvey;
				if (request.getParameter("language.code") != null
						&& request.getParameter("language.code").length() == 2) {
					survey = surveyService.getSurvey(origsurvey.getId(), lang);
				}
				Form f = new Form(survey, translationService.getActiveTranslationsForSurvey(survey.getId()),
						survey.getLanguage(), resources, contextpath);
				sessionService.setFormStartDate(request, f, uniqueCode);
				f.getAnswerSets().add(answerSet);
				validation.put((Element) ie.getElement(),
						resources.getMessage("error.InvalidEmail", null, "The email address is not valid", locale));
				f.setValidation(validation);
				ModelAndView model = new ModelAndView("runner/runner", "form", f);
				surveyService.initializeSkin(f.getSurvey());
				model.addObject("submit", true);
				model.addObject("runnermode", true);
				model.addObject(Constants.UNIQUECODE, uniqueCode);
				model.addObject(Constants.MESSAGE, resources.getMessage("error.CheckValidation", null,
						"Please check for validation errors.", locale));

				return model;
			} catch (LoginAlreadyExistsException le) {
				Survey survey = origsurvey;
				if (request.getParameter("language.code") != null
						&& request.getParameter("language.code").length() == 2) {
					survey = surveyService.getSurvey(origsurvey.getId(), lang);
				}
				Form f = new Form(survey, translationService.getActiveTranslationsForSurvey(survey.getId()),
						survey.getLanguage(), resources, contextpath);
				sessionService.setFormStartDate(request, f, uniqueCode);
				f.getAnswerSets().add(answerSet);
				ModelAndView model = new ModelAndView("runner/runner", "form", f);
				surveyService.initializeSkin(f.getSurvey());
				model.addObject("submit", true);
				model.addObject(Constants.UNIQUECODE, uniqueCode);
				model.addObject(Constants.MESSAGE, resources.getMessage("error.LoginExists", null,
						"This login already exists. Please choose a unique login.", locale));

				return model;
			} catch (SmtpServerNotConfiguredException ex) {
				String message = resources.getMessage("error.SmtpServerNotConfigured", null, locale);
				return new ModelAndView("error/info", Constants.MESSAGE, message);
			} catch (HibernateOptimisticLockingFailureException | ConstraintViolationException he) {
				logger.info(he.getLocalizedMessage(), he);
				hibernateOptimisticLockingFailureExceptionCatched = true;
			}

			if (hibernateOptimisticLockingFailureExceptionCatched) {
				Survey survey = origsurvey;
				if (request.getParameter("language.code") != null
						&& request.getParameter("language.code").length() == 2) {
					survey = surveyService.getSurvey(origsurvey.getId(), lang);
				}
				Form f = new Form(survey, translationService.getActiveTranslationsForSurvey(survey.getId()),
						survey.getLanguage(), resources, contextpath);
				f.getAnswerSets().add(answerSet);
				ModelAndView model = new ModelAndView("runner/runner", "form", f);
				surveyService.initializeSkin(f.getSurvey());
				model.addObject("submit", true);
				model.addObject("runnermode", true);
				model.addObject(Constants.UNIQUECODE, uniqueCode);
				model.addObject("holf", "true");

				return model;
			}

			validCodesService.invalidate(uniqueCode);

			if (origsurvey.getSecurity().startsWith("secured") && origsurvey.getEcasSecurity()
					&& origsurvey.getConfirmationPageLink()) {
				request.getSession().invalidate();
			}

			if (answerSet.getSurvey().getShortname().equalsIgnoreCase("NewSelfRegistrationSurvey")) {
				ModelAndView result = new ModelAndView("thanksregister", Constants.UNIQUECODE, answerSet.getUniqueCode());
				result.addObject("runnermode", true);
				result.addObject("surveyprefix", origsurvey.getId() + "." + uniqueCode);
				return result;
			}

			Survey survey = origsurvey;
			if (request.getParameter("language.code") != null && request.getParameter("language.code").length() == 2) {
				survey = surveyService.getSurvey(origsurvey.getId(), lang);
			}

			if (survey.getIsQuiz()) {
				ModelAndView result = new ModelAndView("runner/quizResult", Constants.UNIQUECODE, answerSet.getUniqueCode());
				Form form = new Form(resources, surveyService.getLanguage(locale.getLanguage().toUpperCase()),
						translationService.getActiveTranslationsForSurvey(answerSet.getSurvey().getId()), contextpath);
				sessionService.setFormStartDate(request, form, uniqueCode);
				form.setSurvey(survey);
				form.getAnswerSets().add(answerSet);
				result.addObject(form);
				result.addObject("surveyprefix", survey.getId());
				result.addObject("quiz", QuizHelper.getQuizResult(answerSet, invisibleElements));
				result.addObject("isquizresultpage", true);
				result.addObject("invisibleElements", invisibleElements);
				return result;
			}

			ModelAndView result = new ModelAndView("thanks", Constants.UNIQUECODE, answerSet.getUniqueCode());
		
			if (survey.getSendConfirmationEmail() && email != null && email.contains("@")) {
				surveyService.sendNotificationEmail(survey, answerSet, email);
				result.addObject("notificationemailtext", surveyService.getNotificationEmailText(survey, email, locale));
			}		

			if (!survey.isAnonymous() && answerSet.getResponderEmail() != null) {
				result.addObject("participantsemail", answerSet.getResponderEmail());
			}

			if (survey.getIsECF()) {
				result.addObject("isECF", true);
				Set<ECFProfile> profiles = this.ecfService.getECFProfiles(survey);
				result.addObject("ecfProfiles", profiles.stream().sorted().collect(Collectors.toList()));
				// compute results
				result.addObject("ecfIndividualResult", this.ecfService.getECFIndividualResult(survey, answerSet));
				result.addObject("contextpath", contextpath);
				result.addObject("surveyShortname", survey.getShortname());
			}

			result.addObject("isthankspage", true);
			result.addObject("runnermode", true);

			Form form = new Form(resources, surveyService.getLanguage(lang),
					translationService.getActiveTranslationsForSurvey(survey.getId()), contextpath);
			sessionService.setFormStartDate(request, form, uniqueCode);
			form.setSurvey(survey);
			
			if (survey.getIsOPC()) {
				result.addObject("opcredirection", form.getFinalConfirmationLink(opcredirect, lang, answerSet));
			}

			form.getAnswerSets().add(answerSet);
		
			result.addObject("form", form);
			result.addObject("text", survey.getConfirmationPage());
			if (survey.getConfirmationPageLink() != null && survey.getConfirmationPageLink()
					&& survey.getConfirmationLink() != null && survey.getConfirmationLink().length() > 0) {
				result.addObject("redirect", form.getFinalConfirmationLink(lang, answerSet));
			} else if (survey.getEcasSecurity() && request.getParameter("passwordauthenticated") == null) {
				result.addObject("asklogout", true);
			}

			result.addObject("surveyprefix", survey.getId());
			request.getSession().removeAttribute("ECASSURVEY");

			return result;
		} catch (LoginAlreadyExistsException le) {
			logger.info(le.getLocalizedMessage(), le);
			ModelAndView model = new ModelAndView(Constants.VIEW_ERROR_GENERIC);
			model.addObject(Constants.MESSAGE, resources.getMessage("error.LoginExists", null,
					"This login already exists. Please choose a unique login.", locale));
			return model;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ModelAndView("redirect:/errors/500.html");
		}
	}

	@RequestMapping(value = "/createanswerpdf/{code}", headers = "Accept=*/*", method = { RequestMethod.GET,
			RequestMethod.HEAD })
	public @ResponseBody String createanswerpdf(@PathVariable String code, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			AnswerSet answerSet = answerService.get(code);

			if ((answerSet == null || !answerSet.getSurvey().getCaptcha()) && !checkCaptcha(request)) {
				return "errorcaptcha";
			}

			if (answerSet != null) {
				Map<String, String[]> parameters = Ucs2Utf8.requestToHashMap(request);
				String email = parameters.get(Constants.EMAIL)[0];

				return pdfService.createAnswerPDF(code, email);
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return Constants.ERROR;
		}

		return "success";
	}

	@RequestMapping(value = "/createdraftanswerpdf/{code}", headers = "Accept=*/*", method = { RequestMethod.GET,
			RequestMethod.HEAD })
	public @ResponseBody String createdraftanswerpdf(@PathVariable String code, HttpServletRequest request,
			HttpServletResponse response) {
		try {

			if (!checkCaptcha(request)) {
				return "errorcaptcha";
			}

			Draft draft = answerService.getDraftByAnswerUID(code);
			if (draft != null) {
				Map<String, String[]> parameters = Ucs2Utf8.requestToHashMap(request);
				String email = parameters.get(Constants.EMAIL)[0];
				
				boolean passwordauthentication = request.getParameter("passwordauthentication") != null;

				return pdfService.createDraftAnswerPDF(code, email, passwordauthentication);
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return Constants.ERROR;
		}

		return "success";
	}

	@RequestMapping(value = "/createquizpdf/{code}", headers = "Accept=*/*", method = { RequestMethod.GET,
			RequestMethod.HEAD })
	public @ResponseBody String createquizpdf(@PathVariable String code, HttpServletRequest request,
			HttpServletResponse response) {
		try {

			if (!checkCaptcha(request)) {
				return "errorcaptcha";
			}

			AnswerSet answerSet = answerService.get(code);
			if (answerSet != null) {
				Map<String, String[]> parameters = Ucs2Utf8.requestToHashMap(request);
				String email = parameters.get(Constants.EMAIL)[0];
				QuizExecutor export = (QuizExecutor) context.getBean("quizExecutor");
				export.init(answerSet, email, sender, serverPrefix);
				taskExecutor.execute(export);
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return Constants.ERROR;
		}

		return "success";
	}

	@RequestMapping(value = "/sendmaillink", headers = "Accept=*/*", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody String sendDraftLinkByMail(HttpServletRequest request, HttpServletResponse response) {

		Map<String, String[]> parameters = Ucs2Utf8.requestToHashMap(request);
		String email = parameters.get(Constants.EMAIL)[0];
		boolean skipChecks = false;
		
		User user;
		try {
			user = sessionService.getCurrentUser(request, false, false);
			skipChecks = user != null && user.getType().equalsIgnoreCase("ECAS");
			if (skipChecks) {
				email = user.getEmail();
			}
		} catch (NotAgreedToTosException | WeakAuthenticationException | NotAgreedToPsException e1) {
			// ignore
		}			
				
		if (!skipChecks && !checkCaptcha(request)) {
			return "errorcaptcha";
		} else {
			String link = parameters.get("link")[0];
			String id = parameters.get("id")[0];

			Survey survey = surveyService.getSurvey(Integer.parseInt(id), false, true, true, false);
			
			if (email == null || link == null) {
				return Constants.ERROR;
			}

			String body = "Dear EUSurvey user,<br /><br />Your contribution to the survey '<b>" + survey.cleanTitle()
					+ "</b>' has been successfully saved as a draft. To retrieve your draft, please follow this link:<br /><br />";
			body += "<a href=\"" + link + "\">" + link + "</a><br /><br />Your EUSurvey team";

			try {
				InputStream inputStream = servletContext
						.getResourceAsStream("/WEB-INF/Content/mailtemplateeusurvey.html");
				String text = IOUtils.toString(inputStream, "UTF-8").replace("[CONTENT]", body).replace("[HOST]",
						serverPrefix);

				mailService.SendHtmlMail(email, sender, sender,
						resources.getMessage("message.mail.linkDraftSubject", null, new Locale("EN")), text, null);
			} catch (Exception e) {
				logger.error("Problem during sending the draft link. To:" + email + " Link:" + link, e);
				return Constants.ERROR;
			}

			return "success";
		}
	}

	@RequestMapping(value = "/elements/{id}", method = { RequestMethod.GET, RequestMethod.HEAD })
	public @ResponseBody List<Element> element(@PathVariable String id, HttpServletRequest request,
			HttpServletResponse response)
			throws NotAgreedToTosException, WeakAuthenticationException, NotAgreedToPsException {
		String ids = request.getParameter("ids");

		if (ids == null)
			return null;

		List<Element> result = surveyService.getElements(ids.split("-"));

		String slang = request.getParameter("slang");
		Survey survey = SurveyHelper.createTranslatedSurvey(Integer.parseInt(id), slang, surveyService,
				translationService, false);

		if (survey == null) {
			return null;
		}

		boolean foreditor = request.getParameter("foreditor") != null
				&& request.getParameter("foreditor").equalsIgnoreCase("true");
		boolean hasGlobalAdminRights = false;

		if (foreditor) {
			User user = sessionService.getCurrentUser(request, false, false);
			if (user != null) {
				hasGlobalAdminRights = user.getGlobalPrivileges().get(GlobalPrivilege.FormManagement) == 2;
			}
		}

		Form form = new Form();
		form.setSurvey(survey);

		for (Element element : result) {
			element.setOriginalTitle(element.getTitle());

			if (element instanceof Section) {
				element.setTitle(form.getSectionTitle((Section) element));
			} else {
				element.setTitle(form.getQuestionTitle(element));
			}

			if (element instanceof Matrix) {
				Matrix m = (Matrix) element;

				for (Element child : m.getQuestions()) {
					child.setOriginalTitle(child.getTitle());
					child.setTitle(form.getQuestionTitle(child));
					child.presetIsDependentMatrixQuestion(survey);
				}

				m.getDependentElementsStrings();
			} else if (element instanceof RatingQuestion) {
				RatingQuestion r = (RatingQuestion) element;
				for (Element child : r.getQuestions()) {
					child.setOriginalTitle(child.getTitle());
					child.setTitle(form.getQuestionTitle(child));
				}
			} else if (element instanceof Table) {
				for (Element child : ((Table) element).getQuestions()) {
					child.setOriginalTitle(child.getTitle());
					child.setTitle(form.getQuestionTitle(child));
				}
				for (Element child : ((Table) element).getAnswers()) {
					child.setOriginalTitle(child.getTitle());
				}
			} else if (element instanceof ComplexTable) {
				for (Element child : ((ComplexTable) element).getChildElements()) {
					child.setOriginalTitle(child.getTitle());
					//child.setTitle(form.getQuestionTitle(child));
				}
			} else if (element instanceof Image) {
				Image image = (Image) element;
				if (image.getUrl() != null && image.getUrl().length() > 0) {
					String fileUID = image.getUrl().replace(contextpath + "/files/", "");

					if (fileUID.length() > 0) {
						try {
							com.ec.survey.model.survey.base.File f = fileService.get(fileUID);
							image.setFilename(f.getName());
						} catch (FileNotFoundException e) {
							// ignore
						}
					}
				}
			}
			
			//this can happen if a delphi survey is turned into a standard survey or the delphi extension is disabled
			if (element.isDelphiElement() && !survey.getIsDelphi())
			{
				((Question)element).setIsDelphiQuestion(false);
			}
		}

		for (Element element : result) {
			if (foreditor && hasGlobalAdminRights) {
				element.setLocked(false);
			}

			if (element instanceof Matrix) {
				Matrix m = (Matrix) element;
				if (foreditor) {
					m.setForeditor(true);
				}
				for (DependencyItem dep : m.getDependentElements()) {
					dep.getDependentElements().clear();
				}
			} else if (element instanceof ChoiceQuestion) {
				ChoiceQuestion q = (ChoiceQuestion) element;
				if (foreditor) {
					q.setForeditor(true);
				}
				for (Element e : q.getPossibleAnswers()) {
					PossibleAnswer a = (PossibleAnswer) e;
					a.clearForJSON();
				}
			} else if (element instanceof ComplexTable){
				for (ComplexTableItem item : ((ComplexTable) element).getChildElements()){
					item.setForEditor(foreditor);
				}
			}
		}

		return result;
	}

}
