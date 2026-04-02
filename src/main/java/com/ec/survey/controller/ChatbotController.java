package com.ec.survey.controller;

import com.ec.survey.replacements.Pair;
import com.ec.survey.tools.Constants;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/chatbot")
public class ChatbotController extends BasicController {

	private @Value("${chat.tokenHost:#{null}}") String tokenHost;
	private @Value("${chat.sessionHost:#{null}}") String sessionHost;
	private @Value("${chat.pipelineHost:#{null}}") String pipelineHost;
	private @Value("${chat.client_id:#{null}}") String client_id;
	private @Value("${chat.client_secret:#{null}}") String client_secret;
	private @Value("${chat.ragHost:#{null}}") String ragHost;
	private @Value("${chat.APIKey:#{null}}") String APIKey;

	@PostMapping(value = "/send")
	public @ResponseBody String sendChatMessage(HttpServletRequest request) {
		String message = request.getParameter("message");
		boolean firstChatMessage = request.getParameter("firstChatMessage") != null && request.getParameter("firstChatMessage").equals("true");

		try {

			if (tokenHost == null || pipelineHost == null || sessionHost == null || client_id == null || client_secret == null) {
				logger.error("invalid chat configuration");
				return Constants.ERROR;
			}

			String token = (String)request.getSession().getAttribute("chattoken");
			String sessionId = (String)request.getSession().getAttribute("chatsessionid");

			String pipelineId = getPipelineId();

			if (token != null) {
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

			if (sessionId == null || firstChatMessage) {
				sessionId = getSessionId(pipelineId);
			}

			var reply = callRAG(token, message, sessionId);
			return reply != null ? reply : Constants.ERROR;

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
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

	private String getPipelineId() {
		try (CloseableHttpClient httpclient = HttpClients.createSystem()) {
			sessionService.initializeProxy();

			HttpGet httpget = new HttpGet(pipelineHost);
			httpget.addHeader("Authorization", "Bearer " + APIKey);
			httpget.addHeader("Accept", "application/json");

			try (CloseableHttpResponse response = httpclient.execute(httpget)) {
				HttpEntity entity = response.getEntity();
				int statusCode = response.getStatusLine().getStatusCode();

				if (statusCode != 200) return null;
				String responseString = EntityUtils.toString(entity);
				JSONObject responseObject = new JSONObject(responseString);

				return responseObject.getString("pipeline_id");
			}

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return null;
	}

	private String getSessionId(String pipelineId) {
		try (CloseableHttpClient httpclient = HttpClients.createSystem()) {
			sessionService.initializeProxy();

			HttpPost httppost = new HttpPost(sessionHost);
			httppost.addHeader("Authorization", "Bearer " + APIKey);
			httppost.addHeader("Accept", "application/json");
			httppost.addHeader("Content-Type", "application/json");

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("pipeline_id", pipelineId);
			String jsonString = jsonObject.toString();

			httppost.setEntity(new StringEntity(jsonString));

			try (CloseableHttpResponse response = httpclient.execute(httppost)) {
				HttpEntity entity = response.getEntity();
				int statusCode = response.getStatusLine().getStatusCode();

				if (statusCode != 200 && statusCode != 201) return null;
				String responseString = EntityUtils.toString(entity);
				JSONObject responseObject = new JSONObject(responseString);

				return responseObject.getString("search_session_id");
			}

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return null;
	}

	private String callRAG(String token, String query, String sessionId) {
		try (CloseableHttpClient httpclient = HttpClients.createSystem()) {
			sessionService.initializeProxy();

			HttpPost httppost = new HttpPost(ragHost);

			httppost.addHeader("Authorization", "Bearer " + APIKey);
			httppost.addHeader("Content-Type", "application/json");
			httppost.addHeader("Accept", "application/json");

			JSONObject json  = new JSONObject();
			JSONArray queries = new JSONArray();
			queries.put(query);
			json.put("search_session_id", sessionId);
			json.put("queries", queries);
			JSONObject params  = new JSONObject();
			JSONObject rag = new JSONObject();
			JSONObject ragParams  = new JSONObject();
			JSONObject userProfileAPIComponent = new JSONObject();
			userProfileAPIComponent.put("user_token", token);
			ragParams.put("UserProfileAPIComponent", userProfileAPIComponent);
			JSONObject multiOpenSearchHybridRetriever = new JSONObject();
			JSONArray activeStores = new JSONArray();
			activeStores.put("DIGIT_A4_EUS");
			multiOpenSearchHybridRetriever.put("active_stores", activeStores);
			multiOpenSearchHybridRetriever.put("top_k", 5);
			ragParams.put("MultiOpenSearchHybridRetriever", multiOpenSearchHybridRetriever);
			JSONObject ranker = new JSONObject();
			ranker.put("top_k", 5);
			ragParams.put("ranker", ranker);
			rag.put("params", ragParams);
			params.put("RAG@ECPipelineCaller", rag);
			json.put("params", params);

			String jsons = json.toString();

			httppost.setEntity(new StringEntity(jsons));

			try (CloseableHttpResponse response = httpclient.execute(httppost)) {
				HttpEntity entity = response.getEntity();
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != 200 && statusCode != 202) return null;
				String responseString = EntityUtils.toString(entity);
				JSONObject responseObject = new JSONObject(responseString);
				JSONArray resultsArray = responseObject.getJSONArray("results");
				JSONObject firstResultObject = resultsArray.getJSONObject(0);
				JSONArray answersArray = firstResultObject.getJSONArray("answers");
				JSONObject firstAnswerObject = answersArray.getJSONObject(0);
				return firstAnswerObject.getString("answer");
			}

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		return null;
	}

}
