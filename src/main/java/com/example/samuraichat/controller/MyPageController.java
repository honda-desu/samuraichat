package com.example.samuraichat.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.samuraichat.entity.User;
import com.example.samuraichat.service.UserService;

@Controller
public class MyPageController {
	
	private final UserService userService;
	
	public MyPageController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping("/mypage")
	public String myPage(Model model, Principal principal) {
		// ログイン中のユーザーのメールアドレスを取得
		String email = principal.getName();
		
		//ユーザー情報を取得
		User user = userService.findByEmail(email);
		
		model.addAttribute("user", user);
		return "myPage/mypage";
	}

}
