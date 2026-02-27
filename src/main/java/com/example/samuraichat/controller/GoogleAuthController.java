package com.example.samuraichat.controller;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GoogleAuthController {
	
	@GetMapping("/test")
	public String test(OAuth2AuthenticationToken auth) {
		System.out.println("Google Auth Info: " + auth);
		return "test";
		}
	
	

}
