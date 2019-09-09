package com.ec.survey.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import org.springframework.stereotype.Service;

@Service
public class SqlQueryService {

	public void setParameters(Query query, Map<String, Object> parameters) throws Exception {
		List<String> params = Arrays.asList(query.getNamedParameters());
		for (String key : parameters.keySet()) {
			if (params.contains(key))
			{
				setParameter(parameters, key, query);
			}
		}
	}

	private void setParameter(Map<String, Object> parameters, String key, Query query) throws Exception {
		try {
			Object parameter = parameters.get(key);
			if (parameter instanceof String) {
				query.setString(key, (String) parameter);
			} else if (parameter instanceof String[]) {
				query.setParameterList(key, (String[]) parameter);
			} else if (parameter instanceof Integer) {
				query.setInteger(key, (Integer) parameter);
			} else if (parameter instanceof Double) {
				query.setDouble(key, (Double) parameter);
			} else if (parameter instanceof Integer[]) {
				query.setParameterList(key, (Integer[]) parameter);
			} else if (parameter instanceof Date) {
				query.setParameter(key,  (Date) parameter);
			} else if (parameter == null) {
				query.setParameter(key, null);
			} else {
				//this should not happen
				throw new Exception("unknown parameter type: " + parameter);
			}
		} catch (Exception e) {
			throw e;
		}
	}
}
