package com.ec.survey.service;

import com.ec.survey.model.Skin;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("skinService")
public class SkinService extends BasicService {
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Skin> getAll(int userId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM Skin s WHERE s.isPublic = true OR s.owner.id = :userId").setInteger("userId", userId);
		return orderSkins(query.list());
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Skin> getAllButEC(int userId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM Skin s WHERE (s.isPublic = true OR s.owner.id = :userId) AND NOT s.name LIKE 'Official EC Skin' AND NOT s.name LIKE 'New Official EC Skin' ").setInteger("userId", userId);
		return orderSkins(query.list());
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Skin> getAll() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM Skin");
		return orderSkins(query.list());
	}
	
	private List<Skin> orderSkins(List<Skin> skins)
	{
		List<Skin> result = new ArrayList<>();
		
		Skin[] defaultSkins = new Skin[5];
		
		for (Skin skin: skins)
		{
			if (skin.getOwner().getLogin().equalsIgnoreCase("admin"))
			{
				switch (skin.getName())
				{
					case "EUSurveyNew.css":
						defaultSkins[0] = skin;
						break;
					case "New Official EC Skin":
						defaultSkins[1] = skin;
						break;
					case "ECA Skin":
						defaultSkins[2] = skin;
						break;
					case "EUSurvey.css":
						defaultSkins[3] = skin;
						break;
					case "Official EC Skin":
						defaultSkins[4] = skin;
						break;
					default:
						result.add(skin);
						break;
				}
			} else {
				result.add(skin);
			}		
		}
		
		for (int i = 0; i < 5; i++)
		{
			result.add(i, defaultSkins[i]);
		}		
		
		return result;
	}
	
	@Transactional(readOnly = true)
	public boolean nameAlreadyExists(String name, int userId) {
		Session session = sessionFactory.getCurrentSession();		
		Query query = session.createQuery("FROM Skin s WHERE s.name LIKE :name AND s.owner.id = :userId").setInteger("userId", userId).setString("name", name);
		return !query.list().isEmpty();
	}
	
	@Transactional(readOnly = true)
	public Skin get( Integer id ) {
		Session session = sessionFactory.getCurrentSession();
		
		Skin skin = (Skin) session.get(Skin.class, id);
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
		Query query = session.createQuery("SELECT s.name FROM Skin s WHERE s.name LIKE :name").setString("name", "%" + login + "%");
		@SuppressWarnings("unchecked")
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
		Query query = session.createQuery("SELECT s.id FROM Skin s WHERE s.name = :name").setString("name", name);
		@SuppressWarnings("unchecked")
		List<Integer> ids = query.list();
		return ids;
	}
	
	@Transactional(readOnly = true)
	public Skin getSkin(String name) {
		Session session = sessionFactory.getCurrentSession();		
		Query query = session.createQuery("FROM Skin s WHERE s.name = :name").setString("name", name);
		@SuppressWarnings("unchecked")
		List<Skin> skins = query.list();
		return !skins.isEmpty() ? skins.get(0) : null;
	}
}
