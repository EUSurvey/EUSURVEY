package com.ec.survey.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ec.survey.service.SessionService;
import com.ec.survey.tools.Captcha;

@Controller
public class CaptchaController extends BasicController {

	public static final String CAPTCHA_IMAGE_FORMAT = "jpeg";

	@Autowired
	private SessionService sessionService;

	@RequestMapping(value = "/captcha.html")
	public void showForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
		byte[] captchaChallengeAsJpeg = null;
		// the output stream to render the captcha image as jpeg into
		ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
		try {
			// get the session id that will identify the generated captcha.
			// the same id must be used to validate the response, the session id is a good
			// candidate!
			String captchaText = Captcha.generateText();
			this.sessionService.setCaptchaText(request, captchaText);

			byte[] imageBytes = Captcha.generateImage(captchaText);

			InputStream is = new ByteArrayInputStream(imageBytes);
        	BufferedImage newBi = ImageIO.read(is);

			ImageIO.write(newBi, CAPTCHA_IMAGE_FORMAT, jpegOutputStream);
		} catch (IllegalArgumentException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		captchaChallengeAsJpeg = jpegOutputStream.toByteArray();

		// flush it in the response
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/" + CAPTCHA_IMAGE_FORMAT);

		ServletOutputStream responseOutputStream = response.getOutputStream();
		responseOutputStream.write(captchaChallengeAsJpeg);
		responseOutputStream.flush();
		responseOutputStream.close();
	}

}
