package com.ec.survey.controller;

import com.ec.survey.exception.ForbiddenURLException;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.attendees.Attendee;
import com.ec.survey.model.selfassessment.SACriterion;
import com.ec.survey.model.selfassessment.SAReportConfiguration;
import com.ec.survey.model.selfassessment.SAResult;
import com.ec.survey.model.selfassessment.SAScore;
import com.ec.survey.model.selfassessment.SAScoreCard;
import com.ec.survey.model.selfassessment.SATargetDataset;
import com.ec.survey.model.survey.*;
import com.ec.survey.service.SelfAssessmentService;
import com.ec.survey.service.SessionService;
import com.ec.survey.tools.Constants;
import com.mysql.cj.util.StringUtils;

import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/{shortname}/management/selfassessment")
public class SelfAssessmentController extends BasicController {
	
	@Resource(name = "sessionService")
	protected SessionService sessionService;
	
	@Resource(name = "selfassessmentService")
	protected SelfAssessmentService selfassessmentService;
	
	@GetMapping("/criteria")
	public ResponseEntity<List<SACriterion>> criteria(@PathVariable String shortname, HttpServletRequest request, Locale locale) {
		try {
			final User user = sessionService.getCurrentUser(request, false, false);
			Survey survey = surveyService.getSurveyByShortname(shortname, true, user, request, false, true, true, false);
						
			List<SACriterion> result = selfassessmentService.getCriteria(survey.getUniqueId());
			
			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return null;
		}
	}
	
	@PostMapping("/createcriterion")
	public @ResponseBody String createcriterion(@PathVariable String shortname, HttpServletRequest request, Locale locale) {
		try {
			final User user = sessionService.getCurrentUser(request, false, false);
			Survey survey = surveyService.getSurveyByShortname(shortname, true, user, request, false, true, true, false);
		
			boolean userIsFormManager = sessionService.userIsFormAdmin(survey, user, request);
			
			if (!userIsFormManager) {
				throw new ForbiddenURLException();
			}	
			
			String name = request.getParameter("name");
					
			SACriterion c = new SACriterion();
			c.setName(name);
			c.setSurveyUID(survey.getUniqueId());
			c.setAcronym("XX");
			c.setType("Standard");
			selfassessmentService.addCriterion(c);
			
			return "OK";
		} catch (ConstraintViolationException ce) {
			if (ce.getConstraintName().contains("NAME_SURVEY")) {
				return "NAMEALREADYEXISTS"; 
			}
		} catch (DataIntegrityViolationException de) {
			if (de.getCause() != null && de.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException ce = (ConstraintViolationException)de.getCause();			
				if (ce.getConstraintName().contains("NAME_SURVEY")) {
					return "NAMEALREADYEXISTS"; 
				}	
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		
		return "ERROR";
	}
	
	@PostMapping("/updatecriterion")
	public @ResponseBody String updatecriterion(@PathVariable String shortname, HttpServletRequest request, Locale locale) {
		try {
			final User user = sessionService.getCurrentUser(request, false, false);
			Survey survey = surveyService.getSurveyByShortname(shortname, true, user, request, false, true, true, false);
		
			boolean userIsFormManager = sessionService.userIsFormAdmin(survey, user, request);
			
			if (!userIsFormManager) {
				throw new ForbiddenURLException();
			}	
			
			String id = request.getParameter("id");
			String name = request.getParameter("name");
			String acronym = request.getParameter("acronym");
			String type = request.getParameter("type");
		
			selfassessmentService.updateCriterion(Integer.parseInt(id), survey.getUniqueId(), name, acronym, type);
			
			return "OK";
		} catch (ConstraintViolationException ce) {
			if (ce.getConstraintName().contains("NAME_SURVEY")) {
				return "NAMEALREADYEXISTS"; 
			}
		} catch (DataIntegrityViolationException de) {
			if (de.getCause() != null && de.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException ce = (ConstraintViolationException)de.getCause();			
				if (ce.getConstraintName().contains("NAME_SURVEY")) {
					return "NAMEALREADYEXISTS"; 
				}	
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		
		return "ERROR";
	}
	
	@PostMapping("/deletecriterion")
	public @ResponseBody boolean deletecriterion(@PathVariable String shortname, HttpServletRequest request, Locale locale) {
		try {
			final User user = sessionService.getCurrentUser(request, false, false);
			Survey survey = surveyService.getSurveyByShortname(shortname, true, user, request, false, true, true, false);
		
			boolean userIsFormManager = sessionService.userIsFormAdmin(survey, user, request);
			
			if (!userIsFormManager) {
				throw new ForbiddenURLException();
			}	
			
			String id = request.getParameter("id");
						
			if (StringUtils.isNullOrEmpty(id)) {
				throw new ForbiddenURLException();
			}
			
			selfassessmentService.deleteCriterion(Integer.parseInt(id), survey.getUniqueId());
			
			return true;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return false;
		}
	}
	
	@GetMapping("/types")
	public @ResponseBody String[] getTypesJSON(@PathVariable String shortname, HttpServletRequest request) {
		try {
			
			//the shortname is actually the uid of the survey in this call		
			String term = request.getParameter("term");
			return selfassessmentService.getTypesForSurvey(shortname, term);
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return null;
		}
	}
		
	@GetMapping("/targetdatasets")
	public ResponseEntity<List<SATargetDataset>> targetdatasets(@PathVariable String shortname, HttpServletRequest request, Locale locale) {
		try {
			final User user = sessionService.getCurrentUser(request, false, false);
			Survey survey = surveyService.getSurveyByShortname(shortname, true, user, request, false, true, true, false);
				
			List<SATargetDataset> result = selfassessmentService.getTargetDatasets(survey.getUniqueId());
			
			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return null;
		}
	}
	
	@PostMapping("/createtargetdataset")
	public @ResponseBody String createtargetdataset(@PathVariable String shortname, HttpServletRequest request, Locale locale) {
		try {
			final User user = sessionService.getCurrentUser(request, false, false);
			Survey survey = surveyService.getSurveyByShortname(shortname, true, user, request, false, true, true, false);
		
			boolean userIsFormManager = sessionService.userIsFormAdmin(survey, user, request);
			
			if (!userIsFormManager) {
				throw new ForbiddenURLException();
			}	
			
			String name = request.getParameter("name");
			
			SATargetDataset t = new SATargetDataset();
			t.setName(name);
			t.setSurveyUID(survey.getUniqueId());
			selfassessmentService.addTargetDataset(t);
			
			return "OK";
		} catch (ConstraintViolationException ce) {
			if (ce.getConstraintName().contains("NAME_SURVEY")) {
				return "NAMEALREADYEXISTS"; 
			}
		} catch (DataIntegrityViolationException de) {
			if (de.getCause() != null && de.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException ce = (ConstraintViolationException)de.getCause();			
				if (ce.getConstraintName().contains("NAME_SURVEY")) {
					return "NAMEALREADYEXISTS"; 
				}	
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		
		return "ERROR";
	}
	
	@PostMapping("/updatetargetdataset")
	public @ResponseBody String updatetargetdataset(@PathVariable String shortname, HttpServletRequest request, Locale locale) {
		try {
			final User user = sessionService.getCurrentUser(request, false, false);
			Survey survey = surveyService.getSurveyByShortname(shortname, true, user, request, false, true, true, false);
		
			boolean userIsFormManager = sessionService.userIsFormAdmin(survey, user, request);
			
			if (!userIsFormManager) {
				throw new ForbiddenURLException();
			}	
			
			String id = request.getParameter("id");
			String name = request.getParameter("name");
		
			selfassessmentService.updateTargetDataset(Integer.parseInt(id), survey.getUniqueId(), name);
			
			return "OK";
		} catch (ConstraintViolationException ce) {
			if (ce.getConstraintName().contains("NAME_SURVEY")) {
				return "NAMEALREADYEXISTS"; 
			}
		} catch (DataIntegrityViolationException de) {
			if (de.getCause() != null && de.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException ce = (ConstraintViolationException)de.getCause();			
				if (ce.getConstraintName().contains("NAME_SURVEY")) {
					return "NAMEALREADYEXISTS"; 
				}	
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		
		return "ERROR";
	}
	
	@PostMapping("/deletetargetdataset")
	public @ResponseBody boolean deletetargetdataset(@PathVariable String shortname, HttpServletRequest request, Locale locale) {
		try {
			final User user = sessionService.getCurrentUser(request, false, false);
			Survey survey = surveyService.getSurveyByShortname(shortname, true, user, request, false, true, true, false);
		
			boolean userIsFormManager = sessionService.userIsFormAdmin(survey, user, request);
			
			if (!userIsFormManager) {
				throw new ForbiddenURLException();
			}	
			
			String id = request.getParameter("id");
						
			if (StringUtils.isNullOrEmpty(id)) {
				throw new ForbiddenURLException();
			}
			
			selfassessmentService.deleteTargetDataset(Integer.parseInt(id), survey.getUniqueId());
			
			return true;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return false;
		}
	}
	
	@GetMapping("/scores")
	public ResponseEntity<SAScoreCard> scores(@PathVariable String shortname, HttpServletRequest request, Locale locale) {
		try {
			final User user = sessionService.getCurrentUser(request, false, false);
			Survey survey = surveyService.getSurveyByShortname(shortname, true, user, request, false, true, true, false);
			
			boolean userIsFormManager = sessionService.userIsFormReaderOrAdmin(survey, user, request);
			
			if (!userIsFormManager) {
				throw new ForbiddenURLException();
			}	
			
			int datasetId = Integer.parseInt(request.getParameter("dataset"));
			
			SAScoreCard result = selfassessmentService.getScoreCard(datasetId);
			List<SACriterion> criteria = selfassessmentService.getCriteria(survey.getUniqueId());
			if (result == null) {
				result = new SAScoreCard();
				result.setScores(new ArrayList<SAScore>());
				result.setDatasetID(datasetId);
				for (SACriterion criterion : criteria) {
					SAScore s = new SAScore();
					s.setCriterion(criterion.getId());
					s.setCriterionName(criterion.getAcronym() + " - " + criterion.getName());
					s.setScoreCard(result);
					result.getScores().add(s);
				}				
			} else {				
				List<SAScore> scoresToDelete = new ArrayList<>();
				for (SAScore saScore : result.getScores()) {
					Optional<SACriterion> criterion = criteria.stream().filter(c -> c.getId().equals(saScore.getCriterion())).findFirst();
					if (criterion.isPresent()) {
						saScore.setCriterionName(criterion.get().getAcronym() + " - " + criterion.get().getName());
					} else {
						//criterion has been deleted in the meantime: remove
						scoresToDelete.add(saScore);
					}
				}
				//remove deleted criteria
				for (SAScore saScore : scoresToDelete) {
					result.getScores().remove(saScore);
				}
				//add new criteria
				for (SACriterion criterion : criteria) {
					if (!result.hasScoreForCriterion(criterion)) {
						SAScore s = new SAScore();
						s.setCriterion(criterion.getId());
						s.setCriterionName(criterion.getAcronym() + " - " + criterion.getName());
						s.setScoreCard(result);
						result.getScores().add(s);
					}					
				}				
				
				selfassessmentService.updateScoreCard(result, datasetId);
			}
			
			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return null;
		}
	}
	
	@PostMapping("/updatescores")
	public @ResponseBody String updatescores(@RequestBody SAScoreCard card, @PathVariable String shortname, HttpServletRequest request, Locale locale) {
		try {
			final User user = sessionService.getCurrentUser(request, false, false);
			Survey survey = surveyService.getSurveyByShortname(shortname, true, user, request, false, true, true, false);
		
			boolean userIsFormManager = sessionService.userIsFormAdmin(survey, user, request);
			
			if (!userIsFormManager) {
				throw new ForbiddenURLException();
			}	
				
			int datasetId = Integer.parseInt(request.getParameter("dataset"));
			selfassessmentService.updateScoreCard(card, datasetId);
			
			return "OK";
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		
		return "ERROR";
	}
	
	@GetMapping("/reportConfiguration")
	public ResponseEntity<SAReportConfiguration> reportConfiguration(@PathVariable String shortname, HttpServletRequest request, Locale locale) {
		try {
			final User user = sessionService.getCurrentUser(request, false, false);
			Survey survey = surveyService.getSurveyByShortname(shortname, true, user, request, false, true, true, false);					
			
			SAReportConfiguration result = selfassessmentService.getReportConfiguration(survey.getUniqueId());
			
			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return null;
		}
	}
	
	@PostMapping("/updateReportConfiguration")
	public @ResponseBody String updateReportConfiguration(@RequestBody SAReportConfiguration configuration, @PathVariable String shortname, HttpServletRequest request, Locale locale) {
		try {
			final User user = sessionService.getCurrentUser(request, false, false);
			Survey survey = surveyService.getSurveyByShortname(shortname, true, user, request, false, true, true, false);
		
			boolean userIsFormManager = sessionService.userIsFormAdmin(survey, user, request);
			
			if (!userIsFormManager) {
				throw new ForbiddenURLException();
			}	
			
			selfassessmentService.updateReportConfiguration(configuration, survey.getUniqueId());
			
			return "OK";
		} catch (ConstraintViolationException ce) {
			if (ce.getConstraintName().contains("SURVEY_REPORT")) {
				return "CONFIGALREADYEXISTS"; 
			}
		} catch (DataIntegrityViolationException de) {
			if (de.getCause() != null && de.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException ce = (ConstraintViolationException)de.getCause();			
				if (ce.getConstraintName().contains("SURVEY_REPORT")) {
					return "CONFIGALREADYEXISTS"; 
				}	
			} else if (de.getCause() != null && de.getCause() instanceof DataException) {
				DataException da = (DataException)de.getCause();
				if (da.getCause().toString().contains("Data too long")) {
					return da.getCause().toString().contains("SAREPORTCONFIG_FEEDBACK") ? "FEEDBACKTOOLONG" : "INTROTOOLONG";
				}
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		
		return "ERROR";
	}
	
	@RequestMapping(value = "/results", method = {RequestMethod.GET, RequestMethod.HEAD})
	public @ResponseBody SAResult results(@PathVariable String shortname, HttpServletRequest request) throws Exception {
		String datasetid = request.getParameter("dataset");		
		int dataset = Integer.parseInt(datasetid);
		
		String contributionuid = request.getParameter("contribution");		
		//TODO check
		
		SAResult result = selfassessmentService.getSAResult(dataset, contributionuid);
		return result;
	}
	
	
}
