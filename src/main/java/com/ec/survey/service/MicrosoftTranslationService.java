package com.ec.survey.service;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;

@Service("microsoftTranslationService")
public class MicrosoftTranslationService extends BasicService {

	@Resource(name = "sessionService")
	private SessionService sessionService;

	public @Value("${microsoft.translation.client.id}") String clientID;
	public @Value("${microsoft.translation.client.secret}") String clientSecret;

	@PostConstruct
	private void init() {
		Translate.setClientId(clientID);
		Translate.setClientSecret(clientSecret);
	}

	private static final String[] EMPTY_ARRAY = new String[0];

	public String[] translate(String[] sourceTexts, String sourceLanguage, String targetLangauge) {
		sessionService.initializeProxy();

		// not all supported languages are available Language enum
		// when implement
		// https://github.com/boatmeme/microsoft-translator-java-api/issues/30
		// better to use strings and to verify is language is supported
		// Language.getLanguageCodesForTranslation()

		Language source = Language.fromString(sourceLanguage.toLowerCase());
		Language target = Language.fromString(targetLangauge.toLowerCase());
		if (source == null || target == null) {
			logger.error("Not supported languages " + sourceLanguage + " or " + targetLangauge);
			return EMPTY_ARRAY;
		}
		try {
			return Translate.execute(sourceTexts, source, target);
		} catch (Exception e) {
			logger.error("Error during translation source lang " + sourceLanguage + " target lang " + targetLangauge + " text to translate " + Arrays.toString(sourceTexts), e);
			logger.error("Error during bulk translation try one by one");
			String currentText = "";
			String[] result = new String[sourceTexts.length];
			// try one by one
			for (int i = 0; i < sourceTexts.length; i++) {
				currentText = sourceTexts[i];
				try {
					result[i] = Translate.execute(currentText, source, target);
				} catch (Exception ex) {
					logger.error("Error during translation source lang " + sourceLanguage + " target lang " + targetLangauge + " text to translate " + currentText, ex);
				}
			}
			return result;
		}

	}
}
