package com.ec.survey.service;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.codec.binary.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ec.survey.exception.BadRequestException;
import com.ec.survey.exception.ECFException;
import com.ec.survey.exception.httpexception.NotFoundException;
import com.ec.survey.model.Answer;
import com.ec.survey.model.AnswerSet;
import com.ec.survey.model.ECFCluster;
import com.ec.survey.model.ECFCompetency;
import com.ec.survey.model.ECFExpectedScore;
import com.ec.survey.model.ECFExpectedScoreToProfileEid;
import com.ec.survey.model.ECFProfile;
import com.ec.survey.model.ECFType;
import com.ec.survey.model.ResultFilter;
import com.ec.survey.model.ResultFilter.ResultFilterOrderBy;
import com.ec.survey.model.SqlPagination;
import com.ec.survey.model.survey.ChoiceQuestion;
import com.ec.survey.model.survey.Element;
import com.ec.survey.model.survey.PossibleAnswer;
import com.ec.survey.model.survey.Question;
import com.ec.survey.model.survey.SingleChoiceQuestion;
import com.ec.survey.model.survey.Survey;
import com.ec.survey.model.survey.ecf.ECFGlobalCompetencyResult;
import com.ec.survey.model.survey.ecf.ECFGlobalResult;
import com.ec.survey.model.survey.ecf.ECFGlobalTotalResult;
import com.ec.survey.model.survey.ecf.ECFIndividualCompetencyResult;
import com.ec.survey.model.survey.ecf.ECFIndividualResult;
import com.ec.survey.model.survey.ecf.ECFOrganizationalCompetencyResult;
import com.ec.survey.model.survey.ecf.ECFOrganizationalResult;
import com.ec.survey.model.survey.ecf.ECFProfileCompetencyResult;
import com.ec.survey.model.survey.ecf.ECFProfileResult;
import com.ec.survey.model.survey.ecf.ECFProfileSummaryResult;
import com.ec.survey.model.survey.ecf.ECFSummaryResult;
import com.ec.survey.model.survey.ecf.TypeUUIDAndName;
import com.google.common.primitives.Ints;

@Service("ecfService")
@Configurable
public class ECFService extends BasicService {

	private static final Logger logger = Logger.getLogger(ECFService.class);

	@Resource(name = "sessionFactory")
	private SessionFactory sessionFactory;

	public ECFSummaryResult getECFSummaryResult(Survey survey) throws Exception {
		ECFSummaryResult ecfSummaryResult = new ECFSummaryResult();
		List<AnswerSet> answerSets = this.answerService.getAllAnswers(survey.getId(), null);

		Set<ECFProfile> profiles = this.getECFProfiles(survey);
		Map<String, String> profileUidToName = new HashMap<>();
		Map<String, Integer> profileToNumberAnswers = new HashMap<>();
		Map<String, Integer> profileUidToOrder = new HashMap<>();

		for (ECFProfile profile : profiles) {
			profileUidToName.put(profile.getProfileUid(), profile.getName());
			profileToNumberAnswers.put(profile.getProfileUid(), 0);
			profileUidToOrder.put(profile.getProfileUid(), profile.getOrderNumber());
		}

		for (AnswerSet answerSet : answerSets) {
			String profileUid = this.getECFProfile(survey, answerSet).getProfileUid();
			if (profileToNumberAnswers.containsKey(profileUid)) {
				Integer previousNumberAnswers = profileToNumberAnswers.get(profileUid);
				profileToNumberAnswers.put(profileUid, previousNumberAnswers + 1);
			} else {
				throw new ECFException("An answerset references a non existing profile : " + profileUid);
			}
		}

		Integer totalContributions = 0;
		for (String profileUid : profileToNumberAnswers.keySet()) {
			ECFProfileSummaryResult ecfSummaryProfileResult = new ECFProfileSummaryResult(
					profileUidToOrder.get(profileUid));
			ecfSummaryProfileResult.setProfileName(profileUidToName.get(profileUid));
			ecfSummaryProfileResult.setNumberOfContributions(profileToNumberAnswers.get(profileUid));
			ecfSummaryProfileResult.setProfileUid(profileUid);
			ecfSummaryResult.addProfileResult(ecfSummaryProfileResult);

			totalContributions = totalContributions + ecfSummaryProfileResult.getNumberOfContributions();
		}

		ECFProfileSummaryResult ecfSummaryProfileResult = new ECFProfileSummaryResult(Integer.MAX_VALUE);
		ecfSummaryProfileResult.setProfileName("All job profile");
		ecfSummaryProfileResult.setNumberOfContributions(totalContributions);
		ecfSummaryProfileResult.setIsSelected(true);
		ecfSummaryResult.addProfileResult(ecfSummaryProfileResult);

		ecfSummaryResult
				.setProfileResults(ecfSummaryResult.getProfileResults().stream().sorted().collect(Collectors.toList()));

		return ecfSummaryResult;
	}

	public ECFOrganizationalResult getECFOrganizationalResult(Survey survey) throws Exception {
		if (survey == null || !survey.getIsECF()) {
			throw new IllegalArgumentException("survey needs to be ECF to parse ECF results");
		}

		ECFOrganizationalResult ecfOrganizationalResult = new ECFOrganizationalResult();

		Map<ECFProfile, Map<ECFCompetency, Integer>> profilesToExpectedScores = getProfilesExpectedScores(
				this.getECFProfiles(survey));
		Map<ECFCompetency, Integer> competencyToMaxTarget = new HashMap<>();

		Map<ECFCompetency, Integer> competencyToTotalTarget = new HashMap<>();
		Map<ECFCompetency, Integer> competencyToNumberTarget = new HashMap<>();

		for (ECFProfile profile : profilesToExpectedScores.keySet()) {
			Map<ECFCompetency, Integer> competencyToTarget = profilesToExpectedScores.get(profile);
			for (ECFCompetency competency : competencyToTarget.keySet()) {
				Integer target = competencyToTarget.get(competency);

				if (competencyToTotalTarget.containsKey(competency)) {
					Integer previousTotalTarget = competencyToTotalTarget.get(competency);
					competencyToTotalTarget.put(competency, previousTotalTarget + target);
				} else {
					competencyToTotalTarget.put(competency, target);
				}

				if (competencyToNumberTarget.containsKey(competency)) {
					Integer previousNumberTarget = competencyToNumberTarget.get(competency);
					competencyToNumberTarget.put(competency, previousNumberTarget + 1);
				} else {
					competencyToNumberTarget.put(competency, 1);
				}

				if (competencyToMaxTarget.containsKey(competency)) {
					Integer previousMaxTarget = competencyToMaxTarget.get(competency);
					if (target > previousMaxTarget) {
						competencyToMaxTarget.put(competency, target);
					}
				} else {
					competencyToMaxTarget.put(competency, target);
				}
			}
		}

		List<AnswerSet> answerSets = this.answerService.getAllAnswers(survey.getId(), null);
		Map<ECFCompetency, List<Integer>> competenciesToScores = this.getCompetenciesToScores(survey, answerSets);

		Set<TypeUUIDAndName> competenciesTypes = new LinkedHashSet<TypeUUIDAndName>();
		List<ECFCompetency> orderedKeySet = competencyToMaxTarget.keySet().stream().sorted().collect(Collectors.toList());

		for (ECFCompetency competency : orderedKeySet) {
			ECFOrganizationalCompetencyResult competencyResult = new ECFOrganizationalCompetencyResult();
			competencyResult.setCompetencyName(competency.getName());
			competencyResult.setOrder(competency.getOrderNumber());

			TypeUUIDAndName typeUUIDAndName = new TypeUUIDAndName(
				competency.getEcfCluster().getEcfType().getName(),
				competency.getEcfCluster().getEcfType().getUid());
				
			competenciesTypes.add(typeUUIDAndName);

			competencyResult.setCompetencyTypeUid(competency.getEcfCluster().getEcfType().getUid());

			competencyResult.setCompetencyMaxTarget(competencyToMaxTarget.get(competency));

			Integer totalTarget = competencyToTotalTarget.get(competency);
			Integer numberOfTargets = competencyToNumberTarget.get(competency);
			Float averageTarget = this.roundedAverage(totalTarget, numberOfTargets);
			competencyResult.setCompetencyAverageTarget(averageTarget);

			List<Integer> competencyScores = competenciesToScores.get(competency);
			if (competencyScores.size() > 0) {
				Integer competencyMaxScore = competencyScores.stream().mapToInt(i -> i).max()
						.orElseThrow(NoSuchElementException::new);
				competencyResult.setCompetencyMaxScore(competencyMaxScore);

				Integer competencySumScores = competencyScores.stream().reduce(0, Integer::sum);
				Float competencyAverageScore = this.roundedAverage(competencySumScores, competencyScores.size());
				competencyResult.setCompetencyAverageScore(competencyAverageScore);
			}

			ecfOrganizationalResult.addCompetencyResult(competencyResult);
		}

		ecfOrganizationalResult.setCompetenciesTypes(new ArrayList<>(competenciesTypes));
		ecfOrganizationalResult.setCompetencyResults(
				ecfOrganizationalResult.getCompetencyResults().stream().sorted().collect(Collectors.toList()));

		return ecfOrganizationalResult;
	}

	public ECFProfileResult getECFProfileResult(Survey survey) throws Exception {
		return this.getECFProfileResult(survey, (ECFProfile) null);
	}

	public ECFProfileResult getECFProfileResult(Survey survey, ResultFilter resultFilter) throws Exception {
		ECFProfile profileComparison = null;
		if (resultFilter.getCompareToECFProfileUID() != null) {
			profileComparison = this.getECFProfileByUUID(resultFilter.getCompareToECFProfileUID());
			if (profileComparison == null)
				throw new NotFoundException();
		}

		return this.getECFProfileResult(survey, profileComparison);
	}

	/**
	 * Returns the ECFProfileResult for the given Survey, Pagination, and optional
	 * EcfProfile
	 */
	public ECFProfileResult getECFProfileResult(Survey survey, ECFProfile ecfProfile) throws Exception {
		if (survey == null || !survey.getIsECF()) {
			throw new IllegalArgumentException("survey needs to be ECF to parse ECF results");
		}

		ECFProfileResult ecfProfileResult = null;

		List<AnswerSet> answerSets = this.answerService.getAllAnswers(survey.getId(), null);

		if (ecfProfile != null) {
			List<AnswerSet> profileFilteredAnswerSets = new ArrayList<>();
			for (AnswerSet answer : answerSets) {
				if (this.getECFProfile(survey, answer).equals(ecfProfile)) {
					profileFilteredAnswerSets.add(answer);
				}
			}
			answerSets = profileFilteredAnswerSets;

			Map<ECFCompetency, Integer> competencyToExpectedScore = this.getProfileExpectedScores(ecfProfile);

			ecfProfileResult = this.getECFProfileCompetencyResult(survey, answerSets, competencyToExpectedScore,
					ecfProfile.getName());
		} else {
			ecfProfileResult = this.getECFProfileCompetencyResult(survey, answerSets);
		}

		ecfProfileResult.setCompetencyResults(
				ecfProfileResult.getCompetencyResults().stream().sorted().collect(Collectors.toList()));
		return ecfProfileResult;
	}

	public ECFProfileResult getECFProfileCompetencyResult(Survey survey, List<AnswerSet> answerSets)
			throws ECFException {
		return this.getECFProfileCompetencyResult(survey, answerSets, new HashMap<>(), null);
	}

	private ECFProfileResult getECFProfileCompetencyResult(Survey survey, List<AnswerSet> answerSets,
			Map<ECFCompetency, Integer> competencyToTargetScore, String profileName) throws ECFException {
		ECFProfileResult profileResult = new ECFProfileResult();
		profileResult.setProfileName(profileName);
		profileResult.setNumberOfAnswers(answerSets.size());

		Map<ECFCompetency, List<Integer>> competenciesToScores = this.getCompetenciesToScores(survey, answerSets);
		for (ECFCompetency competency : competenciesToScores.keySet()) {
			ECFProfileCompetencyResult profileCompetencyResult = new ECFProfileCompetencyResult();
			profileCompetencyResult.setCompetencyName(competency.getName());
			profileCompetencyResult.setOrder(competency.getOrderNumber());
			List<Integer> scores = competenciesToScores.get(competency);
			Integer targetScore = competencyToTargetScore.get(competency);

			if (scores.size() != 0) {
				Integer maxScore = 0;
				Integer totalScore = 0;
				for (Integer score : scores) {
					if (score > maxScore) {
						maxScore = score;
					}
					totalScore = totalScore + score;
				}

				float averageScore = (float) Math.round((totalScore.floatValue() / scores.size()) * 10) / 10;
				profileCompetencyResult.setCompetencyAverageScore(averageScore);
				profileCompetencyResult.setCompetencyMaxScore(maxScore);

				if (targetScore != null) {
					profileCompetencyResult.setCompetencyScoreGap(maxScore - targetScore);
				}

			}
			profileCompetencyResult.setCompetencyTargetScore(targetScore);
			profileResult.addIndividualResults(profileCompetencyResult);
		}
		return profileResult;
	}

	private Map<ECFProfile, Map<ECFCompetency, Integer>> getProfilesExpectedScores(Set<ECFProfile> profiles)
			throws ECFException {
		if (profiles == null) {
			throw new IllegalArgumentException("profiles cannot be null");
		}

		Map<ECFProfile, Map<ECFCompetency, Integer>> profileToCompetencyToExpectedScore = new HashMap<>();

		for (ECFProfile profile : profiles) {
			profileToCompetencyToExpectedScore.put(profile, this.getProfileExpectedScores(profile));
		}

		return profileToCompetencyToExpectedScore;
	}

	private Map<ECFCompetency, Integer> getProfileExpectedScores(ECFProfile ecfProfile) throws ECFException {
		if (ecfProfile == null) {
			throw new IllegalArgumentException("survey needs to be ECF to parse ECF results");
		}
		Map<ECFCompetency, Integer> competencyToExpectedScore = new HashMap<>();
		for (ECFExpectedScore expectedScore : ecfProfile.getECFExpectedScores()) {
			ECFCompetency competency = expectedScore.getECFExpectedScoreToProfileEid().getECFCompetency();
			if (competency == null) {
				throw new ECFException("A score must be linked to a competency");
			}
			competencyToExpectedScore.put(competency, expectedScore.getScore());
		}
		return competencyToExpectedScore;
	}

	/**
	 * Returns the mapping between the competencies and the scores the answerSets
	 * could get
	 */
	private Map<ECFCompetency, List<ParticipantScore>> getCompetenciesToParticipantScores(Survey survey,
			List<AnswerSet> answerSets) throws ECFException {
		Map<ECFCompetency, List<ParticipantScore>> competencyToScores = new HashMap<>();
		Set<Question> ecfQuestions = new HashSet<>();

		// Target the competency questions
		for (Element element : survey.getElements()) {
			if (element instanceof Question) {
				Question question = (Question) element;
				if (question instanceof SingleChoiceQuestion && question.getEcfCompetency() != null) {
					competencyToScores.put(question.getEcfCompetency(), new ArrayList<>());
					ecfQuestions.add(question);
				}
			}
		}

		// For each individual
		for (int i = 0; i < answerSets.size(); i++) {
			AnswerSet answerSet = answerSets.get(i);
			Map<ECFCompetency, Integer> competencyToNumberOfAnswers = new HashMap<>();
			Map<ECFCompetency, Integer> competencyToTotalAnsweredNumbers = new HashMap<>();

			// Pass through his answers
			for (Question question : ecfQuestions) {
				List<Answer> answers = answerSet.getAnswers(question.getId(), question.getUniqueId());
				if (answers.size() == 0)
					continue;
				Answer answer = answers.get(0);
				SingleChoiceQuestion choiceQuestion = (SingleChoiceQuestion) question;
				PossibleAnswer answeredPossibleAnswer = choiceQuestion
						.getPossibleAnswerByUniqueId(answer.getPossibleAnswerUniqueId());
				if (answeredPossibleAnswer != null) {
					Integer answeredNumber = answeredPossibleAnswer.getEcfScore();
					if (answeredNumber > 4 || answeredNumber < 0) {
						throw new ECFException("An ECF possible answer cannot be over 4");
					}

					ECFCompetency questionCompetency = question.getEcfCompetency();

					if (competencyToTotalAnsweredNumbers.containsKey(questionCompetency)) {
						Integer previousNumber = competencyToTotalAnsweredNumbers.get(questionCompetency);
						competencyToTotalAnsweredNumbers.put(questionCompetency, previousNumber + answeredNumber);
					} else {
						competencyToTotalAnsweredNumbers.put(questionCompetency, answeredNumber);
					}

					if (competencyToNumberOfAnswers.containsKey(question.getEcfCompetency())) {
						Integer previousNumber = competencyToNumberOfAnswers.get(question.getEcfCompetency());
						competencyToNumberOfAnswers.put(questionCompetency, previousNumber + 1);
					} else {
						competencyToNumberOfAnswers.put(questionCompetency, 1);
					}
				}
			}
			for (ECFCompetency competency : competencyToNumberOfAnswers.keySet()) {
				Integer numberOfAnswers = competencyToNumberOfAnswers.get(competency);
				Integer totalNumber = competencyToTotalAnsweredNumbers.get(competency);

				ParticipantScore participantScore = new ParticipantScore();
				if (answerSet.getResponderEmail() != null && !answerSet.getResponderEmail().isEmpty()) {
					participantScore.setName(answerSet.getResponderEmail());
				} else {
					participantScore.setName(answerSet.getUniqueCode());
				}
				participantScore.setContributionUUID(answerSet.getUniqueCode());
				participantScore.setScore(totalNumber / numberOfAnswers);

				List<ParticipantScore> listOfScores = competencyToScores.get(competency);
				listOfScores.add(participantScore);
				competencyToScores.put(competency, listOfScores);
			}
			// next individual
		}
		return competencyToScores;
	}

	/**
	 * Returns the mapping between the competencies and the scores the answerSets
	 * could get
	 */
	private Map<ECFCompetency, List<Integer>> getCompetenciesToScores(Survey survey, List<AnswerSet> answerSets)
			throws ECFException {
		Map<ECFCompetency, List<Integer>> competencyToScores = new HashMap<>();
		Set<Question> ecfQuestions = new HashSet<>();

		// Target the competency questions
		for (Element element : survey.getElements()) {
			if (element instanceof Question) {
				Question question = (Question) element;
				if (question instanceof SingleChoiceQuestion && question.getEcfCompetency() != null) {
					competencyToScores.put(question.getEcfCompetency(), new ArrayList<>());
					ecfQuestions.add(question);
				}
			}
		}

		// For each individual
		for (int i = 0; i < answerSets.size(); i++) {
			AnswerSet answerSet = answerSets.get(i);
			Map<ECFCompetency, Integer> competencyToNumberOfAnswers = new HashMap<>();
			Map<ECFCompetency, Integer> competencyToTotalAnsweredNumbers = new HashMap<>();

			// Pass through his answers
			for (Question question : ecfQuestions) {
				List<Answer> answers = answerSet.getAnswers(question.getId(), question.getUniqueId());
				if (answers.size() == 0)
					continue;
				Answer answer = answers.get(0);
				SingleChoiceQuestion choiceQuestion = (SingleChoiceQuestion) question;
				PossibleAnswer answeredPossibleAnswer = choiceQuestion
						.getPossibleAnswerByUniqueId(answer.getPossibleAnswerUniqueId());
				if (answeredPossibleAnswer != null) {
					char lastCharInShortName = answeredPossibleAnswer.getShortname()
							.charAt(answeredPossibleAnswer.getShortname().length() - 1);
					Integer answeredNumber = answeredPossibleAnswer.getEcfScore();
					if (answeredNumber > 4 || answeredNumber < 0) {
						throw new ECFException("An ECF possible answer cannot be over 4");
					}

					ECFCompetency questionCompetency = question.getEcfCompetency();

					if (competencyToTotalAnsweredNumbers.containsKey(questionCompetency)) {
						Integer previousNumber = competencyToTotalAnsweredNumbers.get(questionCompetency);
						competencyToTotalAnsweredNumbers.put(questionCompetency, previousNumber + answeredNumber);
					} else {
						competencyToTotalAnsweredNumbers.put(questionCompetency, answeredNumber);
					}

					if (competencyToNumberOfAnswers.containsKey(question.getEcfCompetency())) {
						Integer previousNumber = competencyToNumberOfAnswers.get(question.getEcfCompetency());
						competencyToNumberOfAnswers.put(questionCompetency, previousNumber + 1);
					} else {
						competencyToNumberOfAnswers.put(questionCompetency, 1);
					}
				}
			}
			for (ECFCompetency competency : competencyToNumberOfAnswers.keySet()) {
				Integer numberOfAnswers = competencyToNumberOfAnswers.get(competency);
				Integer totalNumber = competencyToTotalAnsweredNumbers.get(competency);
				List<Integer> listOfScores = competencyToScores.get(competency);
				listOfScores.add(totalNumber / numberOfAnswers);
				competencyToScores.put(competency, listOfScores);
			}
			// next individual
		}
		return competencyToScores;
	}

	public ECFGlobalResult getECFGlobalResult(Survey survey, SqlPagination sqlPagination) throws Exception {
		ECFProfile ecfProfile = null;
		return this.getECFGlobalResult(survey, sqlPagination, ecfProfile);
	}

	public ECFGlobalResult getECFGlobalResult(Survey survey, SqlPagination sqlPagination, ECFProfile profileComparison)
			throws Exception {
		return this.getECFGlobalResult(survey, sqlPagination, profileComparison, null);
	}

	public ECFGlobalResult getECFGlobalResult(Survey survey, SqlPagination sqlPagination, ECFProfile profileComparison,
			ECFProfile profileFilter) throws Exception {
		return this.getECFGlobalResult(survey, sqlPagination, profileComparison, null, null);
	}

	/**
	 * Entry point method from the controller;
	 */
	public ECFGlobalResult getECFGlobalResult(Survey survey, SqlPagination sqlPagination, ResultFilter resultFilter)
			throws Exception {
		ECFProfile profileFilter = null;
		ECFProfile profileComparison = null;
		String sortOrder = resultFilter.getSortOrder();
		String sortKey = resultFilter.getSortKey();

		if (resultFilter.getAnsweredECFProfileUID() != null) {
			profileFilter = this.getECFProfileByUUID(resultFilter.getAnsweredECFProfileUID());
			if (profileFilter == null)
				throw new NotFoundException();
		}

		if (resultFilter.getCompareToECFProfileUID() != null) {
			profileComparison = this.getECFProfileByUUID(resultFilter.getCompareToECFProfileUID());
			if (profileComparison == null)
				throw new NotFoundException();
		}

		if (sortOrder != null && sortKey != null) {
			if (ResultFilter.ResultFilterOrderBy.parse(sortKey + sortOrder)
					.equals(ResultFilter.ResultFilterOrderBy.UNKNOWN)) {
				throw new BadRequestException();
			}
		}

		return this.getECFGlobalResult(survey, sqlPagination, profileComparison, profileFilter, sortKey + sortOrder);
	}

	/**
	 * Returns the ECFGlobalResult for the given Survey, Pagination, and optional
	 * EcfProfile
	 */
	public ECFGlobalResult getECFGlobalResult(Survey survey, SqlPagination sqlPagination, ECFProfile profileComparison,
			ECFProfile profileFilter, String orderBy) throws Exception {
		if (survey == null || !survey.getIsECF()) {
			throw new IllegalArgumentException("survey needs to be ECF to parse ECF results");
		}

		ECFGlobalResult result = new ECFGlobalResult();
		if (profileComparison != null) {
			result.setProfileComparisonUid(profileComparison.getProfileUid());
		}
		if (profileFilter != null) {
			result.setProfileFilterUid(profileFilter.getProfileUid());
		}
		List<AnswerSet> answerSets = this.getAnswers(survey, profileFilter, orderBy, sqlPagination);

		Integer countAnswers = profileFilter != null ? this.getCount(survey, profileFilter) : this.getCount(survey);

		Map<ECFCompetency, Integer> competencyToExpectedScore = new HashMap<ECFCompetency, Integer>();

		if (profileComparison != null) {
			result.setProfileComparisonUid(profileComparison.getProfileUid());
			competencyToExpectedScore = this.getProfileExpectedScores(profileComparison);
		}

		Map<ECFCompetency, List<ParticipantScore>> competenciesToScores = this
				.getCompetenciesToParticipantScores(survey, answerSets);

		for (ECFCompetency competency : competenciesToScores.keySet()) {
			List<ParticipantScore> competencyScores = competenciesToScores.get(competency);
			List<Integer> competencyScoresNumbers = competencyScores.stream().map(ps -> ps.getScore())
					.collect(Collectors.toList());
			List<String> competencyScoresNames = competencyScores.stream().map(ps -> ps.getName())
					.collect(Collectors.toList());
			List<String> competencyScoresContUUIDs = competencyScores.stream().map(ps -> ps.getContributionUUID())
					.collect(Collectors.toList());

			ECFGlobalCompetencyResult globalCompetencyResult = new ECFGlobalCompetencyResult();
			globalCompetencyResult.setOrder(competency.getOrderNumber());
			globalCompetencyResult.setCompetencyName(competency.getName());
			globalCompetencyResult.setCompetencyScores(competencyScoresNumbers);
			globalCompetencyResult.setParticipantsNames(competencyScoresNames);
			globalCompetencyResult.setParticipantContributionUIDs(competencyScoresContUUIDs);

			if (competencyToExpectedScore.containsKey(competency)) {
				Integer targetScore = competencyToExpectedScore.get(competency);
				globalCompetencyResult.setCompetencyTargetScore(targetScore);

				for (Integer competencyScore : competencyScoresNumbers) {
					globalCompetencyResult.addCompetencyScoreGap(competencyScore - targetScore);
					;
				}
			}
			result.addIndividualResults(globalCompetencyResult);
		}

		ECFGlobalTotalResult totalResult = new ECFGlobalTotalResult();
		Integer totalExpectedScore = null;
		List<Integer> totalScores = new ArrayList<>();
		List<Integer> totalGaps = new ArrayList<>();

		if (!result.getIndividualResults().isEmpty()) {
			int[] totalScoresArray = new int[result.getIndividualResults().get(0).getCompetencyScores().size()];
			int[] totalGapsArray = new int[result.getIndividualResults().get(0).getCompetencyScoreGaps().size()];

			for (ECFGlobalCompetencyResult competencyResult : result.getIndividualResults()) {
				if (competencyResult.getCompetencyTargetScore() != null) {
					totalExpectedScore = totalExpectedScore == null ? competencyResult.getCompetencyTargetScore()
							: totalExpectedScore + competencyResult.getCompetencyTargetScore();
				}

				for (int i = 0; i < competencyResult.getCompetencyScores().size(); i++) {
					totalScoresArray[i] = totalScoresArray[i] + competencyResult.getCompetencyScores().get(i);

					if (totalGapsArray.length > 0) {
						totalGapsArray[i] = totalGapsArray[i] + competencyResult.getCompetencyScoreGaps().get(i);
					}
				}
			}
			totalScores = Ints.asList(totalScoresArray);
			totalGaps = Ints.asList(totalGapsArray);
		}

		totalResult.setTotalTargetScore(totalExpectedScore);
		totalResult.setTotalScores(totalScores);
		totalResult.setTotalGaps(totalGaps);

		result.setTotalResult(totalResult);

		result.setPageNumber(sqlPagination.getCurrentPage());
		result.setPageSize(sqlPagination.getRowsPerPage());
		result.setNumberOfPages((countAnswers / result.getPageSize()) + 1);
		result.setNumberOfResults(countAnswers);

		result.setIndividualResults(result.getIndividualResults().stream().sorted().collect(Collectors.toList()));

		return result;
	}

	public ECFIndividualResult getECFIndividualResult(Survey survey, AnswerSet answerSet) throws ECFException {
		return this.getECFIndividualResult(survey, answerSet, null);
	}

	/**
	 * Returns the individual result for the given answerSet compared to the
	 * expected scores of the profile Or to the profile specified in the AnswerSet
	 * itself.
	 * 
	 * @throws ECFException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional
	public ECFIndividualResult getECFIndividualResult(Survey survey, AnswerSet answerSet, ECFProfile profile)
			throws ECFException {
		if (survey == null || !survey.getIsECF()) {
			throw new IllegalArgumentException("survey needs to be ECF to parse ECF results");
		}
		if (answerSet == null) {
			throw new IllegalArgumentException("answer set is not null");
		}

		ECFIndividualResult ecfIndividualResult = new ECFIndividualResult();
		ECFProfile answererProfile = profile != null ? profile : getECFProfile(survey, answerSet);
		ecfIndividualResult.setProfileName(answererProfile.getName());
		ecfIndividualResult.setProfileUUID(answererProfile.getProfileUid());

		List<AnswerSet> answerSets = Arrays.asList(answerSet);
		Map<ECFCompetency, List<Integer>> competenciesToScores = this.getCompetenciesToScores(survey, answerSets);
		Map<ECFCompetency, Integer> competenciesToExpectedScores = this.getProfileExpectedScores(answererProfile);

		Set<TypeUUIDAndName> competenciesTypes = new LinkedHashSet<TypeUUIDAndName>();
		List<ECFCompetency> orderedKeySet = competenciesToScores.keySet().stream().sorted().collect(Collectors.toList());
		for (ECFCompetency competency : orderedKeySet) {
			ECFIndividualCompetencyResult competencyResult = new ECFIndividualCompetencyResult();
			competencyResult.setCompetencyName(competency.getName());
			competencyResult.setOrder(competency.getOrderNumber());
			competencyResult.setCompetencyTargetScore(competenciesToExpectedScores.get(competency));
			competencyResult.setCompetencyScore(competenciesToScores.get(competency).get(0));
			competencyResult.setTypeUUID(competency.getEcfCluster().getEcfType().getUid());

			TypeUUIDAndName ecfTypeNameAndUUID = new TypeUUIDAndName(
				competency.getEcfCluster().getEcfType().getName(),
				competency.getEcfCluster().getEcfType().getUid()
			);

			competenciesTypes.add(ecfTypeNameAndUUID);
			ecfIndividualResult.addCompetencyResult(competencyResult);
		}
		ecfIndividualResult.setCompetenciesTypes(new ArrayList<>(competenciesTypes));
		ecfIndividualResult.setCompetencyResultList(
				ecfIndividualResult.getCompetencyResultList().stream().sorted().collect(Collectors.toList()));
		return ecfIndividualResult;
	}

	/***
	 * Sets the ECFparts of an AnswerSet.
	 */
	public void setAnswerSetECFComponents(final Survey survey, AnswerSet answerSet) throws ECFException {
		ECFProfile profile = this.getECFProfile(survey, answerSet);

		Map<ECFCompetency, Integer> competenciesToExpectedScores = null;
		if (profile != null) {
			answerSet.setEcfProfileUid(profile.getProfileUid());
			competenciesToExpectedScores = this.getProfileExpectedScores(profile);
		}

		List<AnswerSet> answerSets = Arrays.asList(answerSet);
		Map<ECFCompetency, List<Integer>> competenciesToScores = this.getCompetenciesToScores(survey, answerSets);

		Integer totalScore = 0;
		Integer totalGap = 0;
		for (ECFCompetency competency : competenciesToScores.keySet()) {
			List<Integer> scores = competenciesToScores.get(competency);
			if (scores.size() > 1)
				throw new ECFException("This was not supposed to happen");
			totalScore = totalScore + scores.get(0);

			if (profile != null) {
				Integer expectedScore = competenciesToExpectedScores.get(competency);
				if (expectedScore == null) {
					throw new ECFException("Profile " + profile.getProfileUid() + " to Competency "
							+ competency.getCompetenceUid() + " do not seem to have an expected score");
				}
				Integer gap = scores.get(0) - expectedScore;
				totalGap = totalGap + gap;
			}
		}
		answerSet.setEcfTotalScore(totalScore);

		if (profile != null) {
			answerSet.setEcfTotalGap(totalGap);
		}
	}

	/**
	 * Returns the ECFProfile an answerer has entered in the answerSet, for a
	 * specific ECF survey
	 * 
	 * @throws ECFException if no ECFProfile could be found answered by the user
	 */
	public ECFProfile getECFProfile(Survey survey, AnswerSet answerSet) throws ECFException {
		if (answerSet.getEcfProfileUid() != null) {
			return this.getECFProfileByUUID(answerSet.getEcfProfileUid());
		}
		for (Element element : survey.getElements()) {
			if (element instanceof Question) {
				Question question = (Question) element;
				List<Answer> answers = answerSet.getAnswers(question.getId(), question.getUniqueId());

				if (question instanceof SingleChoiceQuestion) {
					if (answers.size() == 0)
						continue;
					// get points if answer is correct
					Answer answer = answers.get(0);
					ChoiceQuestion choice = (ChoiceQuestion) question;
					PossibleAnswer possibleAnswer = choice
							.getPossibleAnswerByUniqueId(answer.getPossibleAnswerUniqueId());
					if (possibleAnswer.getEcfProfile() != null) {
						return possibleAnswer.getEcfProfile();
					}
				}
			}
		}
		throw new ECFException("An answers set must reference a profile");
	}

	/**
	 * Contains <br>
	 * Horizontal -> Procurement specific competencies <br>
	 * Pre-award -> Procurement specific competencies <br>
	 * Post-award -> Procurement specific competencies
	 */
	public Map<String, String> defaultClusterToType() {
		Map<String, String> clusterNameToType = new HashMap<>();
		clusterNameToType.put("Horizontal", "Procurement specific competencies");
		clusterNameToType.put("Pre-award", "Procurement specific competencies");
		clusterNameToType.put("Post-award", "Procurement specific competencies");

		clusterNameToType.put("Self", "Professional competencies");
		clusterNameToType.put("People", "Professional competencies");
		clusterNameToType.put("Performance", "Professional competencies");
		return clusterNameToType;

	}

	public Map<String, String> defaultCompetencyToCluster() {
		Map<String, String> competencyToCluster = new HashMap<>();
		competencyToCluster.put("C1 - Planning", "Horizontal");
		competencyToCluster.put("C2 - Lifecycle", "Horizontal");
		competencyToCluster.put("C3 - Legislation", "Horizontal");
		competencyToCluster.put("C4 - e-Procurement and other IT tools", "Horizontal");
		competencyToCluster.put("C5 - Sustainable procurement", "Horizontal");
		competencyToCluster.put("C6 - Innovation Procurement", "Horizontal");
		competencyToCluster.put("C7 - Category specific", "Horizontal");
		competencyToCluster.put("C8 - Supplier management", "Horizontal");
		competencyToCluster.put("C9 - Negotiations", "Horizontal");
		competencyToCluster.put("C10 - Needs assessment", "Pre-award");
		competencyToCluster.put("C11 - Market analysis and market engagement", "Pre-award");
		competencyToCluster.put("C12 - Procurement strategy", "Pre-award");
		competencyToCluster.put("C13 - Technical specifications", "Pre-award");
		competencyToCluster.put("C14 - Tender documentation", "Pre-award");
		competencyToCluster.put("C15 - Tender evaluation", "Pre-award");
		competencyToCluster.put("C16 - Contract management", "Post-award");
		competencyToCluster.put("C17 - Certification and payment", "Post-award");
		competencyToCluster.put("C18 - Reporting and evaluation", "Post-award");
		competencyToCluster.put("C19 - Conflict resolution / mediation", "Post-award");
		competencyToCluster.put("C20 - Adaptability and modernisation", "Self");
		competencyToCluster.put("C21 - Analytical and critical thinking", "Self");
		competencyToCluster.put("C22 - Communication", "Self");
		competencyToCluster.put("C23 - Ethics and compliance", "Self");
		competencyToCluster.put("C24 - Collaboration", "People");
		competencyToCluster.put("C25 - Stakeholder relationship management", "People");
		competencyToCluster.put("C26 - Team management and Leadership", "People");
		competencyToCluster.put("C27 - Organisational awareness", "Performance");
		competencyToCluster.put("C28 - Project management", "Performance");
		competencyToCluster.put("C29 - Performance orientation", "Performance");
		competencyToCluster.put("C30a - Risk management and internal control", "Performance");
		competencyToCluster.put("C30b - Risk management and internal control", "Performance");
		return competencyToCluster;
	}

	public Map<String, Integer> defaultCompetenciesOrder() {
		Map<String, Integer> competencyToOrder = new HashMap<>();
		competencyToOrder.put("C1 - Planning", 1);
		competencyToOrder.put("C2 - Lifecycle", 2);
		competencyToOrder.put("C3 - Legislation", 3);
		competencyToOrder.put("C4 - e-Procurement and other IT tools", 4);
		competencyToOrder.put("C5 - Sustainable procurement", 5);
		competencyToOrder.put("C6 - Innovation Procurement", 6);
		competencyToOrder.put("C7 - Category specific", 7);
		competencyToOrder.put("C8 - Supplier management", 8);
		competencyToOrder.put("C9 - Negotiations", 9);
		competencyToOrder.put("C10 - Needs assessment", 10);
		competencyToOrder.put("C11 - Market analysis and market engagement", 11);
		competencyToOrder.put("C12 - Procurement strategy", 12);
		competencyToOrder.put("C13 - Technical specifications", 13);
		competencyToOrder.put("C14 - Tender documentation", 14);
		competencyToOrder.put("C15 - Tender evaluation", 15);
		competencyToOrder.put("C16 - Contract management", 16);
		competencyToOrder.put("C17 - Certification and payment", 17);
		competencyToOrder.put("C18 - Reporting and evaluation", 18);
		competencyToOrder.put("C19 - Conflict resolution / mediation", 19);
		competencyToOrder.put("C20 - Adaptability and modernisation", 20);
		competencyToOrder.put("C21 - Analytical and critical thinking", 21);
		competencyToOrder.put("C22 - Communication", 22);
		competencyToOrder.put("C23 - Ethics and compliance", 23);
		competencyToOrder.put("C24 - Collaboration", 24);
		competencyToOrder.put("C25 - Stakeholder relationship management", 25);
		competencyToOrder.put("C26 - Team management and Leadership", 26);
		competencyToOrder.put("C27 - Organisational awareness", 27);
		competencyToOrder.put("C28 - Project management", 28);
		competencyToOrder.put("C29 - Performance orientation", 29);
		competencyToOrder.put("C30a - Risk management and internal control", 30);
		competencyToOrder.put("C30b - Risk management and internal control", 30);
		return competencyToOrder;
	}

	public Map<String, Map<String, Integer>> defaultProfileNameToCompetencyName() {
		Map<String, Map<String, Integer>> profileToCompetencyToScore = new HashMap<>();
		profileToCompetencyToScore.put("Procurement support officer", this.defaultProcurementSupportOfficer());
		profileToCompetencyToScore.put("Standalone public buyer", this.defaultStandalonePublicBuyer());
		profileToCompetencyToScore.put("Public procurement specialist", this.defaultPublicProcurementSpecialist());
		profileToCompetencyToScore.put("Category specialist", this.defaultCategorySpecialist());
		profileToCompetencyToScore.put("Contract manager", this.defaultContractManager());
		profileToCompetencyToScore.put("Department manager", this.defaultDepartmentManager());
		profileToCompetencyToScore.put("Neutral profile", this.defaultNeutralProfile());
		return profileToCompetencyToScore;
	}

	public Map<String, Integer> defaultProfileNameToOrder() {
		Map<String, Integer> profileToOrder = new HashMap<>();
		profileToOrder.put("Procurement support officer", 0);
		profileToOrder.put("Standalone public buyer", 1);
		profileToOrder.put("Public procurement specialist", 2);
		profileToOrder.put("Category specialist", 3);
		profileToOrder.put("Contract manager", 4);
		profileToOrder.put("Department manager", 5);
		profileToOrder.put("Neutral profile", 6);
		return profileToOrder;
	}

	public Map<Integer, String> defaultQuestionNumberToAnswerText() {
		Map<Integer, String> questionNumberToAnswerText = new HashMap<>();
		questionNumberToAnswerText.put(0, "Knowledge question");
		questionNumberToAnswerText.put(1, "Skill question");
		return questionNumberToAnswerText;
	}

	public Map<Integer, Map<Integer, String>> defaultCompetencyNumberToQuestionNumberToText() {
		Map<Integer, Map<Integer, String>> competencyNumberToQuestionNumberToText = new HashMap<>();

		Map<Integer, String> questionNumberToText1 = new HashMap<>();
		questionNumberToText1.put(1,
				"<b>Knowledge question:</b><br/> How well do you know your organisation\'s procurement planning, policy priorities and budget?");
		questionNumberToText1.put(2,
				"<b>Skill question:</b><br/> To what extent are you able to develop a procurement plan according to available budget resources?");
		competencyNumberToQuestionNumberToText.put(1, questionNumberToText1);

		Map<Integer, String> questionNumberToText2 = new HashMap<>();
		questionNumberToText2.put(1,
				"<b>Knowledge question:</b><br/> How well do you know the different phases of the procurement lifecycle, from pre-publication to post-award?");
		questionNumberToText2.put(2,
				"<b>Skill question:</b><br/> To what extent are you able to follow the various phases of the procurement lifecycle?");
		competencyNumberToQuestionNumberToText.put(2, questionNumberToText2);

		Map<Integer, String> questionNumberToText3 = new HashMap<>();
		questionNumberToText3.put(1,
				"<b>Knowledge question:</b><br/> How well do you know the legislation on public procurement and other relevant areas of law?");
		questionNumberToText3.put(2,
				"<b>Skill question:</b><br/> To what extent are you able to apply specific aspects of the procurement legislation, as well as other legal frameworks impacting procurement ?");
		competencyNumberToQuestionNumberToText.put(3, questionNumberToText3);

		Map<Integer, String> questionNumberToText4 = new HashMap<>();
		questionNumberToText4.put(1,
				"<b>Knowledge question:</b><br/> How well do you know e-Procurement and other IT systems and tools?");
		questionNumberToText4.put(2,
				"<b>Skill question:</b><br/> To what extent are you able use e-procurement and other IT systems and tools?");
		competencyNumberToQuestionNumberToText.put(4, questionNumberToText4);

		Map<Integer, String> questionNumberToText5 = new HashMap<>();
		questionNumberToText5.put(1,
				"<b>Knowledge question:</b><br/> How well do you know the relevant sustainability policies and how to promote them?");
		questionNumberToText5.put(2,
				"<b>Skill question:</b><br/> How well do you know incorporate environmental and sustainable objectives set by the organisation and national policies into the procurement process?");
		competencyNumberToQuestionNumberToText.put(5, questionNumberToText5);

		Map<Integer, String> questionNumberToText6 = new HashMap<>();
		questionNumberToText6.put(1,
				"<b>Knowledge question:</b><br/> How well do you know the relevant innovation policies and how to promote them?");
		questionNumberToText6.put(2,
				"<b>Skill question:</b><br/> To what extent are you able to incorporate innovation objectives set by the organisation and national policies into the procurement process?");
		competencyNumberToQuestionNumberToText.put(6, questionNumberToText6);

		Map<Integer, String> questionNumberToText7 = new HashMap<>();
		questionNumberToText7.put(1,
				"<b>Knowledge question:</b><br/> How well do you know the features and specificities of one or more specific category of supplies, services or works?");
		questionNumberToText7.put(2,
				"<b>Skill question:</b><br/> To what extent are you able to get the most out of one or more category of supplies, services or works?");
		competencyNumberToQuestionNumberToText.put(7, questionNumberToText7);

		Map<Integer, String> questionNumberToText8 = new HashMap<>();
		questionNumberToText8.put(1,
				"<b>Knowledge question:</b><br/> How well do you know supplier management strategies and processes?");
		questionNumberToText8.put(2,
				"<b>Skill question:</b><br/> To what extent are you able to develop, manage and maintain relationship with suppliers while respecting public procurement principles?");
		competencyNumberToQuestionNumberToText.put(8, questionNumberToText8);

		Map<Integer, String> questionNumberToText9 = new HashMap<>();
		questionNumberToText9.put(1,
				"<b>Knowledge question:</b><br/> How well do you know negotiation processes relevant in public procurement?");
		questionNumberToText9.put(2,
				"<b>Skill question:</b><br/> To what extent are we able to apply negotiation processes strategies during the procurement phases and contract management in accordance with public procurement principles and ethical standards?");
		competencyNumberToQuestionNumberToText.put(9, questionNumberToText9);

		Map<Integer, String> questionNumberToText10 = new HashMap<>();
		questionNumberToText10.put(1,
				"<b>Knowledge question:</b><br/> How well do you know needs identification tools and techniques?");
		questionNumberToText10.put(2,
				"<b>Skill question:</b><br/> To what extent are you able to apply needs assessment techniques and tools for determining needs of the organisation and end-users regarding the subject-matter of the procurement?");
		competencyNumberToQuestionNumberToText.put(10, questionNumberToText10);

		Map<Integer, String> questionNumberToText11 = new HashMap<>();
		questionNumberToText11.put(1,
				"<b>Knowledge question:</b><br/> How well do you know market analysis tools and appropriate market engagement techniques?");
		questionNumberToText11.put(2,
				"<b>Skill question:</b><br/> To what extent are you able to use market analysis and market engagement techniques to understand the characteristics and trends of the supplier market?");
		competencyNumberToQuestionNumberToText.put(11, questionNumberToText11);

		Map<Integer, String> questionNumberToText12 = new HashMap<>();
		questionNumberToText12.put(1,
				"<b>Knowledge question:</b><br/> How well do you know the different procurement strategies, such as procedure types, use of lots, and kinds of contracts?");
		questionNumberToText12.put(2,
				"<b>Skill question:</b><br/> To what extent are you able to determine among the range of available procurement strategies the one that fits best to the procurement at stake while reaching the organisation\'s objectives?");
		competencyNumberToQuestionNumberToText.put(12, questionNumberToText12);

		Map<Integer, String> questionNumberToText13 = new HashMap<>();
		questionNumberToText13.put(1,
				"<b>Knowledge question:</b><br/> How well do you know the requirements of drafting technical specifications?");
		questionNumberToText13.put(2,
				"<b>Skill question:</b><br/> To what extent are you able to draft technical specifications that enable potential bidders to submit realistic offers that address the needs of the organisation?");
		competencyNumberToQuestionNumberToText.put(13, questionNumberToText13);

		Map<Integer, String> questionNumberToText14 = new HashMap<>();
		questionNumberToText14.put(1,
				"<b>Knowledge question:</b><br/> How well do you know the requirements of preparing tender documentation?");
		questionNumberToText14.put(2,
				"<b>Skill question:</b><br/> To what extent are you able to prepare procurement documentation including appropriate exclusion, selection and award criteria?");
		competencyNumberToQuestionNumberToText.put(14, questionNumberToText14);

		Map<Integer, String> questionNumberToText15 = new HashMap<>();
		questionNumberToText15.put(1, "Knowledge question:</b><br/> How well do you know the evaluation process?");
		questionNumberToText15.put(2,
				"<b>Skill question:</b><br/> To what extent are you able to evaluate offers against pre-defined criteria in an objective and transparent way?");
		competencyNumberToQuestionNumberToText.put(15, questionNumberToText15);

		Map<Integer, String> questionNumberToText16 = new HashMap<>();
		questionNumberToText16.put(1,
				"<b>Knowledge question:</b><br/> How well do you know the principles of contract management?");
		questionNumberToText16.put(2,
				"<b>Skill question:</b><br/> To what extent are you able to oversee contract implementation while ensuring technical compliance of the good, work or service delivered?");
		competencyNumberToQuestionNumberToText.put(16, questionNumberToText16);

		Map<Integer, String> questionNumberToText17 = new HashMap<>();
		questionNumberToText17.put(1,
				"<b>Knowledge question:</b><br/> How well do you know the process for certification and payment?");
		questionNumberToText17.put(2,
				"<b>Skill question:</b><br/> To what extent are you able to apply verification principles and the financial control framework to verify the legal compliance of the procurement contract before proceeding to payment?");
		competencyNumberToQuestionNumberToText.put(17, questionNumberToText17);

		Map<Integer, String> questionNumberToText18 = new HashMap<>();
		questionNumberToText18.put(1,
				"<b>Knowledge question:</b><br/> How well do you know contract monitoring tools and techniques?");
		questionNumberToText18.put(2,
				"<b>Skill question:</b><br/> To what extent are you able to evaluate the process, deliverables and outcomes of a procurement to draw lessons on how to improve the performance of future procurements?");
		competencyNumberToQuestionNumberToText.put(18, questionNumberToText18);

		Map<Integer, String> questionNumberToText19 = new HashMap<>();
		questionNumberToText19.put(1,
				"<b>Knowledge question:</b><br/> How well do you know conflict resolution and mediation processes and the functioning of the review system?");
		questionNumberToText19.put(2,
				"<b>Skill question:</b><br/> To what extent are you able to prevent and resolve conflicts and manage complaints in the framework of the national review system?");
		competencyNumberToQuestionNumberToText.put(19, questionNumberToText19);

		Map<Integer, String> questionNumberToText20 = new HashMap<>();
		questionNumberToText20.put(1,
				"<b>Knowledge question:</b><br/> How well do you know change management techniques and tools?");
		questionNumberToText20.put(2,
				"<b>Skill question:</b><br/> To what extent are you able to anticipate and accommodate to changing tasks and circumstances and aim to continuously learn and grow?");
		competencyNumberToQuestionNumberToText.put(20, questionNumberToText20);

		Map<Integer, String> questionNumberToText21 = new HashMap<>();
		questionNumberToText21.put(1,
				"<b>Knowledge question:</b><br/> How well do you know analytical and critical thinking approaches and tools?");
		questionNumberToText21.put(2,
				"<b>Skill question:</b><br/> To what extent are you able to use analytical and critical thinking in evaluating an information and/ or a situation and solving problems?");
		competencyNumberToQuestionNumberToText.put(21, questionNumberToText21);

		Map<Integer, String> questionNumberToText22 = new HashMap<>();
		questionNumberToText22.put(1,
				"<b>Knowledge question:</b><br/> How well do you know communication tools and techniques and how to apply the public procurement principles in various communication situations?");
		questionNumberToText22.put(2,
				"<b>Skill question:</b><br/> To what extent are you able to communicate effectively by adapting the communication medium and message to the target audience while ensuring public procurement principles are respected?");
		competencyNumberToQuestionNumberToText.put(22, questionNumberToText22);

		Map<Integer, String> questionNumberToText23 = new HashMap<>();
		questionNumberToText23.put(1,
				"<b>Knowledge question:</b><br/> How well do you know the procedural rules and principles as well as tools, codes and guidance document that help ensure adherence thereto?");
		questionNumberToText23.put(2,
				"<b>Skill question:</b><br/> To what extent are you able to ensure compliance with applicable public procurement rules, principles, and ethical standards?");
		competencyNumberToQuestionNumberToText.put(23, questionNumberToText23);

		Map<Integer, String> questionNumberToText24 = new HashMap<>();
		questionNumberToText24.put(1, "Knowledge question:</b><br/> How well do you know collaboration tools and techniques?");
		questionNumberToText24.put(2,
				"<b>Skill question:</b><br/> To what extent are you able to promote inclusive and collaborative thinking and processes?");
		competencyNumberToQuestionNumberToText.put(24, questionNumberToText24);

		Map<Integer, String> questionNumberToText25 = new HashMap<>();
		questionNumberToText25.put(1,
				"<b>Knowledge question:</b><br/> How well do you know the key concepts and methods of stakeholder management?");
		questionNumberToText25.put(2,
				"<b>Skill question:</b><br/> To what extent are you able to create mutual trust that contribute to solid internal and external stakeholder relationships?");
		competencyNumberToQuestionNumberToText.put(25, questionNumberToText25);

		Map<Integer, String> questionNumberToText26 = new HashMap<>();
		questionNumberToText26.put(1,
				"<b>Knowledge question:</b><br/> How well do you know the key concepts and methods of team management?");
		questionNumberToText26.put(2,
				"<b>Skill question:</b><br/> To what extent are you able to tailor management and leadership methods and techniques to the team and circumstances thereby creating a conducive environment for achieving common goals?");
		competencyNumberToQuestionNumberToText.put(26, questionNumberToText26);

		Map<Integer, String> questionNumberToText27 = new HashMap<>();
		questionNumberToText27.put(1,
				"<b>Knowledge question:</b><br/> How well do you know your organisation\'s administrative structure, procedures and processes, internal culture and legal and policy framework?");
		questionNumberToText27.put(2,
				"<b>Skill question:</b><br/> To what extent are you able to understand both the procurement function and the organisation\'s structure and culture?");
		competencyNumberToQuestionNumberToText.put(27, questionNumberToText27);

		Map<Integer, String> questionNumberToText28 = new HashMap<>();
		questionNumberToText28.put(1,
				"<b>Knowledge question:</b><br/> How well do you know project management tools and techniques relevant for the public administration?");
		questionNumberToText28.put(2,
				"<b>Skill question:</b><br/> To what extent are you able to apply project management tools and techniques to effectively carry out a procurement procedure and contract ?");
		competencyNumberToQuestionNumberToText.put(28, questionNumberToText28);

		Map<Integer, String> questionNumberToText29 = new HashMap<>();
		questionNumberToText29.put(1,
				"<b>Knowledge question:</b><br/> How well do you know cost and performance management strategies and methods as well as Key Performance Indicators (KPIs) that help identify inefficiencies and monitor the financial performance of the procurement and the way it delivers value for money?");
		questionNumberToText29.put(2,
				"<b>Skill question:</b><br/> To what extent are you able to apply commercial and performance management strategies and methods to maximise value for money of procurement contracts?");
		competencyNumberToQuestionNumberToText.put(29, questionNumberToText29);

		Map<Integer, String> questionNumberToText30 = new HashMap<>();
		questionNumberToText30.put(1, "<b>Knowledge question:</b><br/> How well do you know audit and control functions?");
		questionNumberToText30.put(2,
				"<b>Skill question:</b><br/> To what extent are you able to carry out the different functions of inspection, control, audit, and evaluation applicable to public procurement?");
		competencyNumberToQuestionNumberToText.put(30, questionNumberToText30);
		
		Map<Integer, String> questionNumberToText31 = new HashMap<>();		
		questionNumberToText31.put(1, "<b>Knowledge question:</b><br/> How well do you know audit risk management tools and techniques?");
		questionNumberToText31.put(2, "<b>Skill question:</b><br/> To what extent are you able to monitor risks closely and apply mitigating measures and proactive approaches to protect the interest of the organisation?");
		competencyNumberToQuestionNumberToText.put(31, questionNumberToText31);

		return competencyNumberToQuestionNumberToText;
	}

	public Map<Integer, Map<Integer, String>> defaultQuestionNumberToAnswerToText() {
		Map<Integer, String> answerNumberToText0 = new HashMap<>();
		answerNumberToText0.put(0, "I have no knowledge");
		answerNumberToText0.put(1, "I have basic knowledge");
		answerNumberToText0.put(2, "I have intermediate knowledge");
		answerNumberToText0.put(3, "I have advanced knowledge");
		answerNumberToText0.put(4, "I have expert knowledge");

		Map<Integer, String> answerNumberToText1 = new HashMap<>();
		answerNumberToText1.put(0, "I have no skills");
		answerNumberToText1.put(1, "I have basic skills");
		answerNumberToText1.put(2, "I have intermediate skills");
		answerNumberToText1.put(3, "I have advanced skills");
		answerNumberToText1.put(4, "I have expert skills");

		Map<Integer, String> answerNumberToText2 = new HashMap<>();
		answerNumberToText0.put(0, "I have no knowledge");
		answerNumberToText0.put(1, "I have basic knowledge");
		answerNumberToText0.put(2, "I have intermediate knowledge");
		answerNumberToText0.put(3, "I have advanced knowledge");
		answerNumberToText0.put(4, "I have expert knowledge");

		Map<Integer, String> answerNumberToText3 = new HashMap<>();
		answerNumberToText1.put(0, "I have no skills");
		answerNumberToText1.put(1, "I have basic skills");
		answerNumberToText1.put(2, "I have intermediate skills");
		answerNumberToText1.put(3, "I have advanced skills");
		answerNumberToText1.put(4, "I have expert skills");

		Map<Integer, Map<Integer, String>> questionNumberToAnswerToText = new HashMap<>();
		questionNumberToAnswerToText.put(0, answerNumberToText0);
		questionNumberToAnswerToText.put(1, answerNumberToText1);
		questionNumberToAnswerToText.put(2, answerNumberToText0);
		questionNumberToAnswerToText.put(3, answerNumberToText1);

		return questionNumberToAnswerToText;
	}

	private Map<String, Integer> defaultNeutralProfile() {
		Map<String, Integer> competencyToScore = new HashMap<>();
		competencyToScore.put("C1 - Planning", 0);
		competencyToScore.put("C2 - Lifecycle", 0);
		competencyToScore.put("C3 - Legislation", 0);
		competencyToScore.put("C4 - e-Procurement and other IT tools", 0);
		competencyToScore.put("C5 - Sustainable procurement", 0);
		competencyToScore.put("C6 - Innovation Procurement", 0);
		competencyToScore.put("C7 - Category specific", 0);
		competencyToScore.put("C8 - Supplier management", 0);
		competencyToScore.put("C9 - Negotiations", 0);
		competencyToScore.put("C10 - Needs assessment", 0);
		competencyToScore.put("C11 - Market analysis and market engagement", 0);
		competencyToScore.put("C12 - Procurement strategy", 0);
		competencyToScore.put("C13 - Technical specifications", 0);
		competencyToScore.put("C14 - Tender documentation", 0);
		competencyToScore.put("C15 - Tender evaluation", 0);
		competencyToScore.put("C16 - Contract management", 0);
		competencyToScore.put("C17 - Certification and payment", 0);
		competencyToScore.put("C18 - Reporting and evaluation", 0);
		competencyToScore.put("C19 - Conflict resolution / mediation", 0);
		competencyToScore.put("C20 - Adaptability and modernisation", 0);
		competencyToScore.put("C21 - Analytical and critical thinking", 0);
		competencyToScore.put("C22 - Communication", 0);
		competencyToScore.put("C23 - Ethics and compliance", 0);
		competencyToScore.put("C24 - Collaboration", 0);
		competencyToScore.put("C25 - Stakeholder relationship management", 0);
		competencyToScore.put("C26 - Team management and Leadership", 0);
		competencyToScore.put("C27 - Organisational awareness", 0);
		competencyToScore.put("C28 - Project management", 0);
		competencyToScore.put("C29 - Performance orientation", 0);
		competencyToScore.put("C30a - Risk management and internal control", 0);
		competencyToScore.put("C30b - Risk management and internal control", 0);
		return competencyToScore;
	}

	private Map<String, Integer> defaultDepartmentManager() {
		Map<String, Integer> competencyToScore = new HashMap<>();
		competencyToScore.put("C1 - Planning", 3);
		competencyToScore.put("C2 - Lifecycle", 3);
		competencyToScore.put("C3 - Legislation", 4);
		competencyToScore.put("C4 - e-Procurement and other IT tools", 2);
		competencyToScore.put("C5 - Sustainable procurement", 3);
		competencyToScore.put("C6 - Innovation Procurement", 3);
		competencyToScore.put("C7 - Category specific", 0);
		competencyToScore.put("C8 - Supplier management", 2);
		competencyToScore.put("C9 - Negotiations", 3);
		competencyToScore.put("C10 - Needs assessment", 3);
		competencyToScore.put("C11 - Market analysis and market engagement", 0);
		competencyToScore.put("C12 - Procurement strategy", 3);
		competencyToScore.put("C13 - Technical specifications", 0);
		competencyToScore.put("C14 - Tender documentation", 0);
		competencyToScore.put("C15 - Tender evaluation", 2);
		competencyToScore.put("C16 - Contract management", 2);
		competencyToScore.put("C17 - Certification and payment", 2);
		competencyToScore.put("C18 - Reporting and evaluation", 3);
		competencyToScore.put("C19 - Conflict resolution / mediation", 2);
		competencyToScore.put("C20 - Adaptability and modernisation", 3);
		competencyToScore.put("C21 - Analytical and critical thinking", 4);
		competencyToScore.put("C22 - Communication", 3);
		competencyToScore.put("C23 - Ethics and compliance", 4);
		competencyToScore.put("C24 - Collaboration", 3);
		competencyToScore.put("C25 - Stakeholder relationship management", 4);
		competencyToScore.put("C26 - Team management and Leadership", 3);
		competencyToScore.put("C27 - Organisational awareness", 4);
		competencyToScore.put("C28 - Project management", 3);
		competencyToScore.put("C29 - Performance orientation", 3);
		competencyToScore.put("C30a - Risk management and internal control", 4);
		competencyToScore.put("C30b - Risk management and internal control", 4);
		return competencyToScore;
	}

	private Map<String, Integer> defaultContractManager() {
		Map<String, Integer> competencyToScore = new HashMap<>();
		competencyToScore.put("C1 - Planning", 2);
		competencyToScore.put("C2 - Lifecycle", 2);
		competencyToScore.put("C3 - Legislation", 2);
		competencyToScore.put("C4 - e-Procurement and other IT tools", 1);
		competencyToScore.put("C5 - Sustainable procurement", 2);
		competencyToScore.put("C6 - Innovation Procurement", 2);
		competencyToScore.put("C7 - Category specific", 0);
		competencyToScore.put("C8 - Supplier management", 2);
		competencyToScore.put("C9 - Negotiations", 2);
		competencyToScore.put("C10 - Needs assessment", 0);
		competencyToScore.put("C11 - Market analysis and market engagement", 0);
		competencyToScore.put("C12 - Procurement strategy", 0);
		competencyToScore.put("C13 - Technical specifications", 0);
		competencyToScore.put("C14 - Tender documentation", 0);
		competencyToScore.put("C15 - Tender evaluation", 0);
		competencyToScore.put("C16 - Contract management", 3);
		competencyToScore.put("C17 - Certification and payment", 3);
		competencyToScore.put("C18 - Reporting and evaluation", 2);
		competencyToScore.put("C19 - Conflict resolution / mediation", 2);
		competencyToScore.put("C20 - Adaptability and modernisation", 2);
		competencyToScore.put("C21 - Analytical and critical thinking", 3);
		competencyToScore.put("C22 - Communication", 3);
		competencyToScore.put("C23 - Ethics and compliance", 3);
		competencyToScore.put("C24 - Collaboration", 2);
		competencyToScore.put("C25 - Stakeholder relationship management", 3);
		competencyToScore.put("C26 - Team management and Leadership", 0);
		competencyToScore.put("C27 - Organisational awareness", 2);
		competencyToScore.put("C28 - Project management", 2);
		competencyToScore.put("C29 - Performance orientation", 3);
		competencyToScore.put("C30a - Risk management and internal control", 3);
		competencyToScore.put("C30b - Risk management and internal control", 3);
		return competencyToScore;
	}

	private Map<String, Integer> defaultCategorySpecialist() {
		Map<String, Integer> competencyToScore = new HashMap<>();
		competencyToScore.put("C1 - Planning", 2);
		competencyToScore.put("C2 - Lifecycle", 3);
		competencyToScore.put("C3 - Legislation", 1);
		competencyToScore.put("C4 - e-Procurement and other IT tools", 1);
		competencyToScore.put("C5 - Sustainable procurement", 3);
		competencyToScore.put("C6 - Innovation Procurement", 3);
		competencyToScore.put("C7 - Category specific", 3);
		competencyToScore.put("C8 - Supplier management", 2);
		competencyToScore.put("C9 - Negotiations", 0);
		competencyToScore.put("C10 - Needs assessment", 2);
		competencyToScore.put("C11 - Market analysis and market engagement", 2);
		competencyToScore.put("C12 - Procurement strategy", 1);
		competencyToScore.put("C13 - Technical specifications", 3);
		competencyToScore.put("C14 - Tender documentation", 1);
		competencyToScore.put("C15 - Tender evaluation", 1);
		competencyToScore.put("C16 - Contract management", 2);
		competencyToScore.put("C17 - Certification and payment", 0);
		competencyToScore.put("C18 - Reporting and evaluation", 0);
		competencyToScore.put("C19 - Conflict resolution / mediation", 0);
		competencyToScore.put("C20 - Adaptability and modernisation", 2);
		competencyToScore.put("C21 - Analytical and critical thinking", 2);
		competencyToScore.put("C22 - Communication", 1);
		competencyToScore.put("C23 - Ethics and compliance", 1);
		competencyToScore.put("C24 - Collaboration", 1);
		competencyToScore.put("C25 - Stakeholder relationship management", 1);
		competencyToScore.put("C26 - Team management and Leadership", 0);
		competencyToScore.put("C27 - Organisational awareness", 1);
		competencyToScore.put("C28 - Project management", 0);
		competencyToScore.put("C29 - Performance orientation", 2);
		competencyToScore.put("C30a - Risk management and internal control", 1);
		competencyToScore.put("C30b - Risk management and internal control", 1);
		return competencyToScore;
	}

	private Map<String, Integer> defaultPublicProcurementSpecialist() {
		Map<String, Integer> competencyToScore = new HashMap<>();
		competencyToScore.put("C1 - Planning", 1);
		competencyToScore.put("C2 - Lifecycle", 3);
		competencyToScore.put("C3 - Legislation", 1);
		competencyToScore.put("C4 - e-Procurement and other IT tools", 2);
		competencyToScore.put("C5 - Sustainable procurement", 2);
		competencyToScore.put("C6 - Innovation Procurement", 2);
		competencyToScore.put("C7 - Category specific", 1);
		competencyToScore.put("C8 - Supplier management", 1);
		competencyToScore.put("C9 - Negotiations", 2);
		competencyToScore.put("C10 - Needs assessment", 2);
		competencyToScore.put("C11 - Market analysis and market engagement", 2);
		competencyToScore.put("C12 - Procurement strategy", 2);
		competencyToScore.put("C13 - Technical specifications", 2);
		competencyToScore.put("C14 - Tender documentation", 2);
		competencyToScore.put("C15 - Tender evaluation", 2);
		competencyToScore.put("C16 - Contract management", 1);
		competencyToScore.put("C17 - Certification and payment", 1);
		competencyToScore.put("C18 - Reporting and evaluation", 2);
		competencyToScore.put("C19 - Conflict resolution / mediation", 1);
		competencyToScore.put("C20 - Adaptability and modernisation", 1);
		competencyToScore.put("C21 - Analytical and critical thinking", 2);
		competencyToScore.put("C22 - Communication", 2);
		competencyToScore.put("C23 - Ethics and compliance", 2);
		competencyToScore.put("C24 - Collaboration", 2);
		competencyToScore.put("C25 - Stakeholder relationship management", 2);
		competencyToScore.put("C26 - Team management and Leadership", 1);
		competencyToScore.put("C27 - Organisational awareness", 2);
		competencyToScore.put("C28 - Project management", 2);
		competencyToScore.put("C29 - Performance orientation", 2);
		competencyToScore.put("C30a - Risk management and internal control", 2);
		competencyToScore.put("C30b - Risk management and internal control", 2);
		return competencyToScore;
	}

	private Map<String, Integer> defaultStandalonePublicBuyer() {
		Map<String, Integer> competencyToScore = new HashMap<>();
		competencyToScore.put("C1 - Planning", 1);
		competencyToScore.put("C2 - Lifecycle", 2);
		competencyToScore.put("C3 - Legislation", 2);
		competencyToScore.put("C4 - e-Procurement and other IT tools", 2);
		competencyToScore.put("C5 - Sustainable procurement", 1);
		competencyToScore.put("C6 - Innovation Procurement", 1);
		competencyToScore.put("C7 - Category specific", 1);
		competencyToScore.put("C8 - Supplier management", 1);
		competencyToScore.put("C9 - Negotiations", 2);
		competencyToScore.put("C10 - Needs assessment", 1);
		competencyToScore.put("C11 - Market analysis and market engagement", 2);
		competencyToScore.put("C12 - Procurement strategy", 2);
		competencyToScore.put("C13 - Technical specifications", 2);
		competencyToScore.put("C14 - Tender documentation", 2);
		competencyToScore.put("C15 - Tender evaluation", 2);
		competencyToScore.put("C16 - Contract management", 2);
		competencyToScore.put("C17 - Certification and payment", 2);
		competencyToScore.put("C18 - Reporting and evaluation", 2);
		competencyToScore.put("C19 - Conflict resolution / mediation", 1);
		competencyToScore.put("C20 - Adaptability and modernisation", 2);
		competencyToScore.put("C21 - Analytical and critical thinking", 2);
		competencyToScore.put("C22 - Communication", 2);
		competencyToScore.put("C23 - Ethics and compliance", 3);
		competencyToScore.put("C24 - Collaboration", 1);
		competencyToScore.put("C25 - Stakeholder relationship management", 1);
		competencyToScore.put("C26 - Team management and Leadership", 1);
		competencyToScore.put("C27 - Organisational awareness", 2);
		competencyToScore.put("C28 - Project management", 2);
		competencyToScore.put("C29 - Performance orientation", 2);
		competencyToScore.put("C30a - Risk management and internal control", 2);
		competencyToScore.put("C30b - Risk management and internal control", 2);
		return competencyToScore;
	}

	private Map<String, Integer> defaultProcurementSupportOfficer() {
		Map<String, Integer> competencyToScore = new HashMap<>();
		competencyToScore.put("C1 - Planning", 0);
		competencyToScore.put("C2 - Lifecycle", 1);
		competencyToScore.put("C3 - Legislation", 0);
		competencyToScore.put("C4 - e-Procurement and other IT tools", 1);
		competencyToScore.put("C5 - Sustainable procurement", 0);
		competencyToScore.put("C6 - Innovation Procurement", 0);
		competencyToScore.put("C7 - Category specific", 0);
		competencyToScore.put("C8 - Supplier management", 1);
		competencyToScore.put("C9 - Negotiations", 0);
		competencyToScore.put("C10 - Needs assessment", 1);
		competencyToScore.put("C11 - Market analysis and market engagement", 1);
		competencyToScore.put("C12 - Procurement strategy", 0);
		competencyToScore.put("C13 - Technical specifications", 1);
		competencyToScore.put("C14 - Tender documentation", 1);
		competencyToScore.put("C15 - Tender evaluation", 0);
		competencyToScore.put("C16 - Contract management", 1);
		competencyToScore.put("C17 - Certification and payment", 1);
		competencyToScore.put("C18 - Reporting and evaluation", 1);
		competencyToScore.put("C19 - Conflict resolution / mediation", 0);
		competencyToScore.put("C20 - Adaptability and modernisation", 0);
		competencyToScore.put("C21 - Analytical and critical thinking", 1);
		competencyToScore.put("C22 - Communication", 1);
		competencyToScore.put("C23 - Ethics and compliance", 2);
		competencyToScore.put("C24 - Collaboration", 2);
		competencyToScore.put("C25 - Stakeholder relationship management", 1);
		competencyToScore.put("C26 - Team management and Leadership", 0);
		competencyToScore.put("C27 - Organisational awareness", 2);
		competencyToScore.put("C28 - Project management", 1);
		competencyToScore.put("C29 - Performance orientation", 1);
		competencyToScore.put("C30a - Risk management and internal control", 1);
		competencyToScore.put("C30b - Risk management and internal control", 1);
		return competencyToScore;
	}

	/**
	 * Creates the ECF profiles defined for the matrix
	 */
	@Transactional
	public Map<ECFProfile, Map<String, Integer>> createECFProfileToCompetencyNameToScore(
			Map<String, Map<String, Integer>> profileNameToCompetencyToScore, Map<String, Integer> profileNameToOrder) {
		Session session = sessionFactory.getCurrentSession();

		Map<ECFProfile, Map<String, Integer>> profileToCompetencyToScore = new HashMap<>();

		for (String profileName : profileNameToCompetencyToScore.keySet()) {
			ECFProfile ecfProfile = new ECFProfile(UUID.randomUUID().toString(), profileName,
					profileName + " description", profileNameToOrder.get(profileName));
			session.saveOrUpdate(ecfProfile);

			Map<String, Integer> competencyNameToScore = profileNameToCompetencyToScore.get(profileName);
			profileToCompetencyToScore.put(ecfProfile, competencyNameToScore);
		}

		return profileToCompetencyToScore;
	}

	/**
	 * 
	 */
	@Transactional
	public Map<String, ECFType> createClusterNameToType(Map<String, String> clusterToTypeNames) {
		Session session = sessionFactory.getCurrentSession();

		Map<String, ECFType> clusterNameToType = new HashMap<>();
		Map<String, ECFType> typeNameToType = new HashMap<>();

		for (String clusterName : clusterToTypeNames.keySet()) {
			String typeName = clusterToTypeNames.get(clusterName);
			ECFType type = null;

			if (typeNameToType.containsKey(typeName)) {
				 type = typeNameToType.get(typeName);
			} else {
				type = new ECFType(UUID.randomUUID().toString(), typeName);
				session.saveOrUpdate(type);
				typeNameToType.put(typeName, type);
			}
			
			clusterNameToType.put(clusterName, type);
		}
		logger.info("created " + typeNameToType.size() + " ECF types");

		return clusterNameToType;
	}

	@Transactional
	public Map<String, ECFCluster> createCompetencyNameToCluster(Map<String, ECFType> clusterNameToType,
			Map<String, String> competencyToClusterNames) {
		Session session = sessionFactory.getCurrentSession();
		Map<String, ECFCluster> clusterNameToCluster = new HashMap<>();
		for (String clusterName : clusterNameToType.keySet()) {
			ECFType ecfType = clusterNameToType.get(clusterName);
			ECFCluster cluster = new ECFCluster(UUID.randomUUID().toString(), clusterName, ecfType);

			session.saveOrUpdate(cluster);
			clusterNameToCluster.put(clusterName, cluster);
		}

		logger.info("created " + clusterNameToCluster.size() + " ECF Clusters");

		Map<String, ECFCluster> competencyNameToCluster = new HashMap<>();
		for (String competencyName : competencyToClusterNames.keySet()) {
			String clusterName = competencyToClusterNames.get(competencyName);
			ECFCluster cluster = clusterNameToCluster.get(clusterName);
			competencyNameToCluster.put(competencyName, cluster);
		}

		return competencyNameToCluster;
	}

	/**
	 * Creates the ECF competencies defined for the matrix
	 */
	@Transactional
	public Set<ECFCompetency> createECFCompetencies(Map<ECFProfile, Map<String, Integer>> profileToCompetencyToScore,
			Map<String, ECFCluster> competencyToCluster, Map<String, Integer> competencyToOrder) {
		Session session = sessionFactory.getCurrentSession();

		Map<String, ECFCompetency> competencyNameToCompetency = new HashMap<>();

		// Go through the profiles
		for (ECFProfile profile : profileToCompetencyToScore.keySet()) {
			Map<String, Integer> competencyNameToScore = profileToCompetencyToScore.get(profile);

			// Go through the competencies and their scores
			for (String competencyName : competencyNameToScore.keySet()) {
				Integer expectedScoreI = competencyNameToScore.get(competencyName);
				ECFCompetency ecfCompetency = null;
				ECFCluster cluster = competencyToCluster.get(competencyName);
				if (competencyNameToCompetency.containsKey(competencyName)) {
					ecfCompetency = competencyNameToCompetency.get(competencyName);
				} else {
					ecfCompetency = new ECFCompetency(UUID.randomUUID().toString(), competencyName,
							competencyName + " description", cluster, competencyToOrder.get(competencyName));
					session.saveOrUpdate(ecfCompetency);
					competencyNameToCompetency.put(competencyName, ecfCompetency);
				}

				ECFExpectedScoreToProfileEid eid = new ECFExpectedScoreToProfileEid();
				eid.setECFCompetency(ecfCompetency);
				eid.setECFProfile(profile);
				ECFExpectedScore expectedScore = new ECFExpectedScore(eid, expectedScoreI);
				session.saveOrUpdate(expectedScore);

				profile.addECFExpectedScore(expectedScore);
				ecfCompetency.addECFExpectedScore(expectedScore);
			}
		}

		logger.info("created " + competencyNameToCompetency.size() + " ECF competencies");
		return competencyNameToCompetency.values().stream().collect(Collectors.toSet());
	}

	public Set<ECFProfile> getECFProfiles(Survey ecfSurvey) {
		if (ecfSurvey == null || !ecfSurvey.getIsECF()) {
			throw new IllegalArgumentException("survey needs to be ECF to get its profile");
		}
		Set<ECFProfile> profileSet = new HashSet<>();
		for (Element element : ecfSurvey.getElementsRecursive(true)) {
			if (element instanceof PossibleAnswer) {
				PossibleAnswer possibleAnswer = (PossibleAnswer) element;
				if (possibleAnswer.getEcfProfile() != null) {
					profileSet.add(possibleAnswer.getEcfProfile());
				}
			}
		}
		return profileSet;
	}

	@Transactional
	public Survey copySurveyECFElements(Survey alreadyCopiedSurvey) throws ECFException {
		Session session = sessionFactory.getCurrentSession();
		if (alreadyCopiedSurvey == null || !alreadyCopiedSurvey.getIsECF()) {
			throw new IllegalArgumentException("survey needs to be ECF to copy its ECF elements");
		}

		Map<String, ECFCompetency> oldECFCompetencyToNew = new HashMap<>();
		Map<String, ECFProfile> oldECFProfileToNew = new HashMap<>();
		Map<String, ECFCluster> oldECFClusterToNew = new HashMap<>();
		Map<String, ECFType> oldECFTypeToNew = new HashMap<>();

		Set<ECFExpectedScore> encounteredScores = new HashSet<>();

		for (Element element : alreadyCopiedSurvey.getElementsRecursive(true)) {
			if (element instanceof Question) {
				Question question = (Question) element;
				if (question instanceof SingleChoiceQuestion && question.getEcfCompetency() != null) {
					for (ECFExpectedScore score : question.getEcfCompetency().getECFExpectedScores()) {
						encounteredScores.add(score);
					}
					ECFCompetency oldCompetency = question.getEcfCompetency();

					ECFCompetency newCompetency = null;

					if (!oldECFCompetencyToNew.containsKey(oldCompetency.getCompetenceUid())) {
						// COPYING THE COMPETENCY AND ALL ITS INNER COMPONENTS
						newCompetency = oldCompetency.copy();

						ECFCluster oldCluster = oldCompetency.getEcfCluster();
						ECFCluster newCluster = null;
						if (!oldECFClusterToNew.containsKey(oldCluster.getUid())) {
							newCluster = oldCluster.copy();

							ECFType oldType = oldCluster.getEcfType();
							ECFType newType = null;

							if (!oldECFTypeToNew.containsKey(oldType.getUid())) {
								newType = oldType.copy();

								// SAVING TYPE
								session.persist(newType);
								oldECFTypeToNew.put(oldType.getUid(), newType);
							} else {
								newType = oldECFTypeToNew.get(oldType.getUid());
							}

							newCluster.setEcfType(newType);
							newType.addCluster(newCluster);

							// SAVING CLUSTER
							session.persist(newCluster);
							oldECFClusterToNew.put(oldCluster.getUid(), newCluster);
						} else {
							newCluster = oldECFClusterToNew.get(oldCluster.getUid());
						}
						newCompetency.setEcfCluster(newCluster);
						newCluster.addCompetency(newCompetency);

						// SAVING COMPETENCY
						session.persist(newCompetency);
						oldECFCompetencyToNew.put(oldCompetency.getCompetenceUid(), newCompetency);
					} else {
						newCompetency = oldECFCompetencyToNew.get(oldCompetency.getCompetenceUid());
					}

					question.setEcfCompetency(newCompetency);
				}
			}
			if (element instanceof PossibleAnswer) {
				PossibleAnswer possibleAnswer = (PossibleAnswer) element;

				if (possibleAnswer.getEcfProfile() != null) {
					for (ECFExpectedScore score : possibleAnswer.getEcfProfile().getECFExpectedScores()) {
						encounteredScores.add(score);
					}

					ECFProfile oldProfile = possibleAnswer.getEcfProfile();

					ECFProfile newProfile = null;
					if (!oldECFProfileToNew.containsKey(oldProfile.getProfileUid())) {
						newProfile = oldProfile.copy();
						session.persist(newProfile);
						oldECFProfileToNew.put(oldProfile.getProfileUid(), newProfile);
					} else {
						newProfile = oldECFProfileToNew.get(oldProfile.getProfileUid());
					}

					possibleAnswer.setEcfProfile(newProfile);
				}
			}
		}

		for (ECFExpectedScore score : encounteredScores) {
			ECFCompetency oldScoreCompetency = score.getECFExpectedScoreToProfileEid().getECFCompetency();
			ECFProfile oldScoreProfile = score.getECFExpectedScoreToProfileEid().getECFProfile();
			Integer oldScore = score.getScore();

			ECFCompetency newScoreCompetency = oldECFCompetencyToNew.get(oldScoreCompetency.getCompetenceUid());
			ECFProfile newScoreProfile = oldECFProfileToNew.get(oldScoreProfile.getProfileUid());

			if (newScoreCompetency == null) {
				throw new ECFException("Competency " + oldScoreCompetency.getCompetenceUid() + " was not well copied");
			}
			if (newScoreProfile == null) {
				throw new ECFException("Profile " + oldScoreProfile.getProfileUid() + " was not well copied");
			}

			ECFExpectedScoreToProfileEid eid = new ECFExpectedScoreToProfileEid();
			eid.setECFCompetency(newScoreCompetency);
			eid.setECFProfile(newScoreProfile);
			ECFExpectedScore newScore = new ECFExpectedScore(eid, oldScore);

			session.persist(newScore);

			newScoreCompetency.addECFExpectedScore(newScore);
			newScoreProfile.addECFExpectedScore(newScore);

			session.persist(newScoreCompetency);
			session.persist(newScoreProfile);
		}

		logger.info("copied " + oldECFClusterToNew.size() + " clusters");
		for (String oldCluster: oldECFClusterToNew.keySet()) {
			logger.info("cluster "+oldCluster);
		}

		logger.info("copied "+ oldECFTypeToNew.size() + " types");
		for (String oldType: oldECFTypeToNew.keySet()) {
			logger.info("type "+oldType);
		} 
		

		return alreadyCopiedSurvey;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public ECFProfile getECFProfileByUUID(final String profileUid) {
		Session session = sessionFactory.getCurrentSession();

		String hql = "FROM ECFProfile E WHERE E.profileUid = :profileUid";
		Query query = session.createQuery(hql);
		query.setParameter("profileUid", profileUid);

		List<ECFProfile> result = query.list();
		return result.isEmpty() ? null : result.get(0);
	}

	private float roundedAverage(Integer totalScore, Integer numberOfScores) {
		return (float) Math.round((totalScore.floatValue() / numberOfScores) * 10) / 10;
	}

	private List<AnswerSet> getAnswers(Survey survey, ECFProfile profileFilter, String orderBy,
			SqlPagination sqlPagination) throws Exception {
		ResultFilter resultFilter = new ResultFilter();

		if (profileFilter != null) {
			resultFilter.setAnsweredECFProfileUID(profileFilter.getProfileUid());
		}

		if (orderBy != null) {
			ResultFilterOrderBy resultFilterOrderBy = ResultFilter.ResultFilterOrderBy.parse(orderBy);
			resultFilter.setSortKey(resultFilterOrderBy.toResultFilterSortKey().value());
			resultFilter.setSortOrder(resultFilterOrderBy.toAscOrDesc());
		}

		return answerService.getAnswers(survey, resultFilter, sqlPagination, false, false, true);
	}

	private List<AnswerSet> getAnswers(Survey survey, ECFProfile profile, SqlPagination sqlPagination)
			throws Exception {
		return this.getAnswers(survey, profile, null, sqlPagination);
	}

	private List<AnswerSet> getAnswers(Survey survey, String orderBy, SqlPagination sqlPagination) throws Exception {
		return this.getAnswers(survey, null, orderBy, sqlPagination);
	}

	private List<AnswerSet> getAnswers(Survey survey, SqlPagination sqlPagination) throws Exception {
		return this.getAnswers(survey, null, null, sqlPagination);
	}

	private Integer getCount(Survey survey, ECFProfile profileFilter) throws Exception {
		ResultFilter resultFilter = new ResultFilter();
		resultFilter.setAnsweredECFProfileUID(profileFilter.getProfileUid());
		return this.answerService.getNumberOfAnswerSets(survey, resultFilter);
	}

	private Integer getCount(Survey survey) throws Exception {
		ResultFilter resultFilter = new ResultFilter();
		return this.answerService.getNumberOfAnswerSets(survey, resultFilter);
	}

	public List<String> spiderChartsB64ByECFType(ECFIndividualResult individualResult) {
		List<TypeUUIDAndName> typeAndNames = individualResult.getCompetenciesTypes();

		// Empty list map from type UUIDS
		Map<String, List<ECFIndividualCompetencyResult>> typeToResultList = new HashMap<>();
		for (TypeUUIDAndName typeAndName : typeAndNames) {
			String typeUUID = typeAndName.getTypeUUID();
			List<ECFIndividualCompetencyResult> emptyList = new ArrayList<>();
			typeToResultList.put(typeUUID, emptyList);
		}


		// Going through the competency result lists and adding those corresponding to the type
		for (ECFIndividualCompetencyResult competencyResult : individualResult.getCompetencyResultList()) {
			List<ECFIndividualCompetencyResult> competencyResultList = typeToResultList.get(competencyResult.getTypeUUID());
			if (competencyResultList != null) {
				competencyResultList.add(competencyResult);
			}
		}

		// For each Type UUID, generating the spider chart
		Map<String, String> typesToB64 = new HashMap<>();
		for (String type : typeToResultList.keySet()) {
			List<ECFIndividualCompetencyResult> competencyResultList = typeToResultList.get(type);
			String spiderChartB64 = this.spiderChartB64(competencyResultList,"");
			typesToB64.put(type, spiderChartB64);
		}

		return typeAndNames.stream().map(typeAndName -> typesToB64.get(typeAndName.getTypeUUID())).collect(Collectors.toList());
	}

	private String spiderChartB64(List<ECFIndividualCompetencyResult> competencyResultList, String title) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (ECFIndividualCompetencyResult competencyResult : competencyResultList) {
			int competencyScore = competencyResult.getCompetencyScore();
			int competencyTargetScore = competencyResult.getCompetencyTargetScore();

			dataset.addValue(competencyScore, "Score", competencyResult.getCompetencyName());
			dataset.addValue(competencyTargetScore, "Target score", competencyResult.getCompetencyName());
		}

		SpiderWebPlot plot = new SpiderWebPlot(dataset);
		plot.setSeriesPaint(0, new Color(177, 22, 48));
		plot.setSeriesPaint(1, new Color(62, 116, 170));
		plot.setLabelPaint(new Color(90, 90, 90));
		plot.setAxisLinePaint(new Color(102, 102, 102));

		plot.setWebFilled(true);

		JFreeChart chart = new JFreeChart(plot);
		BufferedImage image = new BufferedImage(1000, 500, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = image.createGraphics();
		chart.draw(g2, new Rectangle2D.Double(0, 0, 1000, 500), null, null);
		g2.dispose();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "PNG", out);
		} catch (IOException e) {
			// Logging the error but not interupting the flow
			logger.error(e);
		}
		byte[] bytes = out.toByteArray();
		return Base64.encodeBase64String(bytes);
	}

	public String individualResultToSpiderChartB64(ECFIndividualResult individualResult) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (ECFIndividualCompetencyResult competencyResult : individualResult.getCompetencyResultList()) {
			int competencyScore = competencyResult.getCompetencyScore();
			int competencyTargetScore = competencyResult.getCompetencyTargetScore();

			dataset.addValue(competencyScore, "Score", competencyResult.getCompetencyName());
			dataset.addValue(competencyTargetScore, "Target score", competencyResult.getCompetencyName());
		}

		SpiderWebPlot plot = new SpiderWebPlot(dataset);
		plot.setSeriesPaint(0, new Color(177, 22, 48));
		plot.setSeriesPaint(1, new Color(62, 116, 170));
		plot.setLabelPaint(new Color(90, 90, 90));
		plot.setAxisLinePaint(new Color(102, 102, 102));

		plot.setWebFilled(true);

		JFreeChart chart = new JFreeChart(plot);
		BufferedImage image = new BufferedImage(1000, 500, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = image.createGraphics();
		chart.draw(g2, new Rectangle2D.Double(0, 0, 1000, 500), null, null);
		g2.dispose();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "PNG", out);
		} catch (IOException e) {
			// Logging the error but not interupting the flow
			logger.error(e);
		}
		byte[] bytes = out.toByteArray();
		return Base64.encodeBase64String(bytes);
	}

	class ParticipantScore {
		private Integer score;
		private String name;
		private String contributionUUID;

		public ParticipantScore() {
			super();
		}

		public ParticipantScore(String name) {
			super();
			this.name = name;
		}

		public ParticipantScore(Integer score, String name) {
			super();
			this.score = score;
			this.name = name;
		}

		public ParticipantScore(Integer score, String name, String contributionUUID) {
			super();
			this.score = score;
			this.name = name;
			this.contributionUUID = contributionUUID;
		}

		public Integer getScore() {
			return score;
		}

		public void setScore(Integer score) {
			this.score = score;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getContributionUUID() {
			return contributionUUID;
		}

		public void setContributionUUID(String contributionUUID) {
			this.contributionUUID = contributionUUID;
		}

	}

}