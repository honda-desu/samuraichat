package com.example.samuraichat.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.samuraichat.entity.User;
import com.example.samuraichat.service.UserService;

@Controller
public class MyPageController {
	
	private final UserService userService;
	
	public MyPageController(UserService userService) {
		this.userService = userService;
	}
	
	//マイページを表示する
	@GetMapping("/mypage")
	public String myPage(Model model, Principal principal) {
		// ログイン中のユーザーのメールアドレスを取得
		String email = principal.getName();
		
		//ユーザー情報を取得
		User user = userService.findByEmail(email);
		
		model.addAttribute("user", user);
		return "myPage/mypage";
	}
	
	//マイページを編集する
	@GetMapping("/mypage/edit")
	public String editPage(Model model, Principal principal) {
		String email = principal.getName();
		User user = userService.findByEmail(email);
		
		model.addAttribute("user", user);
		return "myPage/edit";
	}
	
	@PostMapping("/mypage/edit")
	public String updateProfile(
			@RequestParam String name,
			@RequestParam String furigana,
			@RequestParam String profileText,
			@RequestParam("profileImage")MultipartFile profileImage,
			Principal principal) {
		
		String email = principal.getName();
		userService.updateProfile(email, name, furigana, profileText, profileImage);
		
		return "redirect:/mypage";
	}
	
	

}
