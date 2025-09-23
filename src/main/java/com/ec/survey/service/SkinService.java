package com.ec.survey.service;

import com.ec.survey.model.Skin;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("skinService")
public class SkinService extends BasicService {
	

	@Transactional(readOnly = true)
	public List<Skin> getAll(int userId) {
		Session session = sessionFactory.getCurrentSession();
		Query<Skin> query = session.createQuery("FROM Skin s WHERE s.isPublic = true OR s.owner.id = :userId", Skin.class).setParameter("userId", userId);
		return orderSkins(query.list());
	}

	@Transactional(readOnly = true)
	public List<Skin> getOwned(int userId) {
		Session session = sessionFactory.getCurrentSession();
		Query<Skin> query = session.createQuery("FROM Skin s WHERE s.owner.id = :userId", Skin.class).setParameter("userId", userId);
		return orderSkins(query.list());
	}

	@Transactional(readOnly = true)
	public List<Skin> getAllButEC(int userId) {
		Session session = sessionFactory.getCurrentSession();
		Query<Skin> query = session.createQuery("FROM Skin s WHERE (s.isPublic = true OR s.owner.id = :userId) AND NOT s.name LIKE 'ECA 2023' AND NOT s.name LIKE 'New Official EC Skin' ", Skin.class).setParameter("userId", userId);
		return orderSkins(query.list());
	}
	

	@Transactional(readOnly = true)
	public List<Skin> getAll() {
		Session session = sessionFactory.getCurrentSession();
		Query<Skin> query = session.createQuery("FROM Skin", Skin.class);
		return orderSkins(query.list());
	}
	
	private List<Skin> orderSkins(List<Skin> skins)
	{
		List<Skin> result = new ArrayList<>();
		
		Skin[] defaultSkins = new Skin[6];
		
		for (Skin skin: skins)
		{
			if (skin.getIsPublic())
			{
				switch (skin.getName())
				{
					case "EUSurveyNew.css":
						defaultSkins[0] = skin;
						break;
					case "New Official EC Skin":
						defaultSkins[1] = skin;
						break;
					case "ECA 2023":
						defaultSkins[2] = skin;
						break;
					default:
						result.add(skin);
						break;
				}
			} else {
				result.add(skin);
			}		
		}
		
		for (int i = 5; i >= 0; i--)
		{
			if (defaultSkins[i] != null) {
				result.add(0, defaultSkins[i]);
			}
		}		
		
		return result;
	}
	
	@Transactional(readOnly = true)
	public boolean nameAlreadyExists(String name, int userId) {
		Session session = sessionFactory.getCurrentSession();		
		Query<Skin> query = session.createQuery("FROM Skin s WHERE s.name LIKE :name AND s.owner.id = :userId", Skin.class).setParameter("userId", userId).setParameter("name", name);
		return !query.list().isEmpty();
	}
	
	@Transactional(readOnly = true)
	public Skin get( Integer id ) {
		Session session = sessionFactory.getCurrentSession();
		
		Skin skin = session.get(Skin.class, id);
		if (skin != null) skin.createMissingElements();
		
		return skin;
	}
	
	@Transactional
	public void add(Skin skin) {
		Session session = sessionFactory.getCurrentSession();		
		session.save(skin);
	}
	
	@Transactional
	public void save(Skin skin) {
		Session session = sessionFactory.getCurrentSession();		
		session.saveOrUpdate(skin);
	}

	@Transactional
	public void delete(Skin skin) {
		Session session = sessionFactory.getCurrentSession();		
		session.delete(skin);
	}

	@Transactional(readOnly = true)
	public String getNameForNewSkin(String login) {
		Session session = sessionFactory.getCurrentSession();		
		Query<String> query = session.createQuery("SELECT s.name FROM Skin s WHERE s.name LIKE :name", String.class).setParameter("name", "%" + login + "%");

		List<String> skins = query.list();
		
		int i = 1;
		while(true)
		{
			String s = login + "_skin_" + i;
			if (skins.contains(s))
			{
				i++;
			} else {
				return s;
			}
		}
	}

	@Transactional(readOnly = true)
	public List<Integer> get(String name) {
		Session session = sessionFactory.getCurrentSession();		
		Query<Integer> query = session.createQuery("SELECT s.id FROM Skin s WHERE s.name = :name", Integer.class).setParameter("name", name);

		return query.list();
	}
	
	@Transactional(readOnly = true)
	public Skin getSkin(String name) {
		Session session = sessionFactory.getCurrentSession();		
		Query<Skin> query = session.createQuery("FROM Skin s WHERE s.name = :name", Skin.class).setParameter("name", name);

		List<Skin> skins = query.list();
		return !skins.isEmpty() ? skins.get(0) : null;
	}

	@Transactional
	public void removeSurveySkins(List<Integer> skinIds) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("UPDATE Survey s SET s.skin = NULL WHERE s.skin.id IN (:skinIds)").setParameter("skinIds", skinIds);
		query.executeUpdate();
	}
}
