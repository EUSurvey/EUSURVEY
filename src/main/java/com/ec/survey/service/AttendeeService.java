package com.ec.survey.service;

import com.ec.survey.model.ParticipationGroup;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.attendees.*;
import com.ec.survey.tools.ConversionTools;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

@Service("attendeeService")
public class AttendeeService extends BasicService {
	
	@Resource(name = "administrationService")
	private AdministrationService administrationService;
	
	@Autowired
	private SqlQueryService sqlQueryService;	
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<String> getAttendeeEmailsAddresses(int ownerId) {
		Session session = sessionFactory.getCurrentSession();
				
		HashMap<String, Object> parameters = new HashMap<>();
		String sql = "SELECT a.ATTENDEE_EMAIL " + getSql(session, ownerId, new HashMap<>(), parameters, false);
		
		SQLQuery query = session.createSQLQuery(sql);
		sqlQueryService.setParameters(query, parameters);
		
		@SuppressWarnings("rawtypes")
		List res = query.list();
		
		return res;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Attendee> getAttendees(List<Integer> ids, boolean eagerload)
	{
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM Attendee a WHERE a.id in (" + StringUtils.collectionToCommaDelimitedString(ids) + ")");
		List<Attendee> result = query.list();
		
		if (eagerload)
		for (Attendee attendee : result)
		{
			Hibernate.initialize(attendee.getAttributes());
		}
		
		return result;
	}
	
	@Transactional(readOnly = true)
	public List<Attendee> getAttendees(int ownerId, Map<String, String> filterValues, int i, int maxValue) {
		return getAttendees(ownerId, filterValues, i, maxValue, sessionFactory, false);
	}
	
	@Transactional(readOnly = true)
	public List<Attendee> getAttendeesEagerLoad(int ownerId, Map<String, String> filterValues, int i, int maxValue) {
		return getAttendees(ownerId, filterValues, i, maxValue, sessionFactory, true);
	}
	
	@Transactional(readOnly = true)
	public List<Attendee> getAttendees(Integer ownerId, Map<String, String> attributeFilter, int page, int rowsPerPage, SessionFactory sessionFactory, boolean eagerLoad) {
		Session session = sessionFactory.getCurrentSession();
		
		HashMap<String, Object> parameters = new HashMap<>();
		String sql = "SELECT a.ATTENDEE_ID " + getSql(session, ownerId, attributeFilter, parameters, false);
		
		SQLQuery query = session.createSQLQuery(sql);
		sqlQueryService.setParameters(query, parameters);
		
		@SuppressWarnings("rawtypes")
		List res = query.setFirstResult((page - 1)*rowsPerPage).setMaxResults(rowsPerPage).list();
		
		List<Attendee> attendees = new ArrayList<>();
		
		for (Object o: res)
		{
			Attendee attendee = get(ConversionTools.getValue(o)); 
			attendees.add(attendee);
		}
		
		List<Share> shares = getPassiveShares(ownerId);
		
		for (Attendee attendee: attendees)
		{
			if (attendee.getOwnerId() > 0)
			{
				User owner = administrationService.getUser(attendee.getOwnerId());
				if (owner != null) attendee.setOwner(owner.getName());
			}
			
			attendee.setReadonly(true);
			if (ownerId < 0 || attendee.getOwnerId().equals(ownerId))
			{
				attendee.setReadonly(false);
			} else {
				for (Share share: shares)
				{
					if (share.containsAttendee(attendee.getId()) && !share.getReadonly())
					{
						attendee.setReadonly(false);
						break;
					}
				}
			}
			
			if (eagerLoad)
			{
				Hibernate.initialize(attendee.getAttributes());
			}
		}		
		
		return attendees;
	}
	
	@Transactional
	public List<Attendee> getAttendeesToWrite(int ownerId, Map<String, String> filterValues)
	{
		Session session = sessionFactory.getCurrentSession();
		
		HashMap<String, Object> parameters = new HashMap<>();
		String sql = "SELECT a.ATTENDEE_ID " + getSql(session, ownerId, filterValues, parameters, false);
		
		SQLQuery query = session.createSQLQuery(sql);
		sqlQueryService.setParameters(query, parameters);
		
		@SuppressWarnings("rawtypes")
		List res = query.list();
		
		List<Attendee> attendees = new ArrayList<>();
		
		for (Object o: res)
		{
			Attendee attendee = get(ConversionTools.getValue(o)); 
			Hibernate.initialize(attendee.getAttributes());
			attendees.add(attendee);
		}
		
		return attendees;
	}
	
	@Transactional
	public int markAttendeesAsDeleted(int ownerId, Map<String, String> filterValues)
	{
		Session session = sessionFactory.getCurrentSession();
		
		HashMap<String, Object> parameters = new HashMap<>();
		String sql = "UPDATE ATTENDEE SET ATTENDEE_HIDDEN = 1, ATT_UPDATED = NOW(), ATTENDEE_ORIGID = ATTENDEE_ID WHERE ATTENDEE_ID IN (SELECT c.ATTENDEE_ID FROM (SELECT a.ATTENDEE_ID " + getSql(session, ownerId, filterValues, parameters, true) + ") as c)";
		SQLQuery query = session.createSQLQuery(sql);
		sqlQueryService.setParameters(query, parameters);
		
		return query.executeUpdate();
	}
		
	@Transactional(readOnly = true)
	public int getNumberOfAttendees(Integer ownerId, HashMap<String, String> attributeFilter) {
		Session session = sessionFactory.getCurrentSession();
		
		HashMap<String, Object> parameters = new HashMap<>();
		String sql = getSql(session, ownerId, attributeFilter, parameters, false);
		
		SQLQuery query = session.createSQLQuery("SELECT count(*) " + sql);
		sqlQueryService.setParameters(query, parameters);
		
		return ConversionTools.getValue(query.uniqueResult());		
	}
	
	private String getSql(Session session, Integer ownerId, Map<String, String> hashMap, HashMap<String, Object> oQueryParameters, boolean onlywritableshares) {
		
		StringBuilder sql = new StringBuilder("FROM ATTENDEE a");
		
		if (hashMap != null && hashMap.size() > 0)
		{
			for (String key : hashMap.keySet())
			{
				if (!key.equalsIgnoreCase("name") && !key.equalsIgnoreCase("email") && !key.equalsIgnoreCase("owner") && !key.equalsIgnoreCase("_csrf") && !key.startsWith("visibleAttendee") && hashMap.get(key) != null && hashMap.get(key).trim().length() > 0)
				{
					sql.append(" LEFT OUTER JOIN ATTRIBUTE at ON at.ATTE_ID = a.ATTENDEE_ID ");
					break;
				}					
			}
			if (hashMap.containsKey("owner") && hashMap.get("owner") != null && hashMap.get("owner").length() > 0)
			{
				sql.append(" JOIN USERS u ON u.USER_ID = a.OWNER_ID ");
			}
		}
		
		
		if (ownerId > 0)
		{
			if (onlywritableshares)
			{
				sql.append(" WHERE (a.OWNER_ID = :ownerId OR a.ATTENDEE_ID IN (select sa.attendees_ATTENDEE_ID from SHARES_ATTENDEE sa where sa.SHARES_SHARE_ID in (select s.SHARE_ID from SHARES s where s.RECIPIENT = :ownerId AND s.READONLY = 0)))");
			} else {
				sql.append(" WHERE (a.OWNER_ID = :ownerId OR a.ATTENDEE_ID IN (select sa.attendees_ATTENDEE_ID from SHARES_ATTENDEE sa where sa.SHARES_SHARE_ID in (select s.SHARE_ID from SHARES s where s.RECIPIENT = :ownerId)))");
			}
			oQueryParameters.put("ownerId", ownerId);
		} else {
			sql.append(" WHERE a.ATTENDEE_ID > 0");
		}
		
		sql.append(" AND a.ATTENDEE_HIDDEN IS NULL");
		
		if (hashMap != null && hashMap.size() > 0)
		{
			int counter = 0;
			for (String key : hashMap.keySet())
			{
				if (!key.equalsIgnoreCase("name") && !key.equalsIgnoreCase("email") && !key.equalsIgnoreCase("owner"))
				try {
					int intKey = Integer.parseInt(key);
					String value = hashMap.get(key).trim();
					
					if (value.length() > 0)
					{
						sql.append(" AND at.attributeName_AN_ID IN (SELECT AN_ID FROM ATTRIBUTENAME WHERE AN_NAME IN (SELECT AN_NAME FROM ATTRIBUTENAME WHERE AN_ID = :aid").append(counter).append(")) AND at.ATTRIBUTE_VALUE like :av").append(counter);
						oQueryParameters.put("aid" + counter, intKey);
						oQueryParameters.put("av" + counter++, "%" + value + "%");
					}
				} catch (NumberFormatException e)
				{
					//ignore
				}			
			}
		
			if (hashMap.containsKey("name") && hashMap.get("name") != null && hashMap.get("name").length() > 0)
			{
				sql.append(" AND a.ATTENDEE_NAME like :name");
				oQueryParameters.put("name", "%" + hashMap.get("name") + "%");
			}
			
			if (hashMap.containsKey("email") && hashMap.get("email") != null && hashMap.get("email").length() > 0)
			{
				sql.append(" AND a.ATTENDEE_EMAIL like :email");
				oQueryParameters.put("email", "%" + hashMap.get("email") + "%");
			}
			
			if (hashMap.containsKey("owner") && hashMap.get("owner") != null && hashMap.get("owner").length() > 0)
			{
				sql.append(" AND (u.USER_DISPLAYNAME like :owner OR u.USER_LOGIN like :owner)");
				oQueryParameters.put("owner", "%" + hashMap.get("owner") + "%");
			}
		}
		
		sql.append(" ORDER BY a.ATT_CREATED DESC");

		return sql.toString();
	}
	
	@SuppressWarnings({ "unchecked" })
	@Transactional(readOnly = true)
	public List<AttributeName> getAllAttributes(int ownerId)
	{
		Session session = sessionFactory.getCurrentSession();
		
		Query query = session.createQuery("SELECT a FROM AttributeName a ORDER BY a.name ASC");
		
		if (ownerId > 0)
		{
			String shared = " OR a.ownerId = -1 OR a.id IN (SELECT attributeName.id FROM Share share JOIN share.attendees as attendee JOIN attendee.attributes as attribute JOIN attribute.attributeName as attributeName WHERE share.recipient.id = :ownerId)";
			
			query = session.createQuery("SELECT a FROM AttributeName a WHERE a.ownerId = :ownerId" + shared + " ORDER BY a.name ASC");
			query.setInteger("ownerId", ownerId);		
		}
		
		List<AttributeName> attributeNames = query.list();
		
		Map<String, AttributeName> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		for (AttributeName attributeName : attributeNames) {
			if (!attributeName.getName().equals("Owner"))
			{
				if (!map.containsKey(attributeName.getName()))
				{
					map.put(attributeName.getName(), attributeName);
				} else {
					//own attributes have higher priority than shared ones
					if (attributeName.getOwnerId().equals(ownerId))
					{
						map.remove(attributeName.getName());
						map.put(attributeName.getName(), attributeName);
					}
				}
			}
		}
		
		return new ArrayList<>(map.values());
	}
	
	@Transactional(readOnly = true)
	public Attendee get( Integer id ) {
		Session session = sessionFactory.getCurrentSession();
		return (Attendee) session.get(Attendee.class, id);
	}
	
	@Transactional(readOnly = false)
	public void add(Attendee attendee) {
		Session session = sessionFactory.getCurrentSession();	
		for (Attribute attribute : attendee.getAttributes()) {
			if (attribute.getAttributeName().getId() == null || attribute.getAttributeName().getId() == 0)
			{
				session.save(attribute.getAttributeName());
			}
		}
		attendee.setCreated(new Date());
		attendee.setUpdated(attendee.getCreated());
		session.save(attendee);
		for (Attribute attribute : attendee.getAttributes()) {
			attribute.setAttendeeId(attendee.getId());
			session.update(attribute);
		}
	}
	
	@Transactional(readOnly = false)
	public void add(List<Attendee> attendees) {
		Session session = sessionFactory.getCurrentSession();	
		for (Attendee attendee: attendees)
		{
			for (Attribute attribute : attendee.getAttributes()) {
				attribute.setAttendeeId(attendee.getId());
				if (attribute.getAttributeName().getId() == null || attribute.getAttributeName().getId() == 0)
				{
					session.save(attribute.getAttributeName());
				}
			}
			attendee.setCreated(new Date());
			attendee.setUpdated(attendee.getCreated());
			session.save(attendee);
			for (Attribute attribute : attendee.getAttributes()) {
				attribute.setAttendeeId(attendee.getId());
				session.update(attribute);
			}
		}	
	}
	
	@Transactional(readOnly = false, propagation=Propagation.REQUIRES_NEW)
	public void update(Attendee attendee, boolean ismarkdeleted) {
		Session session = sessionFactory.getCurrentSession();
		attendee = (Attendee) session.merge(attendee);
		if (!ismarkdeleted)
		for (Attribute attribute : attendee.getAttributes()) {
			if (attribute.getAttributeName().getId() == null || attribute.getAttributeName().getId() == 0)
			{
				session.saveOrUpdate(attribute.getAttributeName());
			}
		}
		attendee.setUpdated(new Date());
		session.saveOrUpdate(attendee);
		
		//session.flush();
		
		if (!ismarkdeleted)
		for (Attribute attribute : attendee.getAttributes()) {
			attribute.setAttendeeId(attendee.getId());
			//session.saveOrUpdate(attribute);
		}
	}
	
	@Transactional(readOnly = false, propagation=Propagation.REQUIRES_NEW)
	public void update(List<Attendee> attendees, boolean isDelete) {
		Session session = sessionFactory.getCurrentSession();	
		
		for (Attendee attendee: attendees)
		{
			if (!isDelete)
			{
				for (Attribute attribute : attendee.getAttributes()) {
					if (attribute.getAttributeName().getId() == null || attribute.getAttributeName().getId() == 0)
					{
						session.saveOrUpdate(attribute.getAttributeName());
					}
				}
			}
			attendee.setUpdated(new Date());
			session.saveOrUpdate(attendee);
			
			//session.flush();
			
			if (!isDelete)
			{
				for (Attribute attribute : attendee.getAttributes()) {
					attribute.setAttendeeId(attendee.getId());
					//session.saveOrUpdate(attribute);
				}
			}
		}
	}

	@Transactional(readOnly = false)
	public void delete(int id) {
		Session session = sessionFactory.getCurrentSession();	
		
		Attendee attendee = (Attendee) session.get(Attendee.class, id);
		
		Criteria crit = session.createCriteria(ParticipationGroup.class);
		crit.createAlias("attendees", "attendeesAlias");
		crit.add(Restrictions.eq("attendeesAlias.id", id));
		
		@SuppressWarnings("unchecked")
		List<ParticipationGroup> groups = crit.list();
		
		for (ParticipationGroup participationGroup : groups) {
			participationGroup.getAttendees().remove(attendee);
			session.update(participationGroup);
		}
		
		crit = session.createCriteria(Share.class);
		crit.createAlias("attendees", "attendeesAlias");
		crit.add(Restrictions.eq("attendeesAlias.id", id));
		
		@SuppressWarnings("unchecked")
		List<Share> shares = crit.list();
		
		for (Share share : shares) {
			share.getAttendees().remove(attendee);
			session.update(share);
		}
		
		session.flush();
		session.delete(attendee);
	}
	
	@Transactional(readOnly = false)
	public void delete(List<Attendee> attendees) {
		Session session = sessionFactory.getCurrentSession();
		
		List<Integer> ids = new ArrayList<>();
		
		for (Attendee attendee : attendees) {
			int id = attendee.getId();
			ids.add(id);
		}
		
		Query query = session.createSQLQuery("DELETE FROM PARTICIPANTS_ATTENDEE WHERE attendees_ATTENDEE_ID IN (:ids)").setParameterList("ids", ids);
		query.executeUpdate();
		
		query = session.createSQLQuery("DELETE FROM SHARES_ATTENDEE WHERE attendees_ATTENDEE_ID IN (:ids)").setParameterList("ids", ids);
		query.executeUpdate();
		
		query = session.createSQLQuery("DELETE FROM attendee_attributes WHERE ATTENDEE_ID IN (:ids)").setParameterList("ids", ids);
		query.executeUpdate();
		
		query = session.createQuery("DELETE FROM Attribute a WHERE a.attendeeId IN (:ids)").setParameterList("ids", ids);
		query.executeUpdate();
		
		query = session.createQuery("DELETE FROM Attendee a WHERE a.id IN (:ids)").setParameterList("ids", ids);
		query.executeUpdate();
	}

	@Transactional(readOnly = true)
	public Attribute getAttribute(int id) {
		Session session = sessionFactory.getCurrentSession();
		return (Attribute) session.get(Attribute.class, id);
	}
	
	@Transactional(readOnly = false)
	public void add(AttributeName attributeName) {
		Session session = sessionFactory.getCurrentSession();	
		session.save(attributeName);
	}

	@Transactional(readOnly = true)
	public AttributeName getAttributeName(int id) {
		Session session = sessionFactory.getCurrentSession();
		return (AttributeName) session.get(AttributeName.class, id);
	}
	
	@Transactional(readOnly = true)
	public AttributeName getAttributeName(String name, int ownerId) {
		Session session = sessionFactory.getCurrentSession();	
		Query query;
		if (ownerId == -1)
		{
			query = session.createQuery("SELECT a FROM AttributeName a WHERE a.name = :name").setString("name", name);
		} else {
			query = session.createQuery("SELECT a FROM AttributeName a WHERE a.name = :name AND a.ownerId = :owner").setString("name", name).setInteger("owner", ownerId);
		}		
		
		@SuppressWarnings("unchecked")
		List<AttributeName> attributeNames = query.list();	
		
		if (attributeNames.size() > 0) return attributeNames.get(0);
		
		return null;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void add(Invitation invitation) throws Exception {
		internalAddInvitation(invitation);
	}
	
	public void internalAddInvitation(Invitation invitation) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		boolean saved = false;
		
		int counter = 1;
		
		while(!saved)
		{
			try {
				session.save(invitation);
				saved = true;
			} catch (org.hibernate.exception.LockAcquisitionException ex)
			{
				logger.info("lock on invitation table catched; retry counter: " + counter);
				counter++;
								
				if (counter > 60)
				{
					logger.error(ex.getLocalizedMessage(), ex);
					throw ex;
				}
				
				Thread.sleep(1000);
			}
		}		
	}

	@Transactional(readOnly = true)
	public Map<Integer, Invitation> getInvitationsByAttendeeForParticipationGroup(Integer participationGroupId) {
		Session session = sessionFactory.getCurrentSession();	
		Query query = session.createQuery("SELECT i FROM Invitation i WHERE i.participationGroupId = :participationGroupId");
		@SuppressWarnings("unchecked")
		List<Invitation> invitations = query.setInteger("participationGroupId", participationGroupId).list();	
		
		HashMap<Integer, Invitation> result = new HashMap<>();
		
		for (Invitation invitation : invitations) {
			if (result.containsKey(invitation.getAttendeeId()))
			{
				//TODO: error log
			}else {
				result.put(invitation.getAttendeeId(), invitation);
			}
		}
		
		return result;
	}
	
	@Transactional(readOnly = true)
	public Invitation getInvitationForParticipationGroupAndAttendee(Integer participationGroupId, Integer attendeeId) {
		Session session = sessionFactory.getCurrentSession();	
		Query query = session.createQuery("SELECT i FROM Invitation i WHERE i.participationGroupId = :participationGroupId and i.attendeeId = :attendeeId");
		@SuppressWarnings("unchecked")
		List<Invitation> invitations = query.setInteger("participationGroupId", participationGroupId).setInteger("attendeeId", attendeeId).list();	
		
		if (invitations.size() > 0) return invitations.get(0);
		
		return null;
	}
	
	@Transactional(readOnly = true)
	public Invitation getInvitationByUniqueId(String uniqueId) throws Exception {
		return internalGetInvitationByUniqueId(uniqueId);
	}

	@Transactional(readOnly = true)
	public List<Invitation> getInvitationsByUniqueId(String uniqueId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("SELECT i FROM Invitation i WHERE i.uniqueId LIKE :uniqueId").setString("uniqueId", "%" + uniqueId + "%");
		
		@SuppressWarnings("unchecked")
		List<Invitation> invitations = query.list();
		
		return invitations;
	}
	
	private Invitation internalGetInvitationByUniqueId(String uniqueId) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("SELECT i FROM Invitation i WHERE i.uniqueId = :uniqueId").setString("uniqueId", uniqueId).setCacheable(true);
		
		@SuppressWarnings("unchecked")
		List<Invitation> invitations = query.list();
		
		if (invitations.size() > 1)
		{
			throw new Exception("there are more than one invitation with uniqueId = " + uniqueId);
		}
		
		if (invitations.size() > 0) return invitations.get(0);
		
		return null;
	}

	@Transactional(readOnly = true)
	public Invitation getInvitation(int id) {
		Session session = sessionFactory.getCurrentSession();
		return (Invitation) session.get(Invitation.class, id);
	}

	@Transactional(readOnly = false, propagation=Propagation.REQUIRES_NEW)
	public void update(Invitation invitation) {
		Session session = sessionFactory.getCurrentSession();		
		session.update(invitation);
	}

	@Transactional(readOnly = false, propagation=Propagation.REQUIRES_NEW)
	public void save(Share share) {
		Session session = sessionFactory.getCurrentSession();	
		session.saveOrUpdate(share);		
	}

	@Transactional(readOnly = true)
	public List<Share> getShares(int userId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = null; 
		if (userId > 0)
		{
			query = session.createQuery("FROM Share s WHERE s.owner.id = :userId").setInteger("userId", userId);
		} else {
			query = session.createQuery("FROM Share s");
		}
		
		@SuppressWarnings("unchecked")
		List<Share> result = query.list();
		return result;
	}
	
	@Transactional(readOnly = true)
	public List<Share> getPassiveShares(int recipientId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM Share s WHERE s.recipient.id = :recipientId").setInteger("recipientId", recipientId);
		
		@SuppressWarnings("unchecked")
		List<Share> result = query.list();
		return result;
	}

	@Transactional(readOnly = true)
	public Share getShare(int id) {
		Session session = sessionFactory.getCurrentSession();
		return (Share) session.get(Share.class, id);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteShare(int id) {
		Session session = sessionFactory.getCurrentSession();	
		Share share = (Share) session.get(Share.class, id);
		session.delete(share);
	}

	@Transactional(readOnly = true)
	public Map<Integer, List<String>> getAllAttributeValues(User user) {
		
		int ownerId;
		if (user.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) == 2)
    	{
			ownerId = -1;
    	} else {
    		ownerId = user.getId();    		
    	}
		
		List<AttributeName> attributeNames = getAllAttributes(ownerId);
		
		Map<Integer, List<String>> attributeValues = new HashMap<>();
		
		Session session = sessionFactory.getCurrentSession();	
		
		for (AttributeName attributeName: attributeNames)
		{
			Query query = session.createQuery("SELECT a.value FROM Attribute a WHERE a.attributeName.id = :id ORDER BY a.value ASC").setInteger("id", attributeName.getId());
			@SuppressWarnings("unchecked")
			List<String> attributes = query.list();
			
			List<String> currentList = new ArrayList<>();
			for (String attribute: attributes)
			{
				if (!currentList.contains(attribute)) currentList.add(attribute);
			}
			attributeValues.put(attributeName.getId(), currentList);
		}
		
		return attributeValues;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void delete(Invitation invitation) {
		Session session = sessionFactory.getCurrentSession();	
		session.delete(invitation);		
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void addTokens(List<String> tokens, Integer groupId) throws Exception {
		for (String token : tokens) {
			Invitation invitation = new Invitation(groupId, token);
						
			internalAddInvitation(invitation);
		}
	}
	
	@Transactional(readOnly = false)
	public void decreaseInvitationAnswer(String token) throws Exception
	{
		Session session = sessionFactory.getCurrentSession();	
		Invitation invitation = internalGetInvitationByUniqueId(token);
		if (invitation != null)
		{
			if (invitation.getAnswers() > 0)
			{
				invitation.setAnswers(invitation.getAnswers() - 1);
			}
			session.saveOrUpdate(invitation);
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void executeOperations(Map<String, String> operations, int groupId) throws Exception {
		
		Session session = sessionFactory.getCurrentSession();	
					
		for (String token : operations.keySet()) {
			String value = operations.get(token);
			
			Invitation invitation = internalGetInvitationByUniqueId(token);
			
			if (invitation != null && invitation.getParticipationGroupId().equals(groupId))
			{
				switch(value)
				{
					case "delete":
					{
						session.delete(invitation);	
						break;
					}
					case "deactivate":
					{
						invitation.setDeactivated(true);
						session.update(invitation);
						break;
					}
					case "activate":
					{
						invitation.setDeactivated(false);
						session.update(invitation);	
						break;
					}
				}	
			} else {
				throw new Exception("The group id is not correct!");
			}
		}
	}

	@Transactional(readOnly = true)
	public Map<Integer, String> getNamesForAttributenameIDs(Set<Integer> ids) {
		Session session = sessionFactory.getCurrentSession();
		Map<Integer, String> result = new HashMap<>();
		for (int id : ids)
		{
			AttributeName name = (AttributeName) session.get(AttributeName.class, id);
			result.put(id, name.getName());
		}
		return result;
	}

	@Transactional(readOnly = true)
	public boolean attendeeExists(String email, Integer id) {
		Map<String, String> filterValues = new HashMap<>();
		filterValues.put("email", email);
				
		List<Attendee> attendees = getAttendees(id, filterValues, 1, 2);
		return attendees.size() > 0;
	}

	@Transactional
	public void createDummyAttendees(int contacts, int userid) {
		List<Attendee> attendees = new ArrayList<Attendee>();
		
		AttributeName attributeName = getAttributeName("code", userid);
		
		if (attributeName == null)
		{
			attributeName = new AttributeName();
			attributeName.setName("code");
			attributeName.setOwnerId(userid);			
			add(attributeName);
		}	
		
		for (int i = 0; i < contacts; i++)
		{
			Attendee attendee = new Attendee();
			attendee.setOwnerId(userid);
			attendee.setName("Dummy " + i);
			attendee.setEmail("dummy@doesnotexist.xy");
			
			Attribute a = new Attribute();
			a.setAttributeName(attributeName);
			a.setValue("code" + i);
			attendee.getAttributes().add(a);
			
			attendees.add(attendee);
		}
		
		add(attendees);
	}
}
