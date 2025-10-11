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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	
//	@GetMapping("/messages")
//	public String showAllMessages(Model model) {
//		List<Message> messages = messageService.findAllMessages();
//		
//		model.addAttribute("messages", messages);
//		
//		return "message/index";
//	}
	
	@PostMapping("/groups/{groupId}/messages/send")
	public String sendMessage(@PathVariable("groupId") Integer groupId,
	                          @RequestParam("content") String content,
	                          @AuthenticationPrincipal UserDetailsImpl userDetails) {

	    User user = userDetails.getUser();

	    // グループIDに紐づくGroupエンティティを取得（存在チェック含む）
	    ChatGroup group = chatGroupService.findById(groupId)
	        .orElseThrow(() -> new IllegalArgumentException("指定されたグループが存在しません"));

	    // メッセージ保存（グループとユーザーを紐づけて）
	    messageService.saveTextMessage(content, user, group);

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
		model.addAttribute("chatGroup", chatGroup);
		
		return "message/list";
	}
	
	@PostMapping("/groups/{groupId}/images/upload")
	public String uploadImage(@PathVariable Integer groupId,
	                          @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
	                          @RequestParam("imageFile") MultipartFile imageFile,
	                          RedirectAttributes redirectAttributes) {

	    if (imageFile.isEmpty()) {
	        redirectAttributes.addFlashAttribute("error", "画像ファイルが選択されていません。");
	        return "redirect:/groups/" + groupId + "/messages";
	    }

	    User user = userDetailsImpl.getUser();
	    ChatGroup group = chatGroupService.findById(groupId)
	        .orElseThrow(() -> new IllegalArgumentException("指定されたグループが存在しません"));

	    try {
	        // ファイル名生成（UUID + 元ファイル名）
	        String fileName = java.util.UUID.randomUUID() + "_" + imageFile.getOriginalFilename();

	     // 保存先パス（プロジェクトルート/uploads）
	        String uploadRoot = System.getProperty("user.dir") + "/uploads";
	        java.nio.file.Path uploadDir = java.nio.file.Paths.get(uploadRoot);
	        java.nio.file.Files.createDirectories(uploadDir); // ディレクトリがなければ作成

	        java.nio.file.Path filePath = uploadDir.resolve(fileName);
	        imageFile.transferTo(filePath.toFile());

	        // メッセージとして保存
	        String imagePath = "/uploads/" + fileName;
	        messageService.saveImageMessage(imagePath, user, group);

	    } catch (Exception e) {
	        e.printStackTrace();
	        redirectAttributes.addFlashAttribute("error", "画像のアップロードに失敗しました。");
	    }

	    return "redirect:/groups/" + groupId + "/messages";
	}

}
