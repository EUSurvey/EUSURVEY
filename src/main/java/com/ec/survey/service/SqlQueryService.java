package com.ec.survey.service;

import java.util.*;

import org.hibernate.query.Query;
import org.springframework.stereotype.Service;

import com.ec.survey.exception.MessageException;

@Service
public class SqlQueryService {

	public <T> void setParameters(Query query, Map<String, Object> parameters) throws Exception {
		Set<String> params = query.getParameterMetadata().getNamedParameterNames();
		for (String key : parameters.keySet()) {
			if (params.contains(key)){
				setParameter(parameters, key, query);
			}
		}
	}

	private <T> void setParameter(Map<String, Object> parameters, String key, Query query) throws MessageException {
		Object parameter = parameters.get(key);
		if (parameter instanceof String) {
			query.setParameter(key, (String) parameter);
		} else if (parameter instanceof String[]) {
			query.setParameterList(key, (String[]) parameter);
		} else if (parameter instanceof Integer) {
			query.setParameter(key, (Integer) parameter);
		} else if (parameter instanceof Double) {
			query.setParameter(key, (Double) parameter);
		} else if (parameter instanceof Integer[]) {
			query.setParameterList(key, (Integer[]) parameter);
		} else if (parameter instanceof Date) {
			query.setParameter(key,  (Date) parameter);
		} else if (parameter == null) {
			query.setParameter(key, null);
		} else {
			//this should not happen
			throw new MessageException("unknown parameter type: " + parameter);
		}
	}
}
