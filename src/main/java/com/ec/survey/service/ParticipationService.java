package com.ec.survey.service;

import com.ec.survey.model.InvitationTemplate;
import com.ec.survey.model.ParticipationGroup;
import com.ec.survey.model.ParticipationGroupType;
import com.ec.survey.model.ParticipationGroupsForAttendee;
import com.ec.survey.model.SqlPagination;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.attendees.Invitation;
import com.ec.survey.tools.Constants;
import com.ec.survey.tools.ConversionTools;
import com.ec.survey.tools.GuestListCreator;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service("participationService")
@Configurable
public class ParticipationService extends BasicService {
		
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<ParticipationGroup> getAll(String uid, boolean checkRunningMails, int page, int rowsPerPage) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM ParticipationGroup g WHERE g.surveyUid = :uid ORDER BY g.created DESC").setString("uid", uid);
		List<ParticipationGroup> result = query.setFirstResult(page * rowsPerPage).setMaxResults(rowsPerPage).list();
		
		List<Integer> participationGroupsWithRunningMail = null;
		if (checkRunningMails)
		{
			participationGroupsWithRunningMail = mailService.getParticipationGroupsWithRunningMail(uid);
		}
		
		for (ParticipationGroup participationGroup : result) {
			participationGroup.setInvited(getInvitationCount(participationGroup.getId()));
			if (participationGroup.getType() == ParticipationGroupType.Token)
			{
				participationGroup.setAll(getInvitationCountAll(participationGroup.getId()));
			}
			if (checkRunningMails && participationGroupsWithRunningMail.contains(participationGroup.getId()))
			{
				participationGroup.setRunningMails(true);
			}
		}
		
		return result;
	}
	
	@Transactional
	public List<ParticipationGroup> getAll(int id) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM ParticipationGroup g WHERE g.surveyId = :id").setInteger("id", id);
		@SuppressWarnings("unchecked")
		List<ParticipationGroup> result = query.list();
		return result;
	}

	@Transactional(readOnly = true)
	public ParticipationGroup get( Integer id , boolean refreshFirst) {
		Session session = sessionFactory.getCurrentSession();		
		ParticipationGroup participationGroup = (ParticipationGroup) session.get(ParticipationGroup.class, id);		
		
		if (participationGroup != null)
		{
			participationGroup.setInvited(getInvitationCount(participationGroup.getId()));
			
			if (participationGroup.getType() == ParticipationGroupType.Token)
			{
				participationGroup.setAll(getInvitationCountAll(participationGroup.getId()));
			}
		}
		
		return participationGroup;
	}
	
	@Transactional(readOnly = true)
	public ParticipationGroup get( Integer id ) {
		Session session = sessionFactory.getCurrentSession();
		return (ParticipationGroup) session.get(ParticipationGroup.class, id);
	}
	
	@Transactional
	public void update(ParticipationGroup participationGroup) {
		Session session = sessionFactory.getCurrentSession();		
		session.update(participationGroup);
	}
	
	@Transactional
	public void save(ParticipationGroup participationGroup) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(participationGroup);
	}
	
	@Transactional
	public void delete(ParticipationGroup participationGroup) {
		Session session = sessionFactory.getCurrentSession();		
		session.delete(participationGroup);
	}

	@Transactional(readOnly = true)
	public String getGroupIdByInvitationId(String invitationId) {
		try {
			if (invitationId != null && invitationId.length() > 0)
			{
				Session session = sessionFactory.getCurrentSession();
				Query query = session.createQuery("SELECT i.participationGroupId FROM Invitation i WHERE i.uniqueId = :id").setString("id", invitationId);
				@SuppressWarnings("rawtypes")
				List result = query.list();
				if (!result.isEmpty()) return result.get(0).toString();
			}
		} catch (Exception e)
		{
			logger.error(e.getLocalizedMessage(), e);
		}
		return "";
	}

	@Transactional(readOnly = true)
	public List<String> getTokens(int page, int rowsPerPage, int groupId, boolean addActivationStatePrefix) {
		Session session = sessionFactory.getCurrentSession();	
		String sql = "SELECT UNIQUE_ID, INV_DEACTIVATED FROM INVITATIONS WHERE PARTICIPATIONGROUP_ID = :id ORDER BY UNIQUE_ID";
		
		Query query = session.createSQLQuery(sql);
		query.setInteger("id", groupId);
		
		@SuppressWarnings("rawtypes")
		List res = query.setFirstResult((page - 1)*rowsPerPage).setMaxResults(rowsPerPage).list();
		
		List<String> result = new ArrayList<>();
		
		for (Object o: res)
		{
			Object[] a = (Object[]) o;
			String token = (String) a[0];
			
			if (addActivationStatePrefix)
			{
				Boolean deactivated = (Boolean)a[1];
				
				if (deactivated == null || !deactivated)
				{
					result.add("1" + token);
				} else {
					result.add("0" + token);
				}
			} else {
				result.add(token);
			}
		}
		
		return result;
	}

	@Transactional(readOnly = true)
	public int getInvitationCount(int groupId) {
		Session session = sessionFactory.getCurrentSession();	
		String sql = "SELECT COUNT(*) FROM INVITATIONS WHERE PARTICIPATIONGROUP_ID = :id AND (INV_DEACTIVATED IS NULL OR INV_DEACTIVATED = false)";
		Query query = session.createSQLQuery(sql);
		query.setInteger("id", groupId);
		return ConversionTools.getValue(query.uniqueResult());
	}
	
	@Transactional(readOnly = true)
	public int getInvitationCountAll(int groupId) {
		Session session = sessionFactory.getCurrentSession();	
		String sql = "SELECT COUNT(*) FROM INVITATIONS WHERE PARTICIPATIONGROUP_ID = :id";
		Query query = session.createSQLQuery(sql);
		query.setInteger("id", groupId);
		return ConversionTools.getValue(query.uniqueResult());
	}
	
	public void addUsersToGuestListAsync(Integer id, List<Integer> userIDs)
	{
		GuestListCreator c = (GuestListCreator) context.getBean("guestListCreator");
		c.initUsers(id, userIDs);
		getPool().execute(c);
	}
	
	public void addParticipantsToGuestListAsync(Integer id, List<Integer> attendeeIDs) {
		GuestListCreator c = (GuestListCreator) context.getBean("guestListCreator");
		c.initAttendees(id, attendeeIDs);
		getPool().execute(c);
	}
	
	public void addTokensToGuestListAsync(Integer id, List<String> tokens, List<String> deactivatedTokens) {
		GuestListCreator c = (GuestListCreator) context.getBean("guestListCreator");
		c.initTokens(id, tokens, deactivatedTokens);
		getPool().execute(c);
	}	

	@Transactional(readOnly = true)
	public InvitationTemplate getTemplateByName(String name, Integer user) {
		Session session = sessionFactory.getCurrentSession();
		@SuppressWarnings("unchecked")
		List<InvitationTemplate> result = session.createQuery("FROM InvitationTemplate t WHERE t.name like :name AND t.owner.id = :user").setString("name", name).setInteger("user", user).list();
		return !result.isEmpty() ? result.get(0) : null;
	}

	@Transactional
	public void save(InvitationTemplate it) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(it);
	}

	@Transactional(readOnly = true)
	public List<InvitationTemplate> getTemplates(Integer user) {
		Session session = sessionFactory.getCurrentSession();
		@SuppressWarnings("unchecked")
		List<InvitationTemplate> result = session.createQuery("FROM InvitationTemplate t WHERE t.owner.id = :user").setInteger("user", user).list();
		return result;
	}

	@Transactional(readOnly = true)
	public InvitationTemplate getTemplate(int id) {
		Session session = sessionFactory.getCurrentSession();
		return (InvitationTemplate) session.get(InvitationTemplate.class, id);		
	}

	@Transactional(readOnly = false)
	public void delete(InvitationTemplate existing) {
		Session session = sessionFactory.getCurrentSession();
		existing = (InvitationTemplate) session.merge(existing);
		session.delete(existing);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<ParticipationGroup> getGroupsForAttendee(int attendeeId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("SELECT g FROM ParticipationGroup g JOIN g.attendees a WHERE a.id = :attendeeId");
		return (List<ParticipationGroup>) query.setInteger("attendeeId", attendeeId).list();
	}
	
	@Transactional(readOnly = true)
	public List<ParticipationGroupsForAttendee> getGroupsForAttendees(Set<Integer> attendeeIds, int userid) {
		Session session = sessionFactory.getCurrentSession();
		
		Query query = session.createSQLQuery("SELECT a.ATTENDEE_NAME, a.ATTENDEE_EMAIL, p.PARTICIPANTS_NAME, s.SURVEYNAME, p.PARTICIPATION_ID FROM PARTICIPANTS p JOIN PARTICIPANTS_ATTENDEE pa ON pa.PARTICIPANTS_PARTICIPATION_ID = p.PARTICIPATION_ID JOIN ATTENDEE a ON a.ATTENDEE_ID = pa.attendees_ATTENDEE_ID JOIN SURVEYS s ON s.SURVEY_ID = p.PARTICIPATION_SURVEY_ID LEFT JOIN INVITATIONS i ON i.PARTICIPATIONGROUP_ID = p.PARTICIPATION_ID AND i.ATTENDEE_ID = a.ATTENDEE_ID WHERE pa.attendees_ATTENDEE_ID IN (" + StringUtils.collectionToCommaDelimitedString(attendeeIds) + ") AND i.ATTENDEE_INVITED IS NULL AND p.PARTICIPATION_OWNER_ID = :user");
		List<ParticipationGroupsForAttendee> result = new ArrayList<>();
		for (Object o : query.setInteger("user", userid).list()) {
			Object[] e = (Object[])o;
			ParticipationGroupsForAttendee item = new ParticipationGroupsForAttendee();
			item.setAttendeeName(e[0].toString());
			item.setAttendeeEmail(e[1].toString());
			item.setParticipationGroupName(e[2].toString());
			item.setSurveyAlias(e[3].toString());
			item.setParticipationGroupId(ConversionTools.getValue(e[4]));
			result.add(item);
		}
		
		return result;
	}

	@Transactional(readOnly = true)
	public Map<String, Date> getDatesForTokens(Integer participationGroupId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery("SELECT i.UNIQUE_ID, a.ANSWER_SET_DATE FROM INVITATIONS i JOIN ANSWERS_SET a ON a.UNIQUECODE = i.UNIQUE_ID WHERE i.PARTICIPATIONGROUP_ID = :id").setInteger("id", participationGroupId);
		
		Map<String, Date> result = new HashMap<>();
		
		for (Object o : query.list()) {
			Object[] data = (Object[])o;
			result.put((String)data[0], (Date) data[1]);
		}		
		
		return result;
	}

	@Transactional(readOnly = true)
	public int getNumberOfInvitations(String surveyuid) {
		Session session = sessionFactory.getCurrentSession();
		Query hquery = session.createQuery("SELECT count(*) " + 
		"FROM Invitation i, ParticipationGroup pg " + 
		"WHERE pg.surveyUid=:uid " + 
		"AND i.participationGroupId = pg.id ").setString("uid", surveyuid);
		return ConversionTools.getValue(hquery.uniqueResult());
	}

	@Transactional(readOnly = true)
	public int getNumberOfOpenInvitations(String surveyuid) {
		Session session = sessionFactory.getCurrentSession();
		Query hquery = session.createQuery("SELECT count(*) " + 
		"FROM Invitation i, ParticipationGroup pg " + 
		"WHERE pg.surveyUid=:uid " + 
		"AND i.participationGroupId = pg.id " +
		"AND i.answers = 0").setString("uid", surveyuid);
		return ConversionTools.getValue(hquery.uniqueResult());
	}



	public List<Invitation> getInvitations(User user, SqlPagination paging, String survey, String surveystatus, Date expiryStart, Date expiryEnd, Date startInv, Date endInv) {
		Session session = sessionFactory.getCurrentSession();
		
		String where = "";
		if (survey != null)
		{
			where = " s.TITLE like :survey AND ";
		}
		if (surveystatus != null)
		{
			if (surveystatus.equalsIgnoreCase("Published"))
			{
				where += " s.ACTIVE = 1 AND ";
			} else {
				where += " s.ACTIVE = 0 AND ";
			}
		}
		if (expiryStart != null)
		{
			where += " s.SURVEY_START_DATE >= :start AND ";
		}
		if (expiryEnd != null)
		{
			where += " s.SURVEY_END_DATE < :end AND ";
		}
		if (startInv != null)
		{
			where += " i.ATTENDEE_INVITED >= :startinv AND ";
		}
		if (endInv != null)
		{
			where += " i.ATTENDEE_INVITED < :endinv AND ";
		}
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT d.INVITATION_ID FROM ( SELECT i.INVITATION_ID, i.ATTENDEE_INVITED FROM INVITATIONS i JOIN PARTICIPANTS p ON p.PARTICIPATION_ID = i.PARTICIPATIONGROUP_ID JOIN ATTENDEE a ON i.ATTENDEE_ID = a.ATTENDEE_ID JOIN SURVEYS s ON s.SURVEY_ID = p.PARTICIPATION_SURVEY_ID  WHERE ");
		sql.append(where).append("p.PARTICIPATION_TYPE = 1 AND a.ATTENDEE_EMAIL = :email AND i.ATTENDEE_ANSWERS = 0");
		sql.append(" UNION SELECT i.INVITATION_ID, i.ATTENDEE_INVITED FROM INVITATIONS i JOIN PARTICIPANTS p ON p.PARTICIPATION_ID = i.PARTICIPATIONGROUP_ID JOIN ECASUSERS a ON i.ATTENDEE_ID = a.USER_ID JOIN SURVEYS s ON s.SURVEY_ID = p.PARTICIPATION_SURVEY_ID  WHERE ");
		sql.append(where).append("p.PARTICIPATION_TYPE = 2 AND a.USER_EMAIL = :email AND i.ATTENDEE_ANSWERS = 0 ) AS d ORDER BY d.ATTENDEE_INVITED DESC");
		
		SQLQuery query = null;
		
		if (user.getOtherEmail() != null && user.getOtherEmail().length() > 0)
		{
			List<String> allemails = user.getAllEmailAddresses();
			String ssql = sql.toString();
			ssql = ssql.replaceAll("= :email", "IN (:emails)");
			query = session.createSQLQuery(ssql);
			query.setParameterList("emails", allemails);
		} else {
			query = session.createSQLQuery(sql.toString());
			query.setString(Constants.EMAIL, user.getEmail());
		}	
		
		query.setFirstResult(paging.getFirstResult()).setMaxResults(paging.getMaxResult());
		
		if (survey != null)
		{
			query.setString(Constants.SURVEY, "%" + survey + "%");
		}
		if (expiryStart != null)
		{
			query.setDate("start", expiryStart);
		}
		if (expiryEnd != null)
		{
			Calendar cal = Calendar.getInstance();  
			cal.setTime(expiryEnd);  
			cal.add(Calendar.DAY_OF_YEAR, 1); 
			query.setDate("end", cal.getTime());
		}
		
		if (startInv != null)
		{
			query.setDate("startinv", startInv);
		}
		if (endInv != null)
		{
			Calendar cal = Calendar.getInstance();  
			cal.setTime(endInv);  
			cal.add(Calendar.DAY_OF_YEAR, 1); 
			query.setDate("endinv", cal.getTime());
		}
		
		List<Invitation> result = new ArrayList<>();
		for (Object o : query.list()) {
			int id = ConversionTools.getValue(o);
			Invitation item = (Invitation) session.get(Invitation.class, id);		
			result.add(item);
		}
		
		return result;		
	}
	
}
