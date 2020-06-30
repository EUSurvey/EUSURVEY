package com.ec.survey.service;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ec.survey.model.Message;
import com.ec.survey.model.MessageType;

@Service("systemService")
@Configurable
public class SystemService extends BasicService {
	
	@Transactional(readOnly = true)
	public Message getMessage()
	{
		Session session = sessionFactory.getCurrentSession();		
		Query q = session.createQuery("FROM Message m WHERE m.userId is null");
		@SuppressWarnings("unchecked")
		List<Message> messages = q.list();
		
		Message result = new Message();
		
		if (messages.size() > 0)
		{
			result =  messages.get(0);
						
			if (result.getAutoDeactivate() != null && result.getAutoDeactivate().before(new Date()))
			{
				result.setActive(false);
			}
		}
		
		q = session.createQuery("FROM MessageType m ORDER BY m.criticality ASC");
		@SuppressWarnings("unchecked")
		List<MessageType> messageTypes = q.list();
		
		result.setTypes(messageTypes);
		
		return result;
	}
	
	@Transactional(readOnly = true)
	public Message getUserMessage(int userId)
	{
		Session session = sessionFactory.getCurrentSession();		
		Query q = session.createQuery("FROM Message m WHERE m.userId = :id");
		q.setInteger("id", userId);
		@SuppressWarnings("unchecked")
		List<Message> messages = q.list();
		
		Message result = new Message();
		
		if (messages.size() > 0)
		{
			result =  messages.get(0);
						
			if (result.getAutoDeactivate() != null && result.getAutoDeactivate().before(new Date()))
			{
				result.setActive(false);
			}
		} else {
			return null;
		}
		
		q = session.createQuery("FROM MessageType m ORDER BY m.criticality ASC");
		@SuppressWarnings("unchecked")
		List<MessageType> messageTypes = q.list();
		
		result.setTypes(messageTypes);
		
		return result;
	}
	
	@Transactional(readOnly = true)
	public Message getAdminMessage()
	{
		Session session = sessionFactory.getCurrentSession();		
		Query q = session.createQuery("FROM Message m WHERE m.userId = -1 AND m.type = 5");
		@SuppressWarnings("unchecked")
		List<Message> messages = q.list();
		
		Message result = new Message();
		
		if (messages.size() > 0)
		{
			result =  messages.get(0);
						
			if (result.getAutoDeactivate() != null && result.getAutoDeactivate().before(new Date()))
			{
				result.setActive(false);
			}
		} else {
			return null;
		}
		
		q = session.createQuery("FROM MessageType m ORDER BY m.criticality ASC");
		@SuppressWarnings("unchecked")
		List<MessageType> messageTypes = q.list();
		
		result.setTypes(messageTypes);
		
		return result;
	}
	
	@Transactional
	public void sendUserSuccessMessage(int userid, String message)
	{
		Message m = new Message();
		m.setType(4);
		m.setCriticality(1);
		m.setText(message);
		m.setUserId(userid);
		save(m);
	}
	
	@Transactional
	public void sendUserErrorMessage(int userid, String message)
	{
		Message m = new Message();
		m.setType(4);
		m.setCriticality(3);
		m.setText(message);
		m.setUserId(userid);
		save(m);
	}
	
	@Transactional
	public void sendAdminErrorMessage(String message)
	{
		Message m = new Message();
		m.setType(5);
		m.setCriticality(3);
		m.setText(message);
		m.setUserId(-1);
		save(m);
	}
	
	@Transactional
	public void save(Message message)
	{
		Session session = sessionFactory.getCurrentSession();		
		session.saveOrUpdate(message);
	}
	
	@Transactional
	public void save(MessageType messageType)
	{
		Session session = sessionFactory.getCurrentSession();		
		session.saveOrUpdate(messageType);
	}

	@Transactional
	public void deleteMessage(int id, int userId, boolean userisadmin) {
		Session session = sessionFactory.getCurrentSession();
		String hql; 
		if (userisadmin)
		{
			hql = "DELETE FROM Message m WHERE (m.userId = :userid OR m.userId = -1) AND m.id = :id"; 
		} else {
			hql = "DELETE FROM Message m WHERE m.userId = :userid AND m.id = :id"; 
		}
		
		Query q = session.createQuery(hql);
		q.setInteger("userid", userId);
		q.setInteger("id", id);
		q.executeUpdate();
		
	}	
	
}
