package com.example.samuraichat.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraichat.entity.ChatGroup;
import com.example.samuraichat.form.ChatRegisterForm;
import com.example.samuraichat.service.ChatGroupService;

@Controller
@RequestMapping("/groups")
public class ChatGroupController {
	private final ChatGroupService chatGroupService;
	
	public ChatGroupController(ChatGroupService chatGroupService) {
		this.chatGroupService = chatGroupService;
	}
	
	@GetMapping
	public String index(@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, Model model) {
		Page<ChatGroup> chatGroupPage = chatGroupService.findAllChatGroups(pageable);
		
		model.addAttribute("chatGroupPage", chatGroupPage);
		
		return "chatGroup/index";
	}
	
	@GetMapping("/register")
	public String register(Model model) {
		model.addAttribute("chatRegisterForm", new ChatRegisterForm());
		return "chatGroup/register";
	}
	
	@PostMapping("/create")
	public String create(@ModelAttribute @Validated ChatRegisterForm chatRegisterForm,
						 BindingResult bindingResult,
						 RedirectAttributes redirectAttributes,
						 Model model)
	{
		if (bindingResult.hasErrors()) {
			model.addAttribute("chatRegisterForm", chatRegisterForm);
			
			return "chatGroup/register";
		}
		
		chatGroupService.create(chatRegisterForm);
		redirectAttributes.addFlashAttribute("successMessage", "新しいチャットグループを登録しました。");
		
		return "redirect:/groups";
	}
}
