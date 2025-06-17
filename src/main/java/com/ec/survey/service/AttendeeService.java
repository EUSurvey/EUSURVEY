package com.ec.survey.service;

import com.ec.survey.exception.MessageException;
import com.ec.survey.model.ParticipationGroup;
import com.ec.survey.model.administration.GlobalPrivilege;
import com.ec.survey.model.administration.User;
import com.ec.survey.model.attendees.*;
import com.ec.survey.tools.Constants;
import com.ec.survey.tools.ConversionTools;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.Map.Entry;

@Service("attendeeService")
public class AttendeeService extends BasicService {

	@Autowired
	private SqlQueryService sqlQueryService;

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<String> getAttendeeEmailsAddresses(int ownerId) throws Exception {
		Session session = sessionFactory.getCurrentSession();

		HashMap<String, Object> parameters = new HashMap<>();
		String sql = "SELECT a.ATTENDEE_EMAIL " + getSql(ownerId, new HashMap<>(), parameters, false);

		NativeQuery query = session.createSQLQuery(sql);
		sqlQueryService.setParameters(query, parameters);

		@SuppressWarnings("rawtypes")
		List res = query.list();

		return res;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Attendee> getAttendees(List<Integer> ids, boolean eagerload) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(
				"FROM Attendee a WHERE a.id in (" + StringUtils.collectionToCommaDelimitedString(ids) + ")");
		List<Attendee> result = query.list();

		if (eagerload)
		{
			for (Attendee attendee : result) {
				Hibernate.initialize(attendee.getAttributes());
			}
		}

		return result;
	}

	@Transactional(readOnly = true)
	public List<Attendee> getAttendees(int ownerId, Map<String, String> filterValues, int i, int maxValue)
			throws Exception {
		return getAttendees(ownerId, filterValues, i, maxValue, sessionFactory, false);
	}

	@Transactional(readOnly = true)
	public List<Attendee> getAttendeesEagerLoad(int ownerId, Map<String, String> filterValues, int i, int maxValue)
			throws Exception {
		return getAttendees(ownerId, filterValues, i, maxValue, sessionFactory, true);
	}

	@Transactional(readOnly = true)
	public List<Attendee> getAttendees(Integer ownerId, Map<String, String> attributeFilter, int page, int rowsPerPage,
			SessionFactory sessionFactory, boolean eagerLoad) throws Exception {
		Session session = sessionFactory.getCurrentSession();

		HashMap<String, Object> parameters = new HashMap<>();
		String sql = "SELECT a.ATTENDEE_ID " + getSql(ownerId, attributeFilter, parameters, false);

		NativeQuery query = session.createSQLQuery(sql);
		sqlQueryService.setParameters(query, parameters);

		@SuppressWarnings("rawtypes")
		List res = query.setFirstResult((page > 1 ? page - 1 : 0) * rowsPerPage).setMaxResults(rowsPerPage).list();

		List<Attendee> attendees = new ArrayList<>();

		for (Object o : res) {
			Attendee attendee = get(ConversionTools.getValue(o));
			attendees.add(attendee);
		}

		List<Share> shares = getPassiveShares(ownerId);

		for (Attendee attendee : attendees) {
			if (attendee.getOwnerId() > 0) {
				User owner = administrationService.getUser(attendee.getOwnerId());
				if (owner != null)
					attendee.setOwner(owner.getName());
			}

			attendee.setReadonly(true);
			if (ownerId < 0 || attendee.getOwnerId().equals(ownerId)) {
				attendee.setReadonly(false);
			} else {
				for (Share share : shares) {
					if (share.containsAttendee(attendee.getId()) && !share.getReadonly()) {
						attendee.setReadonly(false);
						break;
					}
				}
			}

			if (eagerLoad) {
				Hibernate.initialize(attendee.getAttributes());
			}
		}

		return attendees;
	}

	@Transactional
	public List<Attendee> getAttendeesToWrite(int ownerId, Map<String, String> filterValues) throws Exception {
		Session session = sessionFactory.getCurrentSession();

		HashMap<String, Object> parameters = new HashMap<>();
		String sql = "SELECT a.ATTENDEE_ID " + getSql(ownerId, filterValues, parameters, false);

		NativeQuery query = session.createSQLQuery(sql);
		sqlQueryService.setParameters(query, parameters);

		@SuppressWarnings("rawtypes")
		List res = query.list();

		List<Attendee> attendees = new ArrayList<>();

		for (Object o : res) {
			Attendee attendee = get(ConversionTools.getValue(o));
			Hibernate.initialize(attendee.getAttributes());
			attendees.add(attendee);
		}

		return attendees;
	}

	@Transactional
	public int markAttendeesAsDeleted(int ownerId, Map<String, String> filterValues) throws Exception {
		Session session = sessionFactory.getCurrentSession();

		HashMap<String, Object> parameters = new HashMap<>();
		String sql = "UPDATE ATTENDEE SET ATTENDEE_HIDDEN = 1, ATT_UPDATED = NOW(), ATTENDEE_ORIGID = ATTENDEE_ID WHERE ATTENDEE_ID IN (SELECT c.ATTENDEE_ID FROM (SELECT a.ATTENDEE_ID "
				+ getSql(ownerId, filterValues, parameters, true) + ") as c)";
		NativeQuery query = session.createSQLQuery(sql);
		sqlQueryService.setParameters(query, parameters);

		return query.executeUpdate();
	}

	@Transactional(readOnly = true)
	public int getNumberOfAttendees(Integer ownerId, Map<String, String> attributeFilter) throws Exception {
		Session session = sessionFactory.getCurrentSession();

		HashMap<String, Object> parameters = new HashMap<>();
		String sql = getSql(ownerId, attributeFilter, parameters, false);

		NativeQuery query = session.createSQLQuery("SELECT count(*) " + sql);
		sqlQueryService.setParameters(query, parameters);

		return ConversionTools.getValue(query.uniqueResult());
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Integer> getAccessibleAttendees(Integer ownerId, Map<String, String> attributeFilter) throws Exception {
		Session session = sessionFactory.getCurrentSession();

		HashMap<String, Object> parameters = new HashMap<>();
		String sql = getSql(ownerId, attributeFilter, parameters, false);

		NativeQuery query = session.createSQLQuery("SELECT a.ATTENDEE_ID " + sql);
		sqlQueryService.setParameters(query, parameters);

		@SuppressWarnings("rawtypes")
		List res = query.list();
		return res;
	}

	private String getSql(Integer ownerId, Map<String, String> attributeFilter,
			HashMap<String, Object> oQueryParameters, boolean onlywritableshares) {

		StringBuilder sql = new StringBuilder("FROM ATTENDEE a");

		if (attributeFilter != null && attributeFilter.size() > 0) {
			for (Entry<String, String> entry : attributeFilter.entrySet()) {
				if (!entry.getKey().equalsIgnoreCase("name") && !entry.getKey().equalsIgnoreCase(Constants.EMAIL)
						&& !entry.getKey().equalsIgnoreCase("owner") && !entry.getKey().equalsIgnoreCase("_csrf")
						&& !entry.getKey().startsWith("visibleAttendee") && entry.getValue() != null
						&& entry.getValue().trim().length() > 0) {
					
					if (org.apache.commons.lang3.StringUtils.isNumeric(entry.getKey())) {
						//LEFT OUTER JOIN ATTRIBUTE at<n> ON at<n>.ATTE_ID = a.ATTENDEE_ID
						sql.append(" LEFT OUTER JOIN ATTRIBUTE at")
								.append(entry.getKey())
								.append(" ON at")
								.append(entry.getKey())
								.append(".ATTE_ID = a.ATTENDEE_ID ");
					}
				}
			}
			if (attributeFilter.containsKey("owner") && attributeFilter.get("owner") != null
					&& attributeFilter.get("owner").length() > 0) {
				sql.append(" JOIN USERS u ON u.USER_ID = a.OWNER_ID ");
			}
		}

		if (ownerId > 0) {
			List<Share> shares = getPassiveShares(ownerId);
			List<Integer> attendeeIds = new ArrayList<>();
			for (Share share : shares) {
				if (!onlywritableshares || !share.getReadonly()) {
					for (Attendee a : share.getAttendees()) {
						attendeeIds.add(a.getId());
					}
				}
			}

			if (!attendeeIds.isEmpty()) {
				sql.append(" WHERE (a.OWNER_ID = :ownerId OR a.ATTENDEE_ID IN (:aids))");
				oQueryParameters.put("aids", attendeeIds.toArray(new Integer[0]));
			} else {
				sql.append(" WHERE (a.OWNER_ID = :ownerId)");
			}

			oQueryParameters.put("ownerId", ownerId);
		} else {
			sql.append(" WHERE a.ATTENDEE_ID > 0");
		}

		sql.append(" AND a.ATTENDEE_HIDDEN IS NULL");

		if (attributeFilter != null && attributeFilter.size() > 0) {
			int counter = 0;
			for (Entry<String, String> entry : attributeFilter.entrySet()) {
				if (!entry.getKey().equalsIgnoreCase("name") && !entry.getKey().equalsIgnoreCase(Constants.EMAIL) && !entry.getKey().equalsIgnoreCase("owner"))
				{
					try {
						int intKey = Integer.parseInt(entry.getKey());
						String value = entry.getValue().trim();

						if (value.length() > 0) {
							sql.append(" AND at").append(intKey)
									.append(".attributeName_AN_ID IN (SELECT AN_ID FROM ATTRIBUTENAME WHERE AN_NAME IN (SELECT AN_NAME FROM ATTRIBUTENAME WHERE AN_ID = :aid")
									.append(counter).append(")) AND at").append(intKey).append(".ATTRIBUTE_VALUE like :av").append(counter);
							oQueryParameters.put("aid" + counter, intKey);
							oQueryParameters.put("av" + counter++, "%" + value + "%");
						}
					} catch (NumberFormatException e) {
						// ignore
					}
				}
			}

			if (attributeFilter.containsKey("name") && attributeFilter.get("name") != null
					&& attributeFilter.get("name").length() > 0) {
				sql.append(" AND a.ATTENDEE_NAME like :name");
				oQueryParameters.put("name", "%" + attributeFilter.get("name") + "%");
			}

			if (attributeFilter.containsKey(Constants.EMAIL) && attributeFilter.get(Constants.EMAIL) != null
					&& attributeFilter.get(Constants.EMAIL).length() > 0) {
				sql.append(" AND a.ATTENDEE_EMAIL like :email");
				oQueryParameters.put(Constants.EMAIL, "%" + attributeFilter.get(Constants.EMAIL) + "%");
			}

			if (attributeFilter.containsKey("owner") && attributeFilter.get("owner") != null
					&& attributeFilter.get("owner").length() > 0) {
				sql.append(" AND (u.USER_DISPLAYNAME like :owner OR u.USER_LOGIN like :owner)");
				oQueryParameters.put("owner", "%" + attributeFilter.get("owner") + "%");
			}
		}

		sql.append(" ORDER BY a.ATT_CREATED DESC");

		return sql.toString();
	}

	@SuppressWarnings({ "unchecked" })
	@Transactional(readOnly = true)
	public List<AttributeName> getAllAttributes(int ownerId) {
		Session session = sessionFactory.getCurrentSession();

		Query query = session.createQuery("SELECT a FROM AttributeName a ORDER BY a.name ASC");

		if (ownerId > 0) {
			String shared = " OR a.ownerId = -1 OR a.id IN (SELECT attributeName.id FROM Share share JOIN share.attendees as attendee JOIN attendee.attributes as attribute JOIN attribute.attributeName as attributeName WHERE share.recipient.id = :ownerId)";

			query = session.createQuery(
					"SELECT a FROM AttributeName a WHERE a.ownerId = :ownerId" + shared + " ORDER BY a.name ASC");
			query.setInteger("ownerId", ownerId);
		}

		List<AttributeName> attributeNames = query.list();

		Map<String, AttributeName> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		for (AttributeName attributeName : attributeNames) {
			if (!attributeName.getName().equals("Owner")) {
				if (!map.containsKey(attributeName.getName())) {
					map.put(attributeName.getName(), attributeName);
				} else {
					// own attributes have higher priority than shared ones
					if (attributeName.getOwnerId().equals(ownerId)) {
						map.remove(attributeName.getName());
						map.put(attributeName.getName(), attributeName);
					}
				}
			}
		}

		return new ArrayList<>(map.values());
	}

	@Transactional(readOnly = true)
	public Attendee get(Integer id) {
		Session session = sessionFactory.getCurrentSession();
		return (Attendee) session.get(Attendee.class, id);
	}

	@Transactional(readOnly = false)
	public void add(Attendee attendee) {
		Session session = sessionFactory.getCurrentSession();
		for (Attribute attribute : attendee.getAttributes()) {
			if (attribute.getAttributeName().getId() == null || attribute.getAttributeName().getId() == 0) {
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

		if (attendee.getOriginalId() != null) {
			Query query = session.createSQLQuery("UPDATE SHARES_ATTENDEE SET attendees_ATTENDEE_ID = :id WHERE attendees_ATTENDEE_ID = :origId")
					.setParameter("id", attendee.getId()).setParameter("origId", attendee.getOriginalId());
			query.executeUpdate();
		}

	}

	@Transactional(readOnly = false)
	public void add(List<Attendee> attendees) {
		Session session = sessionFactory.getCurrentSession();
		for (Attendee attendee : attendees) {
			for (Attribute attribute : attendee.getAttributes()) {
				attribute.setAttendeeId(attendee.getId());
				if (attribute.getAttributeName().getId() == null || attribute.getAttributeName().getId() == 0) {
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

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void update(Attendee attendee, boolean ismarkdeleted) {
		Session session = sessionFactory.getCurrentSession();
		attendee = (Attendee) session.merge(attendee);
		if (!ismarkdeleted)
		{
			for (Attribute attribute : attendee.getAttributes()) {
				if (attribute.getAttributeName().getId() == null || attribute.getAttributeName().getId() == 0) {
					session.saveOrUpdate(attribute.getAttributeName());
				}
			}
		}
		attendee.setUpdated(new Date());
		session.saveOrUpdate(attendee);

		if (!ismarkdeleted)
		{
			for (Attribute attribute : attendee.getAttributes()) {
				attribute.setAttendeeId(attendee.getId());
			}
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void update(List<Attendee> attendees, boolean isDelete) {
		Session session = sessionFactory.getCurrentSession();

		for (Attendee attendee : attendees) {
			if (!isDelete) {
				for (Attribute attribute : attendee.getAttributes()) {
					if (attribute.getAttributeName().getId() == null || attribute.getAttributeName().getId() == 0) {
						session.saveOrUpdate(attribute.getAttributeName());
					}
				}
			}
			attendee.setUpdated(new Date());
			session.saveOrUpdate(attendee);

			if (!isDelete) {
				for (Attribute attribute : attendee.getAttributes()) {
					attribute.setAttendeeId(attendee.getId());
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

		Query query = session.createSQLQuery("DELETE FROM PARTICIPANTS_ATTENDEE WHERE attendees_ATTENDEE_ID IN (:ids)")
				.setParameterList("ids", ids);
		query.executeUpdate();

		query = session.createSQLQuery("DELETE FROM SHARES_ATTENDEE WHERE attendees_ATTENDEE_ID IN (:ids)")
				.setParameterList("ids", ids);
		query.executeUpdate();

		query = session.createSQLQuery("DELETE FROM attendee_attributes WHERE ATTENDEE_ID IN (:ids)")
				.setParameterList("ids", ids);
		query.executeUpdate();

		query = session.createQuery("DELETE FROM Attribute a WHERE a.attendeeId IN (:ids)").setParameterList("ids",
				ids);
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
		if (ownerId == -1) {
			query = session.createQuery("SELECT a FROM AttributeName a WHERE a.name = :name").setString("name", name);
		} else {
			query = session.createQuery("SELECT a FROM AttributeName a WHERE a.name = :name AND a.ownerId = :owner")
					.setString("name", name).setInteger("owner", ownerId);
		}

		@SuppressWarnings("unchecked")
		List<AttributeName> attributeNames = query.list();

		if (!attributeNames.isEmpty())
			return attributeNames.get(0);

		return null;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void add(Invitation invitation) throws InterruptedException {
		internalAddInvitation(invitation);
	}

	public void internalAddInvitation(Invitation invitation) throws InterruptedException {
		Session session = sessionFactory.getCurrentSession();
		boolean saved = false;

		int counter = 1;

		while (!saved) {
			try {
				session.save(invitation);
				saved = true;
			} catch (org.hibernate.exception.LockAcquisitionException ex) {
				logger.info("lock on invitation table catched; retry counter: " + counter);
				counter++;

				if (counter > 60) {
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
		Query query = session
				.createQuery("SELECT i FROM Invitation i WHERE i.participationGroupId = :participationGroupId");
		@SuppressWarnings("unchecked")
		List<Invitation> invitations = query.setInteger("participationGroupId", participationGroupId).list();

		HashMap<Integer, Invitation> result = new HashMap<>();

		for (Invitation invitation : invitations) {
			if (!result.containsKey(invitation.getAttendeeId())) {
				result.put(invitation.getAttendeeId(), invitation);
			}
		}

		return result;
	}

	@Transactional
	public List<Invitation> getInvitationsForParticipationGroup(Integer participationGroupId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session
				.createQuery("SELECT i FROM Invitation i WHERE i.participationGroupId = :participationGroupId");
		@SuppressWarnings("unchecked")
		List<Invitation> invitations = query.setInteger("participationGroupId", participationGroupId).list();

		return invitations;
	}

	@Transactional(readOnly = true)
	public Invitation getInvitationForParticipationGroupAndAttendee(Integer participationGroupId, Integer attendeeId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(
				"SELECT i FROM Invitation i WHERE i.participationGroupId = :participationGroupId and i.attendeeId = :attendeeId");
		@SuppressWarnings("unchecked")
		List<Invitation> invitations = query.setInteger("participationGroupId", participationGroupId)
				.setInteger("attendeeId", attendeeId).list();

		if (!invitations.isEmpty())
			return invitations.get(0);

		return null;
	}

	@Transactional(readOnly = true)
	public Invitation getInvitationByUniqueId(String uniqueId) throws MessageException {
		return internalGetInvitationByUniqueId(uniqueId);
	}

	@Transactional(readOnly = true)
	public List<Invitation> getInvitationsByUniqueId(String uniqueId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("SELECT i FROM Invitation i WHERE i.uniqueId LIKE :uniqueId")
				.setString("uniqueId", "%" + uniqueId + "%");

		@SuppressWarnings("unchecked")
		List<Invitation> invitations = query.list();

		return invitations;
	}

	private Invitation internalGetInvitationByUniqueId(String uniqueId) throws MessageException {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("SELECT i FROM Invitation i WHERE i.uniqueId = :uniqueId")
				.setString("uniqueId", uniqueId).setCacheable(true);

		@SuppressWarnings("unchecked")
		List<Invitation> invitations = query.list();

		if (invitations.size() > 1) {
			throw new MessageException("there are more than one invitation with uniqueId = " + uniqueId);
		}

		if (!invitations.isEmpty())
			return invitations.get(0);

		return null;
	}

	@Transactional(readOnly = true)
	public Invitation getInvitation(int id) {
		Session session = sessionFactory.getCurrentSession();
		return (Invitation) session.get(Invitation.class, id);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void update(Invitation invitation) {
		Session session = sessionFactory.getCurrentSession();
		session.update(invitation);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void save(Share share) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(share);
	}

	@Transactional(readOnly = true)
	public List<Share> getShares(int userId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = null;
		if (userId > 0) {
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
		Query query = session.createQuery("FROM Share s WHERE s.recipient.id = :recipientId").setInteger("recipientId",
				recipientId);

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
		if (user.getGlobalPrivileges().get(GlobalPrivilege.ContactManagement) == 2) {
			ownerId = -1;
		} else {
			ownerId = user.getId();
		}

		List<AttributeName> attributeNames = getAllAttributes(ownerId);

		Map<Integer, List<String>> attributeValues = new HashMap<>();

		Session session = sessionFactory.getCurrentSession();

		for (AttributeName attributeName : attributeNames) {
			List<String> currentList = new ArrayList<>();
			
			if (attributeName.getOwnerId() != -1) {
				Query query = session
						.createQuery("SELECT a.value FROM Attribute a WHERE a.attributeName.id = :id ORDER BY a.value ASC")
						.setInteger("id", attributeName.getId());
				@SuppressWarnings("unchecked")
				List<String> attributes = query.list();
		
				for (String attribute : attributes) {
					if (!currentList.contains(attribute))
						currentList.add(attribute);
				}
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
	public void addTokens(List<String> tokens, Integer groupId) throws InterruptedException {
		for (String token : tokens) {
			Invitation invitation = new Invitation(groupId, token);

			internalAddInvitation(invitation);
		}
	}

	@Transactional(readOnly = false)
	public void decreaseInvitationAnswer(String invitationUID) throws MessageException {
		Session session = sessionFactory.getCurrentSession();
		Invitation invitation = internalGetInvitationByUniqueId(invitationUID);
		if (invitation != null) {
			if (invitation.getAnswers() > 0) {
				invitation.setAnswers(invitation.getAnswers() - 1);
			}
			session.saveOrUpdate(invitation);
		}
	}

	@Transactional(readOnly = true)
	public Map<Integer, String> getNamesForAttributenameIDs(Set<Integer> ids) {
		Session session = sessionFactory.getCurrentSession();
		Map<Integer, String> result = new HashMap<>();
		for (int id : ids) {
			AttributeName name = (AttributeName) session.get(AttributeName.class, id);
			result.put(id, name.getName());
		}
		return result;
	}

	@Transactional(readOnly = true)
	public boolean attendeeExists(String email, Integer id) throws Exception {
		Map<String, String> filterValues = new HashMap<>();
		filterValues.put(Constants.EMAIL, email);

		List<Attendee> attendees = getAttendees(id, filterValues, 1, 2);
		return !attendees.isEmpty();
	}

	@Transactional
	public void createDummyAttendees(int contacts, int userid) {
		List<Attendee> attendees = new ArrayList<>();

		AttributeName attributeName = getAttributeName("code", userid);

		if (attributeName == null) {
			attributeName = new AttributeName();
			attributeName.setName("code");
			attributeName.setOwnerId(userid);
			add(attributeName);
		}

		for (int i = 0; i < contacts; i++) {
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

	@Transactional
	public List<Integer> getAttendeesForUser(int userid) {
		Session session = sessionFactory.getCurrentSession();
		NativeQuery query = session.createSQLQuery("SELECT ATTENDEE_ID FROM ATTENDEE WHERE OWNER_ID = :id");

		@SuppressWarnings("rawtypes")
		List attendees = query.setInteger("id", userid).list();
		List<Integer> result = new ArrayList<>();

		for (Object o : attendees) {
			result.add(ConversionTools.getValue(o));
		}

		return result;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deactivateInvitations(List<Integer> invitationsToDeactivate) {
		Session session = sessionFactory.getCurrentSession();
		for (int id : invitationsToDeactivate) {
			Invitation invitation = (Invitation) session.get(Invitation.class, id);
			if (invitation != null) {
				invitation.setDeactivated(true);
				session.saveOrUpdate(invitation);
			}
		}		
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void activateInvitations(List<Integer> invitationsToActivate) {
		Session session = sessionFactory.getCurrentSession();
		for (int id : invitationsToActivate) {
			Invitation invitation = (Invitation) session.get(Invitation.class, id);
			if (invitation != null) {
				invitation.setDeactivated(false);
				session.saveOrUpdate(invitation);
			}
		}		
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteInvitations(List<Integer> invitationsToDelete) {
		Session session = sessionFactory.getCurrentSession();
		for (int id : invitationsToDelete) {
			Invitation invitation = (Invitation) session.get(Invitation.class, id);
			if (invitation != null) {
				session.delete(invitation);
			}
		}		
	}
}
