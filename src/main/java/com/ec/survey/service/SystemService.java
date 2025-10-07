package com.ec.survey.service;

import java.util.Date;
import java.util.List;

import org.hibernate.query.Query;
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
		Query<Message> q = session.createQuery("FROM Message m WHERE m.userId is null", Message.class);
		List<Message> messages = q.list();
		
		Message result = new Message();
		
		if (!messages.isEmpty())
		{
			result =  messages.get(0);
						
			if (result.getAutoDeactivate() != null && result.getAutoDeactivate().before(new Date()))
			{
				result.setActive(false);
			}
		}

		Query<MessageType> q2 = session.createQuery("FROM MessageType m ORDER BY m.criticality ASC", MessageType.class);
		List<MessageType> messageTypes = q2.list();
		
		result.setTypes(messageTypes);
		
		return result;
	}
	
	@Transactional(readOnly = true)
	public Message getUserMessage(int userId)
	{
		Session session = sessionFactory.getCurrentSession();		
		Query<Message> q = session.createQuery("FROM Message m WHERE m.userId = :id", Message.class);
		q.setParameter("id", userId);
		List<Message> messages = q.list();
		
		Message result;
		
		if (!messages.isEmpty())
		{
			result =  messages.get(0);
						
			if (result.getAutoDeactivate() != null && result.getAutoDeactivate().before(new Date()))
			{
				result.setActive(false);
			}
		} else {
			return null;
		}
		
		Query<MessageType> q2 = session.createQuery("FROM MessageType m ORDER BY m.criticality ASC", MessageType.class);
		List<MessageType> messageTypes = q2.list();
		
		result.setTypes(messageTypes);
		
		return result;
	}
	
	@Transactional(readOnly = true)
	public Message getAdminMessage()
	{
		Session session = sessionFactory.getCurrentSession();		
		Query<Message> q = session.createQuery("FROM Message m WHERE m.userId = -1 AND m.type = 5", Message.class);
		List<Message> messages = q.list();
		
		Message result;
		
		if (!messages.isEmpty())
		{
			result =  messages.get(0);
						
			if (result.getAutoDeactivate() != null && result.getAutoDeactivate().before(new Date()))
			{
				result.setActive(false);
			}
		} else {
			return null;
		}
		
		Query<MessageType> q2 = session.createQuery("FROM MessageType m ORDER BY m.criticality ASC", MessageType.class);
		List<MessageType> messageTypes = q2.list();
		
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
		m.setTime(5);
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
		
		@SuppressWarnings("unchecked")
		Query<Message> q = session.createQuery(hql);
		q.setParameter("userid", userId);
		q.setParameter("id", id);
		q.executeUpdate();
		
	}	
	
}
