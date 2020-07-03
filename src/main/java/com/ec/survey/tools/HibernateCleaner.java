package com.ec.survey.tools;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.HibernateProxyHelper;

import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.util.*;

public class HibernateCleaner {
    private static final Logger logger = Logger.getLogger(HibernateCleaner.class);

	public static Object clean(Session session, Object obj) throws Exception {
        return (clean(session, obj, new HashMap<>()));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object clean(Session session,
                                Object obj, 
                                Map<Class, Map<Object, Object>> visitedObjects) throws Exception {
        Object newObj;
        Object value = null;
        Object cleanValue = null;
        Map.Entry m;
        Class clazz;
        Object[] array;
        Collection collection;        
        Map map;
        PropertyDescriptor[] descriptors;
        String property;
        ClassMetadata clazzMetaData;
        Map<Object, Object> visitedObjectsInClass;
        int index, length;
        
        if (obj == null)
            return (null);

        if ((obj instanceof Boolean) || (obj instanceof Number) || (obj.getClass().isEnum()) ||
            (obj instanceof Character) || (obj instanceof String) || 
            (obj instanceof Blob) || (obj instanceof InputStream))
            return (obj);
            
        if (obj instanceof Date)
            return (new Date (((Date) obj).getTime()));
            
        if (obj instanceof Calendar)
            return (((Calendar) obj).clone());

        if (obj instanceof Object[]) {
            array = ((Object[]) obj).clone();
            length = array.length;
            for (index = 0; index < length; index++)
                array[index] = clean(session, array[index], visitedObjects);
                
            return (array);
        }

        if (obj instanceof Collection) {
            collection = createCollection((Collection) obj);
                
            if (Hibernate.isInitialized(obj)) {
                for (Object member: (Collection) obj)
                    collection.add (clean(session, member, visitedObjects));
            }
                
            return (collection);
        }

        if (obj instanceof Map) {
            map = createMap((Map) obj);
            
            if (Hibernate.isInitialized(obj)) {
                for (Object member: ((Map)obj).entrySet()) {
                    m = (Map.Entry) member;
                    clean(session, m.getKey(), visitedObjects);
                    clean(session, m.getValue(), visitedObjects);
                    map.put (m.getKey(), m.getValue());
                }
            }
            
            return (map);
        }

        if (obj instanceof HibernateProxy) {
            clazz = HibernateProxyHelper.getClassWithoutInitializingProxy(obj);
        } else {
            clazz = obj.getClass();
        }

        visitedObjectsInClass = visitedObjects.get(clazz);
        if (visitedObjectsInClass == null) {
            visitedObjectsInClass = new HashMap<>();
            visitedObjects.put(clazz, visitedObjectsInClass);
        } else if (visitedObjectsInClass.containsKey(obj)) {
            return visitedObjectsInClass.get(obj);
        }

        newObj = clazz.newInstance();           
        visitedObjectsInClass.put(obj, newObj);

        if (!Hibernate.isInitialized(obj)) {
            if (session != null) {
                clazzMetaData = session.getSessionFactory().getClassMetadata(newObj.getClass());
                Serializable id = clazzMetaData.getIdentifier(obj, (SessionImplementor)session);
                clazzMetaData.setIdentifier(newObj, id, (SessionImplementor)session);
            }
        } else {
            descriptors = PropertyUtils.getPropertyDescriptors(newObj);
            length = descriptors.length;
            for (index = 0; index < length; index++) {
                property = descriptors[index].getName();
                if (!property.equals("class")) {
                    try {
                        value = PropertyUtils.getProperty(obj, property);
                        cleanValue = clean(session, value, visitedObjects);
                        PropertyUtils.setProperty(newObj, property, cleanValue);
                    } catch (NoSuchMethodException e) {
                    	//can happen for properties that have no setter
                    } catch (Exception e) {
                        logger.error(e.getLocalizedMessage(), e);
                    }
                }
            }
        }
        
        return (newObj);
    }

    @SuppressWarnings("rawtypes")
	private static Collection createCollection(Collection obj) {
        Collection newObj = null;
        
        if (obj instanceof SortedSet)
            newObj = new TreeSet ();
        else if (obj instanceof Set)
            newObj = new HashSet ();
        else
            newObj = new ArrayList ();
        
        return (newObj);
    }

    @SuppressWarnings("rawtypes")
	private static Map createMap(Map obj) {
        Map newObj = null;
        
        if (obj instanceof SortedMap)
            newObj = new TreeMap ();
        else
            newObj = new HashMap ();
        
        return (newObj);
    }
}

