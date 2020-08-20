package com.ec.survey.security;

import com.ec.survey.controller.ManagementController;
import com.ec.survey.model.administration.User;
import com.ec.survey.tools.Constants;

import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class SavedRequestAwareAuthenticationSuccessHandlerExtended extends SimpleUrlAuthenticationSuccessHandler {
	protected static final Logger logger = Logger.getLogger(ManagementController.class);

	private RequestCache requestCache = new HttpSessionRequestCache();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		SavedRequest savedRequest = requestCache.getRequest(request, response);

		String survey = request.getParameter(Constants.SURVEY);

		if (survey != null) {
			getRedirectStrategy().sendRedirect(request, response, "/runner/" + survey);
			return;
		}

		User user = (User) request.getSession().getAttribute("USER");

		if (!user.isAgreedToPS()) {
			getRedirectStrategy().sendRedirect(request, response, "/auth/ps");
			return;
		}

		if (!user.isAgreedToToS()) {
			getRedirectStrategy().sendRedirect(request, response, "/auth/tos");
			return;
		}

		if (user.isDeleted()) {
			getRedirectStrategy().sendRedirect(request, response, "/auth/deleted");
			return;
		}

		if (savedRequest == null) {
			super.onAuthenticationSuccess(request, response, authentication);

			return;
		}
		String targetUrlParameter = getTargetUrlParameter();
		if (isAlwaysUseDefaultTargetUrl()
				|| (targetUrlParameter != null && StringUtils.hasText(request.getParameter(targetUrlParameter)))) {
			requestCache.removeRequest(request, response);
			super.onAuthenticationSuccess(request, response, authentication);

			return;
		}

		clearAuthenticationAttributes(request);

		// Use the DefaultSavedRequest URL
		String targetUrl = savedRequest.getRedirectUrl();

		if (!savedRequest.getMethod().equalsIgnoreCase("GET") || isAjax(targetUrl, savedRequest)) {
			targetUrl = "/dashboard";
		}

		// check if url is valid
		if (targetUrl.endsWith("addUser")) {
			targetUrl = "/dashboard";
		}

		getRedirectStrategy().sendRedirect(request, response, targetUrl);

	}

	private boolean isAjax(String url, SavedRequest request) {
		if (url != null && (url.contains("checkNew") || url.contains("/messages") || url.contains("/dashboard/")
				|| url.toLowerCase().contains("json") || url.toLowerCase().contains("ajax"))) {
			return true;
		}

		List<String> requestedWithHeader = request.getHeaderValues("X-Requested-With");
		return requestedWithHeader != null && requestedWithHeader.contains("XMLHttpRequest");
	}

	public void setRequestCache(RequestCache requestCache) {
		this.requestCache = requestCache;
	}

	public RequestCache getRequestCache() {
		return requestCache;
	}
}
