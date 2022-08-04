package com.tweetapp.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.GenericFilterBean;

import com.tweetapp.service.TokenService;

/**
 * 
 * JWT Filter class Filters the  Servlet Requests by authentication
 * 
 * @author MADHU
 *
 */
@Configuration
public class JWTFilter extends GenericFilterBean {

	private static Logger log = LogManager.getLogger(JWTFilter.class);

	private TokenService tokenService;

	JWTFilter() {
		this.tokenService = new TokenService();
	}

	public boolean allowRequestWithoutToken(HttpServletRequest request) {

		if (request.getRequestURI().contains("/login") || request.getRequestURI().contains("/register")
				|| request.getRequestURI().contains("/swagger-ui.html/")) {

			return true;
		}
		return false;
	}

	@Override

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
			throws IOException, ServletException {
		log.info("Filtering using DOfilter Method");

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Expose-Headers", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET,POST,PATCH,DELETE,PUT,OPTIONS");
		response.setHeader("Access-Control-Allow-Headers", "*");
		response.setHeader("Access-Control-Max-Age", "86400");
		String token = request.getHeader("Authorization");

		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			response.sendError(HttpServletResponse.SC_OK, "success");
			return;
		}

		if (allowRequestWithoutToken(request)) {
			response.setStatus(HttpServletResponse.SC_OK);

			filterChain.doFilter(req, res);
		} else {
			if (token == null || !tokenService.isTokenValid(token)) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			} else {
				ObjectId userId = new ObjectId(tokenService.getUserIdFromToken(token));
				request.setAttribute("userId", userId);
				filterChain.doFilter(req, res);

			}
		}

	}

}