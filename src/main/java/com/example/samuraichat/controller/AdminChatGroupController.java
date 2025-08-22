package com.example.samuraichat.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.samuraichat.entity.ChatGroup;
import com.example.samuraichat.service.ChatGroupService;

@Controller
@RequestMapping("/admin/chatGroups")
public class AdminChatGroupController {
	private final ChatGroupService chatGroupService;
	
	public AdminChatGroupController(ChatGroupService chatGroupService) {
		this.chatGroupService = chatGroupService;
	}
	
	@GetMapping
	public String index(Model model) {
		List<ChatGroup> chatGroups = chatGroupService.findAllChatGroups();
		
		model.addAttribute("chatGroups", chatGroups);
		
		return "admin/chatGroups/index";
	}

}
