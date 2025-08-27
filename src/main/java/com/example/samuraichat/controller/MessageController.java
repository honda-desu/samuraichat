package com.example.samuraichat.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.samuraichat.entity.Message;
import com.example.samuraichat.entity.User;
import com.example.samuraichat.security.UserDetailsImpl;
import com.example.samuraichat.service.MessageService;

@Controller
@RequestMapping("/messages")
public class MessageController {
	private final MessageService messageService;
	
	public MessageController(MessageService messageService) {
		this.messageService = messageService;
	}
	
	@GetMapping
	public String showAllMessages(Model model) {
		List<Message> messages = messageService.findAllMessages();
		
		model.addAttribute("messages", messages);
		
		return "message/index";
	}
	
	@PostMapping("/send")
	public String sendMessage(@RequestParam("content") String content,
	                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
		
	    User user = userDetails.getUser();
	    
	    messageService.saveMessage(content, user);
	    
	    return "redirect:/messages";
	}
}
