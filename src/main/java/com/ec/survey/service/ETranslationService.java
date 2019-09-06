package com.ec.survey.service;

import com.ec.survey.model.machinetranslation.RequestTranslationMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("eTranslationService")
public class ETranslationService {

  private @Value("${mt.rest.url}")
  String url;
  private @Value("${mt.rest.username}")
  String username;
  private @Value("${mt.rest.password}")
  String password;

  private @Value("${mt.applicationname}")
  String applicationName;
  private @Value("${server.mt.response.callback}")
  String serverResponseCallBack;

  private static final String REQUEST_ID = "?requestId=";
  private static final String NOTIFY_SUCCESS_PATH = "home/notifySuccess";
  private static final String NOTIFY_ERROR_PATH = "home/notifyError";

  private static final Log logger = LogFactory
      .getLog(ETranslationService.class);


  private static String createTranslationRequest(String applicationName, String departmentNumber,
      String documentToTranslate, String errorCallback, String externalReference,
      String institution, int priority,
      String requesterCallback, String sourceLanguage, String targetLanguage,
      String targetTranslationPath, String textToTranslate, String username)
      throws JSONException {

    String[] targetLanguages = targetLanguage.split(",");
    JSONArray targetLanguagesJSON = new JSONArray();
    for (int i = 0; i < targetLanguages.length; i++) {
      targetLanguagesJSON.put(i, targetLanguages[i]);
    }
    if (documentToTranslate.equals("")) {
      return new JSONObject().put("priority", priority)
          .put("externalReference", externalReference)
          .put("callerInformation", new JSONObject()
              .put("application", applicationName)
              .put("username", username)
              .put("institution", institution))
          .put("textToTranslate", textToTranslate)
          .put("sourceLanguage", sourceLanguage)
          .put("targetLanguages", targetLanguagesJSON)
          .put("domain", "SPD")
          .put("requesterCallback", requesterCallback)
          .put("errorCallback", errorCallback)
          .toString();
    } else {
      return new JSONObject().put("priority", priority)
          .put("externalReference", externalReference)
          .put("callerInformation", new JSONObject()
              .put("application", applicationName)
              .put("username", username)
              .put("institution", institution))
          .put("documentToTranslatePath", documentToTranslate)
          .put("sourceLanguage", sourceLanguage)
          .put("targetLanguages", targetLanguagesJSON)
          .put("domain", "SPD")
          .put("destinations", new JSONObject()
              .put("ftpDestinations", new JSONArray().put(0, targetTranslationPath)))
          .put("requesterCallback", requesterCallback)
          .put("errorCallback", errorCallback)
          .toString();
    }

  }

  public boolean sendMessage(RequestTranslationMessage rtm) throws Exception {
    boolean result = true;
    try {
      String errorCallback =
          serverResponseCallBack + NOTIFY_ERROR_PATH + REQUEST_ID + rtm.getExternalReference();
      String requesterCallback =
          serverResponseCallBack + NOTIFY_SUCCESS_PATH + REQUEST_ID + rtm.getExternalReference();
      sendMessage(applicationName, rtm.getDepartmentNumber(),
          rtm.getDocumentToTranslate(),
          errorCallback, rtm.getExternalReference(), rtm.getInstitution(),
          rtm.getPriority(),
          requesterCallback,
          rtm.getSourceLanguage(), rtm.getTargetLanguage(),
          rtm.getTargetTranslationPath(), rtm.getTextToTranslate(), rtm.getUsername()
      );
    } catch (Exception e) {
      result = false;
      throw new Exception("Error when sending message to translation service", e);

    }
    return result;
  }

  private void sendMessage(String applicationName, String departmentNumber,
      String documentToTranslate, String errorCallback, String externalReference,
      String institution, int priority,
      String requesterCallback, String sourceLanguage, String targetLanguage,
      String targetTranslationPath, String textToTranslate, String username) {

    try {
      DefaultHttpClient client = new DefaultHttpClient();
      client.getCredentialsProvider().setCredentials(AuthScope.ANY, new
          UsernamePasswordCredentials(this.username, this.password));
      HttpPost post = new HttpPost(this.url);
      post.setHeader("Accept", "application/json");
      post.setHeader("Content-type", "application/json");
      String json = createTranslationRequest(applicationName, departmentNumber, documentToTranslate,
          errorCallback,
          externalReference, institution, priority, requesterCallback, sourceLanguage,
          targetLanguage, targetTranslationPath,
          textToTranslate, username);
      if (logger.isInfoEnabled()) {
        logger.info("json:" + json);
        logger.info("url" + this.url);
      }
      post.setEntity(new StringEntity(json, "UTF-8"));
      HttpClientParams.setRedirecting(post.getParams(), false);
      HttpResponse response = client.execute(post);
      if (logger.isInfoEnabled()) {
        logger.info("response status line " + response.getStatusLine().toString());
        logger.info("response status code " + response.getStatusLine().getStatusCode());
      }
    } catch (Exception e) {
      if (logger.isErrorEnabled()) {
        logger.error(
            "Error when sending message to translation service", e);
      }
      throw new RuntimeException("Error when call machine translation web service");
    }
  }

}

