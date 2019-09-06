package com.ec.survey.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import org.apache.log4j.Logger;
import org.springframework.web.filter.OncePerRequestFilter;
 
/**
 * A custom filter that denies access if the given username is equal to
 * <b>mike</b>. This filter extends the {@link OncePerRequestFilter} to
 * guarantee that this filter is executed just once.
 * <p>
 
 * When the user enters this filter, he is already authenticated. This
 * filters acts like an intercept-url where you can customize access levels
 * per user
 *
 */
public class BlacklistFilter extends OncePerRequestFilter {
 
 protected static Logger logger = Logger.getLogger("filter");
  
 @Override
 protected void doFilterInternal(HttpServletRequest request,
   HttpServletResponse response, FilterChain filterChain)
   throws ServletException, IOException {
   
        // User details are not empty
        logger.debug("Continue with remaining filters");
        filterChain.doFilter(request, response);
 }
 
}