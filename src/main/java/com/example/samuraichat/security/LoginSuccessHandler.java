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
    	
    	System.out.println("principal class = " + authentication.getPrincipal().getClass());

        // ★ principal は UserDetailsImpl
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // ★ UserDetailsImpl から User を取り出す
        User user = userDetails.getUser();
        
        System.out.println(">>> LoginSuccessHandler: userId = " + user.getId());


        // ★ セッションに userId を保存
        request.getSession().setAttribute("userId", user.getId());

        super.onAuthenticationSuccess(request, response, authentication);
    }
}