package com.ec.survey.controller;

import com.ec.survey.replacements.Pair;
import com.ec.survey.tools.Constants;
import org.apache.http.NameValuePair;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.net.ssl.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/chatbot")
public class ChatbotController extends BasicController {

	private @Value("${chat.tokenHost:#{null}}") String tokenHost;
	private @Value("${chat.client_id:#{null}}") String client_id;
	private @Value("${chat.client_secret:#{null}}") String client_secret;
	private @Value("${chat.ragHost:#{null}}") String ragHost;
	private @Value("${chat.model_id:#{null}}") String model_id;

	@PostMapping(value = "/send")
	public @ResponseBody String sendChatMessage(HttpServletRequest request) {
		String message = request.getParameter("message");
		boolean firstChatMessage = request.getParameter("firstChatMessage") != null && request.getParameter("firstChatMessage").equals("true");

		try {

			if (tokenHost == null || client_id == null || client_secret == null) {
				logger.error("invalid chat configuration");
				return Constants.ERROR;
			}

			String token = (String)request.getSession().getAttribute("chattoken");

			if (token != null) {
				// TODO: check if token is still valid
				Date chattokenend = (Date)request.getSession().getAttribute("chattokenend");
				if (chattokenend.before(new Date())) {
					token = null;
				}
			}

			if (token == null || firstChatMessage) {
				Pair<String, Date> tokenAndDate = getToken();
				token = tokenAndDate.getKey();
				request.getSession().setAttribute("chattoken", token);
				request.getSession().setAttribute("chattokenend", tokenAndDate.getValue());
			}

			List<String[]> history = (List<String[]>)request.getSession().getAttribute("chathistory");
			if (history == null || firstChatMessage) {
				history = new ArrayList<>();
			}

			var reply = callRAG(token, message, history);
			history.add(new String[]{message, reply});
			request.getSession().setAttribute("chathistory", history);

			return reply != null ? reply : Constants.ERROR;

		} catch (Exception e) {
			logger.error(e.getMessage());
			return Constants.ERROR;
		}
	}

	private Pair<String, Date> getToken() {
		try (CloseableHttpClient httpclient = HttpClients.createSystem()) {
			sessionService.initializeProxy();

			HttpPost httppost = new HttpPost(tokenHost);
			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");

			List<NameValuePair> urlParameters = new ArrayList<>();

			urlParameters.add(new BasicNameValuePair("grant_type", "client_credentials"));
			urlParameters.add(new BasicNameValuePair("client_id", client_id));
			urlParameters.add(new BasicNameValuePair("client_secret", client_secret));

			httppost.setEntity(new UrlEncodedFormEntity(urlParameters));

			try (CloseableHttpResponse response = httpclient.execute(httppost)) {
				HttpEntity entity = response.getEntity();
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != 200) return null;
				String responseString = EntityUtils.toString(entity);
				JSONObject responseObject = new JSONObject(responseString);

				String token = responseObject.getString("access_token");
				int expires_in = responseObject.getInt("expires_in");

				Date now = new Date();
				Calendar cal = Calendar.getInstance();
				cal.setTime(now);
				cal.add(Calendar.SECOND, expires_in);
				Date end = cal.getTime();

				return new Pair<>(token, end);
			}

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return null;
	}

	private JSONArray makeHistory(List<String[]> history) {
		JSONArray historyArray = new JSONArray();
		for (String[] historyItem : history) {
			JSONArray a = new JSONArray();
			a.put("user");
			a.put(historyItem[0]);
			historyArray.put(a);
			a = new JSONArray();
			a.put("assistant");
			a.put(historyItem[1]);
			historyArray.put(a);
		}
		return historyArray;
	}

	private String callRAG(String token, String message, List<String[]> history) {
		try (CloseableHttpClient httpclient = HttpClients.createSystem()) {
			sessionService.initializeProxy();

			HttpPost httppost = new HttpPost(ragHost);

			httppost.addHeader("Authorization", "Bearer " + token);
			httppost.addHeader("Content-Type", "application/json");

			String query = message;
			String prompt = "Use HTML instead of markdown for the response.";

			JSONObject json  = new JSONObject();
			json.put("client_id", client_id);
			json.put("query", query);
			json.put("system_prompt", prompt);
			json.put("model_id", model_id);
			json.put("temperature", 0);
			json.put("top_k", 5);

			JSONArray ja = new JSONArray();
			//ja.put("a4_ecollege");
			//ja.put("a4_mwp_spo_pages");
			//ja.put("a4_rsp");
			ja.put("a4_eusurvey");

			json.put("datasources", ja);
			json.put("streaming", false);
			json.put("relevance_threshold", 0.2);
			json.put("include_metadata", false);
			json.put("snc", false);
			if (!history.isEmpty()) {
				json.put("chat_history", makeHistory(history));
			}

			String jsons = json.toString();

			httppost.setEntity(new StringEntity(jsons));

			try (CloseableHttpResponse response = httpclient.execute(httppost)) {
				HttpEntity entity = response.getEntity();
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != 200 && statusCode != 202) return null;
				String responseString = EntityUtils.toString(entity);
				JSONObject responseObject = new JSONObject(responseString);
				return responseObject.getString("answer");
			}

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return null;
	}

}
