package com.ec.survey.service;

import com.ec.survey.exception.MessageException;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Service("microsoftTranslationService")
public class MicrosoftTranslationService extends BasicService {

	public @Value("${microsoft.translation.client.id}") String clientID;
	public @Value("${microsoft.translation.client.secret}") String clientSecret;

	@PostConstruct
	private void init() {
		Translate.setClientId(clientID);
		Translate.setClientSecret(clientSecret);
	}

	public String[] translate(String[] sourceTexts, String sourceLanguage, String targetLangauge) throws Exception {
		sessionService.initializeProxy();

		// not all supported languages are available Language enum
		// when implement
		// https://github.com/boatmeme/microsoft-translator-java-api/issues/30
		// better to use strings and to verify is language is supported
		// Language.getLanguageCodesForTranslation()

		Language source = Language.fromString(sourceLanguage.toLowerCase());
		Language target = Language.fromString(targetLangauge.toLowerCase());
		if (source == null || target == null) {
			throw new Exception("Not supported languages " + sourceLanguage + " or " + targetLangauge);
		}
		try {
			return Translate.execute(sourceTexts, source, target);
		} catch (Exception e) {
			throw new MessageException("Error during translation source lang " + sourceLanguage + " target lang "
					+ targetLangauge + " text to translate " + Arrays.toString(sourceTexts));
		}

	}
}
