package com.example.samuraichat.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    @Autowired
    private CustomOidcUserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers("/css/**", "/images/**", "/js/**", "/storage/**", "/signup/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/messages/**").hasAnyRole("ADMIN", "GENERAL")
                .anyRequest().authenticated()
            )

            // ★ Google OAuth ログイン設定（formLogin の外）
            .oauth2Login(oauth -> oauth
            	    .loginPage("/login")
            	    .successHandler(loginSuccessHandler)
            	    .userInfoEndpoint(userInfo -> userInfo
            	        .oidcUserService(customOAuth2UserService)  // ← OIDC 用
            	      
            	    )
            	)


            // ★ フォームログイン設定（oauth2Login の外）
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(loginSuccessHandler)
                .loginProcessingUrl("/login")
                .failureUrl("/login?error")
                .permitAll()
            )

            .logout(logout -> logout
            		.logoutSuccessUrl("/logout-switch")
            	    .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}