package com.example.samuraichat.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.samuraichat.entity.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
	                                    HttpServletResponse response,
	                                    Authentication authentication)
	                                    throws IOException, ServletException {

	    Object principal = authentication.getPrincipal();
	    User user;

	    if (principal instanceof UserDetailsImpl userDetails) {
	        user = userDetails.getUser();
	    } else if (principal instanceof CustomOAuth2User oauthUser) {
	        user = oauthUser.getUser();
	    } else {
	        super.onAuthenticationSuccess(request, response, authentication);
	        return;
	    }

	    request.getSession().setAttribute("userId", user.getId());

	    super.onAuthenticationSuccess(request, response, authentication);
	}
}