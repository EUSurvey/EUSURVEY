package com.ec.survey.service;

import com.ec.survey.exception.MessageException;
import com.ec.survey.model.WhitelistLabel;
import com.ec.survey.model.WhitelistedUser;
import com.ec.survey.model.administration.*;
import com.ec.survey.tools.LoginAlreadyExistsException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.naming.NamingException;
import java.util.*;

@Service("organisationService")
public class OrganisationService extends BasicService {

	@Resource(name = "ldapService")
	protected LdapService ldapService;

	private Organisation getOrganisationByName(String name) {
		Session session = sessionFactory.getCurrentSession();
		Query<Organisation> query = session.createQuery("FROM Organisation o where o.organisation = :name", Organisation.class).setParameter("name", name);
		List<Organisation> list = query.list();

		if (!list.isEmpty())
		{
			return list.get(0);
		}

		return null;
	}

	@Transactional
	public Organisation getOrganisation(String name) {
		return getOrganisationByName(name);
	}

	@Transactional
	public boolean addManager(String organisation, User user) {
		Session session = sessionFactory.getCurrentSession();

		Organisation o = getOrganisationByName(organisation);
		if (o == null)
		{
			o = new Organisation();
			o.setOrganisation(organisation);
		} else if (o.getManagers().stream().anyMatch(u -> u.getId().equals(user.getId()))) {
			return false;
		}

		o.getManagers().add(user);
		session.save(o);
		return true;
	}

	@Transactional
	public boolean deleteManager(String organisation, int userId) throws MessageException {
		Session session = sessionFactory.getCurrentSession();

		Organisation o = getOrganisationByName(organisation);

		if (o == null) {
			throw new MessageException("organisation not found");
		}

		if (o.getManagers().stream().noneMatch(u -> u.getId().equals(userId))) {
			return false;
		}

		var user = o.getManagers().stream().filter(u -> u.getId().equals(userId)).findFirst().get();
		o.getManagers().remove(user);

		session.save(o);
		return true;
	}

	@Transactional
	public User prepareAuthorizedUser(User u, boolean isEcasUser, String login) throws NamingException, LoginAlreadyExistsException {
		if (u == null && isEcasUser) {
			u = new User();
			u.setLogin(login);
			u.setDisplayName(ldapService.getMoniker(login));
			u.setEmail(ldapService.getEmail(login));
			u.setDepartments(ldapService.getUserLDAPGroups(u.getLogin()));
			u.setType(User.ECAS);

			Role fmRole = null;
			for (Role role : administrationService.getAllRoles()) {
				if (role.getName().equalsIgnoreCase("Form Manager")) {
					fmRole = role;
					break;
				}
			}

			u.getRoles().add(fmRole);
			u = administrationService.createUser(u);
		}

		return u;
	}

	@Transactional
	public boolean addAuthorisedUser(String organisation, int userId) {
		Session session = sessionFactory.getCurrentSession();
		User user = administrationService.getUser(userId);

		Organisation o = getOrganisationByName(organisation);
		if (o == null)
		{
			o = new Organisation();
			o.setOrganisation(organisation);
		} else if (o.getWhitelist().stream().anyMatch(u -> u.getUser().getId().equals(userId))) {
			return false;
		}

		WhitelistedUser wu = new WhitelistedUser();
		wu.setUser(user);
		wu.setAdded(new Date());

		o.getWhitelist().add(wu);
		session.save(wu);
		session.save(o);
		return true;
	}

	@Transactional
	public boolean deleteAuthorisedUser(String organisation, int userId) throws MessageException {
		Session session = sessionFactory.getCurrentSession();

		Organisation o = getOrganisationByName(organisation);

		if (o == null) {
			throw new MessageException("organisation not found");
		}

		if (o.getWhitelist().stream().noneMatch(u -> u.getId().equals(userId))) {
			return false;
		}

		var user = o.getWhitelist().stream().filter(u -> u.getId().equals(userId)).findFirst().get();
		o.getWhitelist().remove(user);

		session.delete(user);
		session.save(o);
		return true;
	}

	@Transactional
	public List<Organisation> getOrganisationsForManager(Integer userId) {
		Session session = sessionFactory.getCurrentSession();

		Query<Organisation> query = session.createQuery("SELECT o FROM Organisation o JOIN o.managers c where c.id = :userId", Organisation.class).setParameter("userId", userId);
		return query.list();
	}

	@Transactional
	public List<String> getLabels(String term) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "SELECT DISTINCT l.name FROM WhitelistLabel l WHERE l.name LIKE :term";
		Query<String> query = session.createQuery(hql, String.class).setParameter("term", term + "%");
		return query.list();
	}

	@Transactional
	public Organisation getOrganisationForWhitelistedUser(int userId) {
		Session session = sessionFactory.getCurrentSession();

		Query<Organisation> query = session.createQuery("SELECT o FROM Organisation o JOIN o.whitelist c where c.id = :userId", Organisation.class).setParameter("userId", userId);
		var list = query.list();
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	@Transactional
	public void addLabel(WhitelistedUser whitelistedUser, String label) {
		Session session = sessionFactory.getCurrentSession();

		var newLabel = new WhitelistLabel();

		Query<WhitelistLabel> query = session.createQuery("FROM WhitelistLabel l WHERE l.name = :label", WhitelistLabel.class).setParameter("label", label);
		var list = query.list();
		if (list.size() > 0) {
			newLabel = list.get(0);
		} else {
			newLabel.setName(label);
			session.save(newLabel);
		}

		whitelistedUser.getLabels().add(newLabel);
		session.save(whitelistedUser);
	}

	@Transactional
	public void removeLabel(WhitelistedUser whitelistedUser, int labelId) {
		Session session = sessionFactory.getCurrentSession();

		var label = whitelistedUser.getLabels().stream().filter(l -> l.getId().equals(labelId)).findFirst().get();
		whitelistedUser.getLabels().remove(label);
		session.save(whitelistedUser);
	}

	@Transactional
	public boolean isWhitelisted(int userId) {
		Session session = sessionFactory.getCurrentSession();
		Query<Organisation> query = session.createQuery("SELECT o FROM Organisation o JOIN o.whitelist c JOIN c.user u where u.id = :userId", Organisation.class).setParameter("userId", userId);
		var list = query.list();
		return !list.isEmpty();
	}

	@Transactional
	public List<Organisation> getOrganisationsForWhitelistedUser(Integer userId) {
		Session session = sessionFactory.getCurrentSession();
		Query<Organisation> query = session.createQuery("SELECT o FROM Organisation o JOIN o.whitelist c JOIN c.user u where u.id = :userId", Organisation.class).setParameter("userId", userId);
		return query.list();
	}
}
