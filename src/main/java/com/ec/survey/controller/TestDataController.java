package com.ec.survey.controller;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ec.survey.exception.MessageException;
import com.ec.survey.model.administration.User;
import com.ec.survey.tools.Constants;
import com.ec.survey.tools.TestDataGenerator;

@Controller
@Configurable
@RequestMapping("/testdata")
public class TestDataController extends BasicController {

	@Resource(name = "testDataGenerator")
	protected TestDataGenerator testDataGenerator;

	private @Value("${smtpserver}") String smtpServer;
	private @Value("${smtp.port}") String smtpPort;

	@RequestMapping(value = "/survey", method = { RequestMethod.GET, RequestMethod.HEAD })
	@Transactional
	public String survey(@RequestParam("answers") String answers, @RequestParam(Constants.EMAIL) String email, Locale locale,
			ModelMap model, HttpServletRequest request) {
		try {
			User user = sessionService.getCurrentUser(request);
			testDataGenerator.init(user, Integer.parseInt(answers), fileDir, sender, email, null,
					null, false, context, 0, 1, 0);
			getPool().execute(testDataGenerator);

			model.put(Constants.MESSAGE,
					"The generation of a test survey with " + answers
							+ " answer(s) has started. You will receive an email to " + email
							+ " when the operation is completed. This can take a while.");
			return "error/info";
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return "redirect:/errors/500.html";
		}
	}

	@RequestMapping(value = "/survey2", method = { RequestMethod.GET, RequestMethod.HEAD })
	public String survey2(@RequestParam(Constants.SHORTNAME) String shortname, @RequestParam("answers") String answers,
			@RequestParam(Constants.EMAIL) String email, Locale locale, ModelMap model, HttpServletRequest request) {
		try {
			User user = sessionService.getCurrentUser(request);
			testDataGenerator.init(user, Integer.parseInt(answers), fileDir, sender, email,
					shortname, null, false, context, 0, 1, 0);
			getPool().execute(testDataGenerator);

			model.put(Constants.MESSAGE,
					"The generation of a test survey with " + answers
							+ " answer(s) has started. You will receive an email to " + email
							+ " when the operation is completed. This can take a while.");
			return "error/info";
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return "redirect:/errors/500.html";
		}
	}

	@RequestMapping(value = "/survey3", method = { RequestMethod.GET, RequestMethod.HEAD })
	@Transactional
	public String survey3(@RequestParam("answers") String answers, @RequestParam("questions") String questions,
			@RequestParam(Constants.EMAIL) String email, Locale locale, ModelMap model, HttpServletRequest request) {
		try {
			User user = sessionService.getCurrentUser(request);
			testDataGenerator.init(user, Integer.parseInt(answers), fileDir, sender, email, null,
					Integer.parseInt(questions), false, context, 0, 1, 0);
			getPool().execute(testDataGenerator);

			model.put(Constants.MESSAGE,
					"The generation of a test survey with " + questions + " question(s) and " + answers
							+ " answer(s) has started. You will receive an email to " + email
							+ " when the operation is completed. This can take a while.");
			return "error/info";
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return "redirect:/errors/500.html";
		}
	}

	@RequestMapping(value = "/stress", method = { RequestMethod.GET, RequestMethod.HEAD })
	public String stress(@RequestParam(Constants.EMAIL) String email, Locale locale, ModelMap model,
			HttpServletRequest request) {
		try {
			User user = sessionService.getCurrentUser(request);

			// 5000 test surveys are created with #replies(survey(x))=floor(150.000/x -1)
			testDataGenerator.init(user, 10, fileDir, sender, email, null, null, false, context,
					0, 5000, 0);
			getPool().execute(testDataGenerator);

			model.put(Constants.MESSAGE, "The generation of 5000 test surveys has started. You will receive an email to "
					+ email + " when the operation is completed. This can take a while. Seriously.");
			return "error/info";
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return "redirect:/errors/500.html";
		}
	}

	@RequestMapping(value = "/stress2", method = { RequestMethod.GET, RequestMethod.HEAD })
	public String stress2(@RequestParam(Constants.EMAIL) String email, Locale locale, ModelMap model,
			HttpServletRequest request) {
		try {
			User user = sessionService.getCurrentUser(request);

			// 1 test surveys is created with 1000000 replys
			testDataGenerator.init(user, 1000000, fileDir, sender, email, null, null, false,
					context, 0, 1, 0);
			getPool().execute(testDataGenerator);

			model.put(Constants.MESSAGE, "The generation of 1000000 answers has started. You will receive an email to " + email
					+ " when the operation is completed. This can take a while. Seriously.");
			return "error/info";
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return "redirect:/errors/500.html";
		}
	}

	@RequestMapping(value = "/archives", method = { RequestMethod.GET, RequestMethod.HEAD })
	public String archives(@RequestParam(Constants.EMAIL) String email, Locale locale, ModelMap model,
			HttpServletRequest request) {
		try {
			User user = sessionService.getCurrentUser(request);

			for (int i = 1; i <= 1000; i++) {
				if (i == 1000) {
					testDataGenerator.init(user, 1, fileDir, sender, email, null, null, true,
							context, 0, 1, 0);
					getPool().execute(testDataGenerator);
				} else {
					testDataGenerator.init(user, 1, fileDir, sender, null, null, null, true,
							context, 0, 1, 0);
					getPool().execute(testDataGenerator);
				}
			}

			model.put(Constants.MESSAGE, "The generation of 1000 archived surveys has started. You will receive an email to "
					+ email + " when the operation is completed. This can take a while. Seriously.");
			return "error/info";
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return "redirect:/errors/500.html";
		}
	}

	@RequestMapping(value = "/files", method = { RequestMethod.GET, RequestMethod.HEAD })
	public String files(@RequestParam("files") String files, @RequestParam("type") String type,
			@RequestParam(Constants.EMAIL) String email, Locale locale, ModelMap model, HttpServletRequest request) {
		try {
			User user = sessionService.getCurrentUser(request);

			testDataGenerator.init(user, 0, fileDir, sender, email, null, null, true, context,
					Integer.parseInt(files), 1, 0);
			getPool().execute(testDataGenerator);

			model.put(Constants.MESSAGE, "The generation of " + files + " file(s) has started. You will receive an email to "
					+ email + " when the operation is completed. This can take a while. Seriously.");
			return "error/info";
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return "redirect:/errors/500.html";
		}
	}

	@RequestMapping(value = "/contacts", method = { RequestMethod.GET, RequestMethod.HEAD })
	public String contacts(@RequestParam("contacts") String contacts, @RequestParam(Constants.EMAIL) String email,
			Locale locale, ModelMap model, HttpServletRequest request) {
		try {
			User user = sessionService.getCurrentUser(request);

			testDataGenerator.init(user, 0, fileDir, sender, email, null, null, true, context, 0,
					0, Integer.parseInt(contacts));
			getPool().execute(testDataGenerator);

			model.put(Constants.MESSAGE,
					"The generation of " + contacts + " contact(s) has started. You will receive an email to " + email
							+ " when the operation is completed. This can take a while. Seriously.");
			return "error/info";
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return "redirect:/errors/500.html";
		}
	}

	@RequestMapping(value = "/debug", method = { RequestMethod.GET, RequestMethod.HEAD })
	public ModelAndView debug(HttpServletRequest request, HttpServletResponse response) throws MessageException, IOException {
		response.getOutputStream().flush();
		response.getOutputStream().close();

		throw new MessageException(Constants.ERROR);
	}

	private ExecutorService pool;

	public ExecutorService getPool() {
		if (pool == null) {
			pool = Executors.newFixedThreadPool(1, new MyThreadFactory());
		}
		return pool;
	}

	public class MyThreadFactory implements ThreadFactory {
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setPriority(Thread.MIN_PRIORITY);
			return t;
		}
	}

}
