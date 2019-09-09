package com.ec.survey.service;

import com.ec.survey.model.Skin;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("skinService")
public class SkinService extends BasicService {
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Skin> getAll(int userId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM Skin s WHERE s.isPublic = true OR s.owner.id = :userId").setInteger("userId", userId);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Skin> getAllButEC(int userId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM Skin s WHERE (s.isPublic = true OR s.owner.id = :userId) AND NOT s.name LIKE 'Official EC Skin' AND NOT s.name LIKE 'New Official EC Skin' ").setInteger("userId", userId);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Skin> getAll() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("FROM Skin");
		return query.list();
	}
	
	@Transactional(readOnly = true)
	public boolean nameAlreadyExists(String name, int userId) {
		Session session = sessionFactory.getCurrentSession();		
		Query query = session.createQuery("FROM Skin s WHERE s.name LIKE :name AND s.owner.id = :userId").setInteger("userId", userId).setString("name", name);
		return query.list().size() > 0;
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
		return skins.size() > 0 ? skins.get(0) : null;
	}
}
