package com.ec.survey.service;

import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.activity.ActivityRegistry;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ec.survey.exception.MessageException;
import com.ec.survey.exception.TooManyFiltersException;
import com.ec.survey.model.ParticipationGroup;
import com.ec.survey.model.ParticipationGroupType;
import com.ec.survey.model.ResultFilter;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.administration.Voter;
import com.ec.survey.model.evote.DHondtEntry;
import com.ec.survey.model.evote.ElectedCandidate;
import com.ec.survey.model.evote.SeatCounting;
import com.ec.survey.model.evote.SeatDistribution;
import com.ec.survey.model.evote.eVoteListResult;
import com.ec.survey.model.evote.eVoteResults;
import com.ec.survey.model.survey.MultipleChoiceQuestion;
import com.ec.survey.model.survey.PossibleAnswer;
import com.ec.survey.model.survey.Question;
import com.ec.survey.model.survey.SingleChoiceQuestion;
import com.ec.survey.model.survey.Survey;
import com.mysql.cj.util.StringUtils;

import edu.emory.mathcs.backport.java.util.Collections;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service("eVoteService")
public class EVoteService extends BasicService {
		
	public ArrayList<Voter> importVoterFile(String surveyUid, InputStream file) throws IOException {
		 XSSFWorkbook workbook = new XSSFWorkbook(file);
		 XSSFSheet sheet = workbook.getSheetAt(0);
		 
		 ArrayList<Voter> voters = new ArrayList<>();
		 
		 boolean usesHeader = true;
		 
		 //iterate rows
		 Iterator<Row> rowIterator = sheet.iterator();
         while (rowIterator.hasNext()) 
         {
             Row row = rowIterator.next();
             
             String login = row.getCell(0).getStringCellValue();
             String firstName = row.getCell(1).getStringCellValue();
             String surname = row.getCell(2).getStringCellValue();
             
             if (usesHeader) {
            	 usesHeader = false;
             } else {
            	 Voter voter = new Voter();
            	 voter.setEcMoniker(login);
            	 voter.setGivenName(firstName);
            	 voter.setSurname(surname);
            	 voter.setSurveyUid(surveyUid);
            	 voter.setCreated(new Date());
            	 voters.add(voter);
             }
         }
         workbook.close();
         
         return voters;
	}
	
	public eVoteResults importSeatTestSheet(Survey survey, InputStream file) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		XSSFSheet sheet = workbook.getSheetAt(0);
		 
		eVoteResults results = getEmptyListResult(survey);
		 
		//iterate rows
		Iterator<Row> rowIterator = sheet.iterator();
		int rowCounter = 0;
		int colCounter = 1;
		int candidateCounter = 0;
		
		// this map is used to quickly find a candidate by his position in the list
		Map<String, ArrayList<String>> candidatesByList = new HashMap<>();
		for (eVoteListResult listResult : results.getLists().values()) {
			candidatesByList.put(listResult.getUid(), new ArrayList<>(listResult.getCandidateVotes().keySet()));
		}		
		
        while (rowIterator.hasNext()) 
        {
            Row row = rowIterator.next();
            
            if (rowCounter == 0) {
            	results.setBlankVotes((int) row.getCell(1).getNumericCellValue());
            } else if (rowCounter == 1) {
            	results.setSpoiltVotes((int) row.getCell(1).getNumericCellValue());
            } else if (rowCounter == 2) {
            	results.setPreferentialVotes((int) row.getCell(1).getNumericCellValue());
            } else if (rowCounter == 3) {
            	// skip as it contains the names of the lists
            } else if (rowCounter == 4 && !survey.geteVoteTemplate().equals("l")) {
            	// list votes
            	colCounter = 1;
            	for (eVoteListResult listResult : results.getLists().values()) {
           			listResult.setListVotes((int) row.getCell(colCounter++).getNumericCellValue());
            	}
            } else {
            	// candidate votes
            	colCounter = 1;
            	for (eVoteListResult listResult : results.getLists().values()) {
            		int votes = (int) row.getCell(colCounter++).getNumericCellValue();
            		if (votes > 0) {
	            		ArrayList<String> candidates = candidatesByList.get(listResult.getUid());
	            		if (candidates.size() > candidateCounter) {
	            			listResult.getCandidateVotes().put(candidates.get(candidateCounter), votes);
	            		}
            		}
            	}
            	
            	candidateCounter++;
            }      
         
            rowCounter++;
        }
        workbook.close();
        
        return results;
	}
	

	@Transactional
	public void addVoters(List<Voter> voters, User user) {
		Session session = sessionFactory.getCurrentSession();
		for (Voter voter: voters) {
			session.save(voter);
		}
		ParticipationGroup group = new ParticipationGroup(voters.get(0).getSurveyUid());
		group.setActive(true);
		group.setName("Voter File");
		group.setType(ParticipationGroupType.VoterFile);
		group.setOwnerId(user.getId());
		participationService.save(group);
		
		activityService.log(ActivityRegistry.ID_GUEST_LIST_CREATED, null, group.getId().toString(), user.getId(), voters.get(0).getSurveyUid(),
				group.getNiceType());
	}

	@Transactional
	public void addMoreVoters(List<Voter> voters, User user) {
		Session session = sessionFactory.getCurrentSession();
		for (Voter voter: voters) {
			session.save(voter);
		}
		String logValues = voters.stream().map(v -> v.getId() + "").collect(Collectors.joining(","));
		activityService.log(ActivityRegistry.ID_GUEST_LIST_VOTER_ADDED, null, logValues, user.getId(), voters.get(0).getSurveyUid(), "VoterFile");
	}

	@Transactional
	public List<Voter> getVoters(String surveyUid, int page, int rowsPerPage, String user, String first, String last, Boolean voted) {
		Session session = sessionFactory.getCurrentSession();
		
		if (page == -1) {
			// this means last page
			long count = getVoterCount(surveyUid, user, first, last, voted);
			page = (int) Math.floorDiv(count, 20l);
		}
		
		String where = getWhere(user, first, last, voted);
		
		Query<Voter> query = session.createQuery("FROM Voter WHERE " + where + " ORDER BY id ASC", Voter.class);
		query.setParameter("surveyUid", surveyUid).setFirstResult((page-1) * rowsPerPage).setMaxResults(rowsPerPage);
		
		addQueryParameters(query, surveyUid, user, first, last, voted);
		
		List<Voter> result = query.list();
		return result;
	}

	@Transactional
	public long getVoterCount(String surveyUid, Boolean voted) {
		return getVoterCount(surveyUid, null, null, null, voted);
	}
	
	@Transactional
	public long getVoterCount(String surveyUid, String user, String first, String last, Boolean voted) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "SELECT COUNT(voter) FROM Voter voter WHERE " + getWhere(user, first, last, voted);
			
		Query<Long> query = session.createQuery(hql, Long.class);
		query.setParameter("surveyUid", surveyUid);
		
		addQueryParameters(query, surveyUid, user, first, last, voted);
		
		return query.uniqueResult();
	}
	
	private String getWhere(String user, String first, String last, Boolean voted) {
		String where = "surveyUid = :surveyUid";
		if (!StringUtils.isNullOrEmpty(user)) {
			where += " AND ecMoniker like :user";
		}
		if (!StringUtils.isNullOrEmpty(first)) {
			where += " AND givenName like :first";
		}
		if (!StringUtils.isNullOrEmpty(last)) {
			where += " AND surname like :last";
		}
		if (voted != null) {
			where += " AND voted = :voted";
		}
		return where;
	}
	
	private void addQueryParameters(@SuppressWarnings("rawtypes") Query query, String surveyUid, String user, String first, String last, Boolean voted) {
		query.setParameter("surveyUid", surveyUid);
		if (!StringUtils.isNullOrEmpty(user)) {
			query.setParameter("user", '%' + user + '%');
		}
		if (!StringUtils.isNullOrEmpty(first)) {
			query.setParameter("first", '%' + first + '%');
		}
		if (!StringUtils.isNullOrEmpty(last)) {
			query.setParameter("last", '%' + last + '%');
		}
		if (voted != null) {
			query.setParameter("voted", voted.booleanValue());
		}
	}

	@Transactional
	public void deleteAllVoters(String surveyUid) {
		Session session = sessionFactory.getCurrentSession();
		@SuppressWarnings("rawtypes")
		Query query = session.createQuery("DELETE FROM Voter WHERE surveyUid = :surveyUid");
		query.setParameter("surveyUid", surveyUid);
		query.executeUpdate();
	}

	@Transactional
	public boolean deleteVoter(int voterId, String surveyUid, User user) {
		Session session = sessionFactory.getCurrentSession();
		@SuppressWarnings("rawtypes")
		Query query = session.createQuery("DELETE FROM Voter WHERE surveyUid = :surveyUid AND id = :id");
		query.setParameter("surveyUid", surveyUid).setParameter("id", voterId);

		activityService.log(ActivityRegistry.ID_GUEST_LIST_VOTER_REMOVED, voterId + "", null, user.getId(), surveyUid, "VoterFile");

		return query.executeUpdate() > 0;
	}

	@Transactional
	public boolean checkVoter(String surveyUid, String ecMoniker) {
		Session session = sessionFactory.getCurrentSession();
		
		Query<Voter> queryVoter = session.createQuery("FROM Voter WHERE surveyUid = :surveyUid AND ecMoniker = :ecMoniker", Voter.class);
		queryVoter.setParameter("surveyUid", surveyUid).setParameter("ecMoniker", ecMoniker);
		
		List<Voter> result = queryVoter.list();
		
		if (result.size() == 1 && !result.get(0).getVoted()) {
			// make sure that voter file is not deactivated			
			Query<ParticipationGroup> queryGroup = session.createQuery("FROM ParticipationGroup WHERE surveyUid = :surveyUid", ParticipationGroup.class);
			queryGroup.setParameter("surveyUid", surveyUid);
			List<ParticipationGroup> result2 = queryGroup.list();
			
			return result2.size() == 1 && result2.get(0).getActive();
		}
		
		return false;
	}

	@Transactional
	public void setVoted(String surveyUid, String ecMoniker) {
		Session session = sessionFactory.getCurrentSession();
		@SuppressWarnings("rawtypes")
		Query query = session.createQuery("UPDATE Voter SET voted = true WHERE surveyUid = :surveyUid AND ecMoniker = :ecMoniker");
		query.setParameter("surveyUid", surveyUid).setParameter("ecMoniker", ecMoniker);
		query.executeUpdate();
	}

	public byte[] exportVoterFile(String surveyUid, String user, String first, String last, Boolean voted) throws IOException {
		List<Voter> voters = getVoters(surveyUid, 1, 100000, user, first, last, voted);

		return exportVoterFile(voters);
	}

	public byte[] exportVoterFile(Collection<Voter> voters) throws IOException {
		
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Voters");
		Row header = sheet.createRow(0);
		
		Cell headerCell = header.createCell(0);
		headerCell.setCellValue("User name");
		headerCell = header.createCell(1);
		headerCell.setCellValue("First name");
		headerCell = header.createCell(2);
		headerCell.setCellValue("Surname");
		if (voters.size() > 0) {
			headerCell = header.createCell(3);
			headerCell.setCellValue("Has voted?");
		}
		
		Row row;
		int rowCounter = 1;
		for (Voter voter: voters) {
			row = sheet.createRow(rowCounter);
			
			row.createCell(0).setCellValue(voter.getEcMoniker());
			row.createCell(1).setCellValue(voter.getGivenName());
			row.createCell(2).setCellValue(voter.getSurname());
			row.createCell(3).setCellValue(voter.getVoted() ? "Yes" : "No");
			
			rowCounter++;
		}
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
		    workbook.write(bos);
		} finally {
		    bos.close();
		}
		byte[] bytes = bos.toByteArray();
		
		workbook.close();
		
		return bytes;
	}
	
	private void parseEVoteSurvey(EVoteConfiguration config, SeatCounting result) throws MessageException {
		for (Question question : config.survey.getQuestions()) {
			if (config.firstSingle == null && question instanceof SingleChoiceQuestion) {
				config.firstSingle = (SingleChoiceQuestion) question;
				
				for (PossibleAnswer pa : config.firstSingle.getPossibleAnswers())
				{
					if (pa.getShortname().equalsIgnoreCase("vote")) {
						config.vote = pa;
					} else if (pa.getShortname().equalsIgnoreCase("blank")) {
						config.blank = pa;
					} else if (pa.getShortname().equalsIgnoreCase("spoilt")) {
						config.spoilt = pa;
					} else {
						if (config.vote == null) {
							config.vote = pa;
						} else if (config.blank == null) {
							config.blank = pa;
						} else if (config.spoilt == null) {
							config.spoilt = pa;
						}
					}
				}
			}
			
			if (question instanceof MultipleChoiceQuestion) {
				SeatDistribution listSeats = new SeatDistribution();
				listSeats.setName(question.getStrippedTitle());
				result.getListSeatDistribution().add(listSeats);
				
				MultipleChoiceQuestion mc = (MultipleChoiceQuestion)question;
				config.listSeatDistributions.put(mc, listSeats);

				for (PossibleAnswer pa : mc.getPossibleAnswers()) {
					ElectedCandidate ec = new ElectedCandidate();
					ec.setList(question.getStrippedTitle());
					ec.setName(pa.getStrippedTitle());
					config.candidateVotes.put(pa, ec);
				}
				
				if (mc.getPossibleAnswers().size() > result.getMaxCandidatesInLists()) {
					result.setMaxCandidatesInLists(mc.getPossibleAnswers().size());
				}
			}			
		}
		if (config.firstSingle == null || config.vote == null || config.blank == null || config.spoilt == null) {
			throw new MessageException("invalid survey");
		}		
	}
	
	private class EVoteConfiguration {
		public Survey survey;
		public SingleChoiceQuestion firstSingle;
		public PossibleAnswer vote;
		public PossibleAnswer blank;
		public PossibleAnswer spoilt;
		public Map<MultipleChoiceQuestion, SeatDistribution> listSeatDistributions = new LinkedHashMap<>();
		public Map<PossibleAnswer, ElectedCandidate> candidateVotes = new LinkedHashMap<>();
		public boolean useLuxembourgProcedure;
	}
	
	@Transactional
	private eVoteResults loadEVoteAnswers(EVoteConfiguration config, SeatCounting result) throws TooManyFiltersException, MessageException {
		eVoteResults evoteResults = getEmptyListResult(config.survey);
		
		Session session = sessionFactory.getCurrentSession();
		HashMap<String, Object> values = new HashMap<>();
		ResultFilter filter = new ResultFilter();
		filter.setSurveyUid(config.survey.getUniqueId());
		String sql = "select a.QUESTION_UID, a.PA_UID, a.VALUE, ans.ANSWER_SET_ID FROM ANSWERS a RIGHT JOIN ANSWERS_SET ans ON a.AS_ID = ans.ANSWER_SET_ID where ans.ANSWER_SET_ID IN ("
				+ answerService.getSql(null, config.survey.getId(), filter, values, true)
				+ ") ORDER BY ans.ANSWER_SET_ID";

		@SuppressWarnings("rawtypes")
		NativeQuery query = session.createSQLQuery(sql);

		query.setReadOnly(true);

		for (String attrib : values.keySet()) {
			Object value = values.get(attrib);
			if (value instanceof String) {
				query.setParameter(attrib, (String) values.get(attrib));
			} else if (value instanceof Integer) {
				query.setParameter(attrib, (Integer) values.get(attrib));
			} else if (value instanceof Date) {
				query.setParameter(attrib, (Date) values.get(attrib));
			}
		}
		
		query.setFetchSize(Integer.MIN_VALUE);
		ScrollableResults results = query.setReadOnly(true).scroll(ScrollMode.FORWARD_ONLY);
		
		int counter = 0;
		Set<Integer> preferentialVoteAnswerIds = new HashSet<>();
		
		// we use his hashset to check whether each vote has at least one candidate or list vote
		// if not, we treat it as "blank" vote
		Set<Integer> voteAnswerIds = new HashSet<>();
		
		try {
			session.flush();
			
			while (results.next()) {
				if (counter > 1000) {
					session.clear();
					counter = 0;
				}
				counter++;

				Object[] a = results.get();
				String quid = (String) a[0];
				String pauid = (String) a[1];
				String value = (String) a[2];
				int answerSetId = ConversionTools.getValue(a[3]);
				
				if (config.blank.getUniqueId().equals(pauid)) {
					evoteResults.setBlankVotes(evoteResults.getBlankVotes() + 1);
				} else if (config.spoilt.getUniqueId().equals(pauid)) {
					evoteResults.setSpoiltVotes(evoteResults.getSpoiltVotes() + 1);
				} else if (config.vote.getUniqueId().equals(pauid)) {
					voteAnswerIds.add(answerSetId);
				} else if (evoteResults.getLists().containsKey(quid)) {
					eVoteListResult listResult = evoteResults.getLists().get(quid);
					
					if (voteAnswerIds.contains(answerSetId)) {
						voteAnswerIds.remove(answerSetId);
					}
					
					if (!config.useLuxembourgProcedure && value != null && value.equalsIgnoreCase("EVOTE-ALL")) {
						listResult.setListVotes(listResult.getListVotes() + 1);
					} else if (listResult.getCandidateVotes().containsKey(pauid)) {
						listResult.getCandidateVotes().put(pauid, listResult.getCandidateVotes().get(pauid) + 1);
						
						if (!preferentialVoteAnswerIds.contains(answerSetId)) {
							preferentialVoteAnswerIds.add(answerSetId);
						}
						
						if (config.useLuxembourgProcedure) {
							listResult.setLuxListVotes(listResult.getLuxListVotes() + 1);
						}
					}
				}				
			}
		} finally {
			results.close();
		}
				
		evoteResults.setPreferentialVotes(preferentialVoteAnswerIds.size());
		if (voteAnswerIds.size() > 0) {
			evoteResults.setBlankVotes(evoteResults.getBlankVotes() + voteAnswerIds.size());
		}
		
		return evoteResults;
	}
	
	@Transactional
	private void parseEVoteAnswers(EVoteConfiguration config, SeatCounting result, eVoteResults evoteResults) {
		
		result.setPreferentialVotes(evoteResults.getPreferentialVotes());
		
		for (eVoteListResult listResult : evoteResults.getLists().values()) {
			result.setListVotes(result.getListVotes() + listResult.getListVotes());
			if (config.useLuxembourgProcedure) {
				result.setLuxListVotes(result.getLuxListVotes() + listResult.getLuxListVotes());
			}
			
			Optional<MultipleChoiceQuestion> mc = config.listSeatDistributions.keySet().stream().filter(q -> q.getUniqueId().equals(listResult.getUid())).findFirst();
			
			if (mc.isPresent()) {
				SeatDistribution listSeatDistribution = config.listSeatDistributions.get(mc.get());
				listSeatDistribution.setLuxListVotes(listResult.getLuxListVotes());
				listSeatDistribution.setListVotes(listResult.getListVotes());
				
				int totalPreferentialVotes = 0;
				
				for (PossibleAnswer pa : mc.get().getPossibleAnswers()) {
					int paVotes = listResult.getCandidateVotes().get(pa.getUniqueId());					
					ElectedCandidate ec = config.candidateVotes.get(pa);
					ec.setVotes(paVotes);
					totalPreferentialVotes += paVotes;
				}
				
				listSeatDistribution.setPreferentialVotes(totalPreferentialVotes);
			}			
		}
		
		result.setBlankVotes(evoteResults.getBlankVotes());
		result.setSpoiltVotes(evoteResults.getSpoiltVotes());
		
		int votes = result.getListVotes() + result.getPreferentialVotes();
		int total = votes + result.getBlankVotes() + result.getSpoiltVotes();
		
		result.setVotes(votes);			
		result.setTotal(total);
	}
	
	@Transactional
	public SeatCounting getCounting(String surveyUid, eVoteResults evoteResults, MessageSource resources, Locale locale, boolean full) throws Exception {
		SeatCounting result = new SeatCounting();
		EVoteConfiguration config = new EVoteConfiguration();
		
		Survey survey = surveyService.getSurvey(surveyUid, false, false, false, false, null, true, false);
		
		if (survey == null) {
			// test page use
			survey = surveyService.getSurvey(surveyUid, true, false, false, false, null, true, false);
		}
		
		long voterCount = getVoterCount(survey.getUniqueId(), null);
		config.survey = survey;
		config.useLuxembourgProcedure = survey.geteVoteTemplate().equals("l");
		result.setQuorum(survey.getQuorum());
		result.setTemplate(survey.geteVoteTemplate());
		result.setMaxSeats(survey.getSeatsToAllocate());
		result.setMinListPercent(survey.getMinListPercent());
		result.setVoterCount((int)voterCount);
		
		parseEVoteSurvey(config, result);
		if (evoteResults == null) {
			evoteResults = loadEVoteAnswers(config, result);
		}
		parseEVoteAnswers(config, result, evoteResults);
		
		if (full) {		
			// compute distribution of seats (list / preferential)
			Integer[] votes;
			int[] seatsArray;			
			
			if (config.useLuxembourgProcedure) {
				result.setPreferentialVotesFinal(result.getLuxListVotes());
				result.setTotalPreferentialVotes(result.getLuxListVotes());
				
				votes = new Integer[] {result.getListVotes(), result.getPreferentialVotes()};
				seatsArray = computeSeats(survey, votes, result.getListVotes() + result.getPreferentialVotes(), result.getMaxSeats(), null, null);
			} else {
				int sumAllPreferentialVotes = config.listSeatDistributions.values().stream().collect(Collectors.summingInt(SeatDistribution::getPreferentialVotes));
				result.setTotalPreferentialVotes(sumAllPreferentialVotes);
				result.setPreferentialVotesFinal(sumAllPreferentialVotes);
				
				votes = new Integer[] {result.getListVotesWeighted(), result.getTotalPreferentialVotes()};
				seatsArray = computeSeats(survey, votes, result.getListVotesWeighted() + result.getTotalPreferentialVotes(), result.getMaxSeats(), null, null);
			}
			
			result.setListVotesSeats(seatsArray[0]);
			result.setPreferentialVotesSeats(seatsArray[1]);
			result.setListVotesFinal(result.getListVotes());
			
			for (SeatDistribution listSeatDistribution : config.listSeatDistributions.values()) {
				if (config.useLuxembourgProcedure) {
					listSeatDistribution.setListPercent(Math.round((float)listSeatDistribution.getLuxListVotes() / (float)result.getLuxListVotes() * 10000) / 100.0);
				} else {
					listSeatDistribution.setListPercent(Math.round((float)listSeatDistribution.getListVotes() / (float)result.getListVotes() * 10000) / 100.0);
				}
				listSeatDistribution.setListVotesWeighted(listSeatDistribution.getListVotes() * result.getMaxSeats());
				listSeatDistribution.setPreferentialPercent(Math.round((float)listSeatDistribution.getPreferentialVotes() / (float)result.getPreferentialVotes() * 10000) / 100.0);
				listSeatDistribution.setListPercentWeighted(Math.round((double)listSeatDistribution.getTotalWeighted() / (double)result.getTotalVotesWeighted() * 10000) / 100.0);
				if (listSeatDistribution.getListPercentWeighted() < result.getMinListPercent()) {
					result.setListVotesFinal(result.getListVotesFinal() - listSeatDistribution.getListVotes());
					if (config.useLuxembourgProcedure) {
						result.setPreferentialVotesFinal(result.getPreferentialVotesFinal() - listSeatDistribution.getLuxListVotes());
					} else {
						result.setPreferentialVotesFinal(result.getPreferentialVotesFinal() - listSeatDistribution.getPreferentialVotes());
					}				
				}
			}
					
			// compute distribution of list seats		
			LinkedHashMap<MultipleChoiceQuestion, SeatDistribution> listSeatDistributionsFiltered = config.listSeatDistributions
					.entrySet().stream().filter(x -> x.getValue().getListPercentWeighted() >= result.getMinListPercent())
					.collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue(),  (u, v) -> {
		                throw new IllegalStateException(String.format("Duplicate key %s", u));
		            },
		            LinkedHashMap::new));
			
		
			// compute distribution of preferential seats
			Integer[] preferentialVotes = listSeatDistributionsFiltered.values().stream().map(SeatDistribution::getPreferentialVotes).toArray(Integer[]::new);
			DHondtEntry[][] numbers = new DHondtEntry[result.getMaxSeats()][preferentialVotes.length];
			
			int[] candidatesPerList = new int[listSeatDistributionsFiltered.size()];
			int counter = 0;
			for (MultipleChoiceQuestion mc : listSeatDistributionsFiltered.keySet()) {
				candidatesPerList[counter++] = mc.getPossibleAnswers().size();
			}
			
			int[] preferentialSeatsArray = computeSeats(survey, preferentialVotes, result.getPreferentialVotesFinal(), result.getPreferentialVotesSeats(), numbers, candidatesPerList);
			counter = 0;
			for (SeatDistribution listSeatDistribution : listSeatDistributionsFiltered.values()) {
				listSeatDistribution.setPreferentialSeats(preferentialSeatsArray[counter++]);
				if (!config.useLuxembourgProcedure) {
					listSeatDistribution.setPreferentialPercentFinal(Math.round((double)listSeatDistribution.getPreferentialVotes() / (double)result.getPreferentialVotesFinal() * 10000) / 100.0);
				} else {
					listSeatDistribution.setPreferentialPercentFinal(Math.round((double)listSeatDistribution.getLuxListVotes() / (double)result.getPreferentialVotesFinal() * 10000) / 100.0);
				}
			}
	
			Integer[] listVotes = listSeatDistributionsFiltered.values().stream().map(SeatDistribution::getListVotes).toArray(Integer[]::new);
			int[] listSeatsArrayLimited = null;
			if (config.useLuxembourgProcedure) {
				result.setDHondtEntries(numbers);
				
				// there are no list votes
				// the lists get the votes according to the preferential votes			
				for (MultipleChoiceQuestion mc : listSeatDistributionsFiltered.keySet()) {
					List<ElectedCandidate> candidatesOrdered = getCandidates(mc, config.candidateVotes, true);
					
					// compute elected candidates from preferential votes
					SeatDistribution listSeatDistribution = listSeatDistributionsFiltered.get(mc);
					for (int i = 0; i < listSeatDistribution.getPreferentialSeats(); i++) {
						if (i >= candidatesOrdered.size()) {
							break;
						}
						
						ElectedCandidate ec = candidatesOrdered.get(i);
						ec.setSeats(1);
						ec.setPreferentialSeat(true);
						result.getCandidatesFromPreferentialVotes().add(ec);
					}
				}
				
				// if the lists were not big enough to use all seats
				if (result.getCandidatesFromPreferentialVotes().size() < result.getMaxSeats()) {
					List<ElectedCandidate> allCandidatesOrdered = getCandidates(listSeatDistributionsFiltered.keySet(), config.candidateVotes, true);
					for (ElectedCandidate ec : allCandidatesOrdered) {
						if (ec.getSeats() == 0) { // only consider candidates that do not already have a seat
							ec.setSeats(1);
							ec.setPreferentialSeat(true);
							result.getCandidatesFromPreferentialVotes().add(ec);
							
							if (result.getCandidatesFromPreferentialVotes().size() == result.getMaxSeats()) {
								// we reached the limit for seats
								break;
							}
						}
					}
				}	
				
			} else {
				listSeatsArrayLimited = computeSeats(survey, listVotes, result.getListVotesFinal(), result.getListVotesSeats(), null, candidatesPerList);
				
				counter = 0;
				for (SeatDistribution listSeatDistribution : listSeatDistributionsFiltered.values()) {
					listSeatDistribution.setListSeats(listSeatsArrayLimited[counter++]);
					listSeatDistribution.setListPercentFinal(Math.round((double)listSeatDistribution.getListVotes() / (double)result.getListVotesFinal() * 10000) / 100.0);
				}				
				
				// compute elected candidates from list votes
				for (MultipleChoiceQuestion mc : listSeatDistributionsFiltered.keySet()) {
					List<ElectedCandidate> candidatesOrdered = getCandidates(mc, config.candidateVotes, false);
					List<ElectedCandidate> candidatesOrderedByVotes = getCandidates(mc, config.candidateVotes, true);
					
					// compute elected candidates from list votes
					SeatDistribution listSeatDistribution = listSeatDistributionsFiltered.get(mc);
					for (int i = 0; i < listSeatDistribution.getListSeats(); i++) {
						if (i >= candidatesOrdered.size()) {
							break;
						}
						ElectedCandidate ec = candidatesOrdered.get(i);
						ec.setSeats(1);
						result.getCandidatesFromListVotes().add(ec);					
					}
					
					counter = 0;
					if (listSeatDistribution.getPreferentialSeats() > 0) {
						for (int i = 0; i < candidatesOrderedByVotes.size(); i++) {
							ElectedCandidate ec = candidatesOrderedByVotes.get(i);
							if (ec.getSeats() == 0) { // only consider candidates that do not already have a seat (list vote)
								ec.setSeats(1);
								ec.setPreferentialSeat(true);
								result.getCandidatesFromPreferentialVotes().add(ec);
								counter++;
								
								if (counter == listSeatDistribution.getPreferentialSeats()) {
									// we reached the limit for preferential vote seats
									break;
								}
							}
						}
					}
				}
				
				// in case one or more lists did not have enough candidates for list and preferential seats: fill with candidates that have the most votes
				if (result.getMaxSeats() > (result.getCandidatesFromListVotes().size() + result.getCandidatesFromPreferentialVotes().size())) {
					List<ElectedCandidate> allCandidatesOrdered = getCandidates(listSeatDistributionsFiltered.keySet(), config.candidateVotes, true);
					for (ElectedCandidate ec : allCandidatesOrdered) {
						if (ec.getSeats() == 0) { // only consider candidates that do not already have a seat (list vote)
							ec.setSeats(1);
							ec.setPreferentialSeat(true);
							result.getCandidatesFromPreferentialVotes().add(ec);
							
							if (result.getMaxSeats() == (result.getCandidatesFromListVotes().size() + result.getCandidatesFromPreferentialVotes().size())) {
								// all seats allocated
								break;
							}
						}
					}
				}			
			}
			
			MultipleChoiceQuestion[] mcs = listSeatDistributionsFiltered.keySet().toArray(new MultipleChoiceQuestion[listSeatDistributionsFiltered.size()]);
			int[] finalPreferentialSeats = null;
			int[] reallocatedListSeats = new int[listSeatDistributionsFiltered.size()];
			if (!config.useLuxembourgProcedure) {
				// find reallocated list seats
				int[] listSeatsArray = computeSeats(survey, listVotes, result.getListVotesFinal(), result.getListVotesSeats(), null, null);
				
				for (int i = 0; i < listSeatDistributionsFiltered.size(); i++) {
					int reallocatedSeats = listSeatsArray[i] - listSeatsArrayLimited[i];
					if (reallocatedSeats > 0) {
						String list = mcs[i].getStrippedTitle();
						String reallocationMessage = resources.getMessage("message.reallocationMessage", new String[] { list, Integer.toString(reallocatedSeats) }, "There are not enough candidates for", locale);
						
						result.getReallocationMessagesForLists().add(reallocationMessage);
						reallocatedListSeats[i] += reallocatedSeats;
					}
				}
				
				// compute final preferential seats
				finalPreferentialSeats = new int[listSeatDistributionsFiltered.size()];
				for (int i = 0; i < result.getMaxCandidatesInLists(); i++) {			
					for (int q = 0; q < mcs.length; q++) {
						MultipleChoiceQuestion mc = mcs[q];
						
						if (mc.getPossibleAnswers().size() > i) {
							PossibleAnswer pa = mc.getPossibleAnswers().get(i);					
							ElectedCandidate candidateVote = config.candidateVotes.get(pa);
							if (candidateVote.getSeats() > 0 && candidateVote.isPreferentialSeat()) {
								finalPreferentialSeats[q]++;
							}
						}
					}			
				}	
			} else {
				finalPreferentialSeats = preferentialSeatsArray;
			}
				
			// find reallocated preferential seats
			int[] preferentialSeatsArrayNoLimits = computeSeats(survey, preferentialVotes, result.getPreferentialVotesFinal(), result.getPreferentialVotesSeats(), numbers, null);
			int[] reallocatedSeats = new int[listSeatDistributionsFiltered.size()];
			for (int i = 0; i < listSeatDistributionsFiltered.size(); i++) {
				reallocatedSeats[i] = preferentialSeatsArrayNoLimits[i] - finalPreferentialSeats[i];
				if (reallocatedSeats[i] > 0) {
					String list = mcs[i].getStrippedTitle();
					String reallocationMessage = resources.getMessage("message.reallocationMessage", new String[] { list, Integer.toString(reallocatedSeats[i]) }, "There are not enough candidates for", locale);
					
					result.getReallocationMessages().add(reallocationMessage);
				}
			}
		
			// combine vote counts for all candidates in all lists		
			for (int i = 0; i < result.getMaxCandidatesInLists(); i++) {
				List<ElectedCandidate> candidateList = new ArrayList<>();
					
				for (int q = 0; q < mcs.length; q++) {
					MultipleChoiceQuestion mc = mcs[q];
				
					if (mc.getPossibleAnswers().size() > i) {
						PossibleAnswer pa = mc.getPossibleAnswers().get(i);					
						ElectedCandidate candidateVote = config.candidateVotes.get(pa);
						candidateList.add(candidateVote);
					} else {
						ElectedCandidate ec = new ElectedCandidate();
						
						if (reallocatedListSeats[q] > 0) {
							ec.setReallocatedSeat(true);
							reallocatedListSeats[q]--;
						} else if (reallocatedSeats[q] > 0) {
							ec.setReallocatedSeat(true);
							reallocatedSeats[q]--;
						}
						candidateList.add(ec); // the list does not have enough candidates -> add empty one
					}
				}					
				result.getCandidateVotes().add(candidateList);
			}			
		
			if (!config.useLuxembourgProcedure) {
				counter = 0;
				for (MultipleChoiceQuestion mc : listSeatDistributionsFiltered.keySet()) {
					SeatDistribution listSeatDistribution = listSeatDistributionsFiltered.get(mc);
					listSeatDistribution.setPreferentialSeats(finalPreferentialSeats[counter++]);
				}
			}
		}
			
		return result;
	}
	
	private List<ElectedCandidate> getCandidates(Set<MultipleChoiceQuestion> mcs, Map<PossibleAnswer, ElectedCandidate> candidateVotes, boolean orderByVotes) {
		List<ElectedCandidate> result = new ArrayList<>();
	
		for (MultipleChoiceQuestion mc : mcs) {
			for (int i = 0; i < mc.getPossibleAnswers().size(); i++) {
				PossibleAnswer pa = mc.getPossibleAnswers().get(i);
				ElectedCandidate candidateVote = candidateVotes.get(pa);
				candidateVote.setPosition(i);
				result.add(candidateVote);
			}
		}
		
		if (orderByVotes) {		
			Collections.sort(result, Comparator.comparing(ElectedCandidate::getVotes).reversed()
	            .thenComparing(ElectedCandidate::getPosition));
		}
		
		return result;
	}
	
	private List<ElectedCandidate> getCandidates(MultipleChoiceQuestion mc, Map<PossibleAnswer, ElectedCandidate> candidateVotes, boolean orderByVotes) {
		Set<MultipleChoiceQuestion> mcs = new HashSet<>();
		mcs.add(mc);
		return getCandidates(mcs, candidateVotes, orderByVotes);
	}
	
	private static class RemainderValue {
		private int index;
		private int seats;
		private double fraction;
		
		public double getFraction() {
			return fraction;
		}
		public void setFraction(double fraction) {
			this.fraction = fraction;
		}
		
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		
		public int getSeats() {
			return seats;
		}
		public void setSeats(int seats) {
			this.seats = seats;
		}
	}
	
	public static int[] computeSeats(Survey survey, Integer[] input, int total, int maxSeats, DHondtEntry[][] numbers, int[] candidatesPerList) {
		switch (survey.geteVoteTemplate()){
			case "l":
				return computeSeatsUsingDHondtMethod(input, total, maxSeats, numbers, candidatesPerList);
			default:
				return computeSeatsUsingLargestRemainderMethod(input, total, maxSeats, candidatesPerList);
		}
	}
		
	private static DHondtEntry getLargestEntry(DHondtEntry[][] numbers) {
		DHondtEntry result = null;
		
		for (int r = 0; r < numbers.length; r++) {
			for (int i = 0; i < numbers[0].length; i++) {
				if (numbers[r][i].getSeat() == 0 && !numbers[r][i].isInvalid() && (result == null || numbers[r][i].getValue() > result.getValue())) {
					result = numbers[r][i];
				}
			}
		}
		
		return result;
	}
	
	public static int[] computeSeatsUsingDHondtMethod(Integer[] input, int total, int maxSeats, DHondtEntry[][] numbers, int[] candidatesPerList) {
		// the result array
		int[] seats = new int[input.length];
		
		if (numbers == null) {
			numbers = new DHondtEntry[maxSeats][input.length];
		}
		// the array for the numbers
		for (int r = 1; r <= maxSeats; r++) {  // r = round = divisor
			for (int i = 0; i < input.length; i++) {
				DHondtEntry entry = new DHondtEntry();
				entry.setRound(r);
				entry.setListIndex(i);
				entry.setValue(input[i] / (double)r);
				numbers[r-1][i] = entry;
			}
		}		
		
		// counter for allocated seats
		int totalSeats = 0;
	
		while (totalSeats < maxSeats) {			
			DHondtEntry largest = getLargestEntry(numbers);
			if (largest == null) {
				break;
			}
			
			//check if the corresponding list has enough candidates
			if (candidatesPerList != null) {
				int listSize = candidatesPerList[largest.getListIndex()];
				if (seats[largest.getListIndex()] == listSize) {
					// the list does not have enough candidates for another seat
					largest.setInvalid(true);
					continue;
				}
			}
			
			largest.setSeat(++totalSeats);
			seats[largest.getListIndex()]++;
		}		
		
		return seats;
	}
	
	public static int[] computeSeatsUsingLargestRemainderMethod(Integer[] input, int total, int maxSeats, int[] candidatesPerList) {
		int[] result = new int[input.length];
		
		List<RemainderValue> rvs = new ArrayList<>();
		
		int seats = 0;
		
		for (int i = 0; i < input.length; i++) {
			float value = input[i] / (float)total * (float)maxSeats;
			
			RemainderValue rv = new RemainderValue();
			rv.setIndex(i);
			
			int initialSeats = (int)Math.floor(value);
			
			if (candidatesPerList != null && initialSeats > candidatesPerList[i]) {
				initialSeats = candidatesPerList[i];
				// todo: mark as invalid
			}
			
			rv.setSeats(initialSeats);
			rv.setFraction(value - rv.seats);
			rvs.add(rv);
			
			seats += rv.seats;
		}
		
		if (seats < maxSeats) {
			Collections.sort(rvs, Comparator.comparingDouble(RemainderValue::getFraction).reversed());
			
			for (int i = 0; i < rvs.size(); i++) {
				if (candidatesPerList != null && rvs.get(i).getSeats() >= candidatesPerList[rvs.get(i).getIndex()]) {
					// todo: mark as invalid
					continue;
				}
				
				rvs.get(i).setSeats(rvs.get(i).getSeats()+1);
				seats++;
				if (seats >= maxSeats) {
					break;
				}
			}
		}
		
		for (RemainderValue rv : rvs) {
			result[rv.getIndex()] = rv.getSeats();
		}
		
		return result;
	}

	public eVoteResults getEmptyListResult(Survey survey) {
		eVoteResults result = new eVoteResults();
		
		for (Question q : survey.getQuestions()) {
			if (q instanceof MultipleChoiceQuestion) {
				eVoteListResult listResult = new eVoteListResult();
				listResult.setUid(q.getUniqueId());
				for (PossibleAnswer pa : ((MultipleChoiceQuestion)q).getPossibleAnswers()) {
					listResult.getCandidateVotes().put(pa.getUniqueId(), 0);
				}
				
				result.getLists().put(q.getUniqueId(), listResult);
			}
		}
		
		return result;
	}
}
