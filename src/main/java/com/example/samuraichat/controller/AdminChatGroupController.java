package com.example.samuraichat.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	public String index(@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, Model model) {
		Page<ChatGroup> chatGroupPage = chatGroupService.findAllChatGroups(pageable);
		
		model.addAttribute("chatGroupPage", chatGroupPage);
		
		return "admin/chatGroups/index";
	}
	
	@PostMapping("/{id}/delete")
	public String delete(@PathVariable(name = "id") Integer groupId, RedirectAttributes redirectAttributes) {
		Optional<ChatGroup> optionalChatGroup = chatGroupService.findById(groupId);
		
		if(optionalChatGroup.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "チャットグループが存在しません。");
			
			return "redirect:/admin/chatGroups";
		}
		
		ChatGroup chatGroup = optionalChatGroup.get();
		chatGroupService.delete(chatGroup);
		redirectAttributes.addFlashAttribute("successMessage", "民宿を削除しました。");
		
		return "redirect:/admin/chatGroups";
	}

}
