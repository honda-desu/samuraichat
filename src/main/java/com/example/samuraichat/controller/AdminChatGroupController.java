package com.example.samuraichat.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	public String index(Pageable pageable, Model model) {
		Page<ChatGroup> chatGroupPage = chatGroupService.findAllChatGroups(pageable);
		
		model.addAttribute("chatGroupPage", chatGroupPage);
		
		return "admin/chatGroups/index";
	}

}
