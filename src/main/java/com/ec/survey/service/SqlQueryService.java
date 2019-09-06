package com.ec.survey.service;

import java.util.Date;
import java.util.Map;
import org.hibernate.Query;
import org.springframework.stereotype.Service;

@Service
public class SqlQueryService {

	public void setParameters(Query query, Map<String, Object> parameters) {
		for (String key : parameters.keySet()) {
			setParameter(parameters, key, query);
		}
	}

	private void setParameter(Map<String, Object> parameters, String key, Query query) {
		Object parameter = parameters.get(key);
		if (parameter instanceof String) {
			query.setString(key, (String) parameter);
		} else if (parameter instanceof Integer) {
			query.setInteger(key, (Integer) parameter);
		} else if (parameter instanceof Integer[]) {
			query.setParameterList(key, (Integer[]) parameter);
		} else if (parameter instanceof Date) {
			query.setDate(key, (Date) parameter);
		}
	}
}
