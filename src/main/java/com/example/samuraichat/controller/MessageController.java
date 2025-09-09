package com.example.samuraichat.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.samuraichat.entity.ChatGroup;
import com.example.samuraichat.entity.Favorite;
import com.example.samuraichat.entity.Message;
import com.example.samuraichat.entity.User;
import com.example.samuraichat.security.UserDetailsImpl;
import com.example.samuraichat.service.ChatGroupService;
import com.example.samuraichat.service.FavoriteService;
import com.example.samuraichat.service.MessageService;

@Controller
public class MessageController {
	private final MessageService messageService;
	private final ChatGroupService chatGroupService;
	private final FavoriteService favoriteService;
	
	public MessageController(MessageService messageService, ChatGroupService chatGroupService, FavoriteService favoriteService) {
		this.messageService = messageService;
		this.chatGroupService = chatGroupService;
		this.favoriteService = favoriteService;
	}
	
	@GetMapping("/messages")
	public String showAllMessages(Model model) {
		List<Message> messages = messageService.findAllMessages();
		
		model.addAttribute("messages", messages);
		
		return "message/index";
	}
	
	@PostMapping("/groups/{groupId}/messages/send")
	public String sendMessage(@PathVariable("groupId") Integer groupId,
	                          @RequestParam("content") String content,
	                          @AuthenticationPrincipal UserDetailsImpl userDetails) {

	    User user = userDetails.getUser();

	    // グループIDに紐づくGroupエンティティを取得（存在チェック含む）
	    ChatGroup group = chatGroupService.findById(groupId)
	        .orElseThrow(() -> new IllegalArgumentException("指定されたグループが存在しません"));

	    // メッセージ保存（グループとユーザーを紐づけて）
	    messageService.saveMessage(content, user, group);

	    // グループのメッセージ一覧にリダイレクト
	    return "redirect:/groups/" + groupId + "/messages";
	}

	
	@GetMapping("/groups/{groupId}/messages")
	public String showMessages(@PathVariable Integer groupId,
			                   @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			                   Model model) {
		
		List<Message> messages = messageService.getMessagesByGroupId(groupId);
		Optional<ChatGroup> optionalChatGroup = chatGroupService.findById(groupId);
		
		ChatGroup chatGroup = optionalChatGroup.get();
		Favorite favorite = null;
		boolean isFavorite = false;
		
		if (userDetailsImpl != null) {
			User user = userDetailsImpl.getUser();
			isFavorite = favoriteService.isFavorite(chatGroup, user);
			
			if(isFavorite) {
				favorite = favoriteService.findFavoriteByChatGroupAndUser(chatGroup, user);
			}
			
		}
		
		model.addAttribute("messages", messages);
		model.addAttribute("groupId", groupId);
		model.addAttribute("favorite", favorite);
		model.addAttribute("isFavorite", isFavorite);
		
		return "message/list";
	}

}
