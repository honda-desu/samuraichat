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
            // 想定外の principal の場合は "/" に飛ばす
            response.sendRedirect("/");
            return;
        }

        // セッションに userId を保存
        request.getSession().setAttribute("userId", user.getId());

        // ★ SavedRequest を無視して "/" に強制リダイレクト
        response.sendRedirect("/?loggedIn");
    }
}