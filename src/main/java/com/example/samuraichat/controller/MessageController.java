package com.example.samuraichat.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
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
import com.example.samuraichat.repository.UserRepository;
import com.example.samuraichat.security.CustomOAuth2User;
import com.example.samuraichat.security.UserDetailsImpl;
import com.example.samuraichat.service.ChatGroupService;
import com.example.samuraichat.service.FavoriteService;
import com.example.samuraichat.service.MessageService;

@Controller
public class MessageController {
	private final MessageService messageService;
	private final ChatGroupService chatGroupService;
	private final FavoriteService favoriteService;
	private final UserRepository userRepository;
	
	private User extractUser(Object principal) {
	    if (principal instanceof UserDetailsImpl userDetails) {
	        return userDetails.getUser();
	    }
	    if (principal instanceof CustomOAuth2User oauthUser) {
	        return oauthUser.getUser();
	    }
	    if (principal instanceof DefaultOidcUser oidcUser) {
	        String email = oidcUser.getEmail(); // または getAttribute("email")
	        User user = userRepository.findByEmail(email);
	        if (user == null) {
	            throw new IllegalStateException("Email に対応する User が存在しません: " + email);
	        }
	        return user;
	    }
	    throw new IllegalStateException("Unknown principal type: " + principal.getClass());
	}
	
	public MessageController(MessageService messageService, ChatGroupService chatGroupService, FavoriteService favoriteService, UserRepository userRepository) {
		this.messageService = messageService;
		this.chatGroupService = chatGroupService;
		this.favoriteService = favoriteService;
		this.userRepository = userRepository;	
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
	public String sendMessage(
	        @PathVariable("groupId") Long groupId,
	        @RequestParam("content") String content,
	        @AuthenticationPrincipal Object principal) {

	    User user = extractUser(principal);

	    ChatGroup group = chatGroupService.findById(groupId)
	            .orElseThrow(() -> new IllegalArgumentException("指定されたグループが存在しません"));

	    messageService.saveTextMessage(content, user, group);

	    return "redirect:/groups/" + groupId + "/messages";
	}

	
	@GetMapping("/groups/{groupId}/messages")
	public String showMessages(
	        @PathVariable Long groupId,
	        @AuthenticationPrincipal Object principal,
	        Model model) {

	    User user = null;
	    if (principal != null) {
	        user = extractUser(principal);
	    }

	    List<Message> messages = messageService.getMessagesByGroupId(groupId);
	    ChatGroup chatGroup = chatGroupService.findById(groupId)
	            .orElseThrow(() -> new IllegalArgumentException("指定されたグループが存在しません"));
	    
	 // ★未読 → 既読処理 ★★★
	    if (user != null) {
	        messageService.markGroupMessagesAsRead(groupId, user.getId());
	    }


	    boolean isFavorite = false;
	    Favorite favorite = null;

	    if (user != null) {
	        isFavorite = favoriteService.isFavorite(chatGroup, user);
	        if (isFavorite) {
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
	public String uploadImage(
	        @PathVariable Long groupId,
	        @AuthenticationPrincipal Object principal,
	        @RequestParam("imageFile") MultipartFile imageFile,
	        RedirectAttributes redirectAttributes) {

	    if (imageFile.isEmpty()) {
	        redirectAttributes.addFlashAttribute("error", "画像ファイルが選択されていません。");
	        return "redirect:/groups/" + groupId + "/messages";
	    }

	    User user = extractUser(principal);

	    ChatGroup group = chatGroupService.findById(groupId)
	            .orElseThrow(() -> new IllegalArgumentException("指定されたグループが存在しません"));

	    try {
	        String fileName = java.util.UUID.randomUUID() + "_" + imageFile.getOriginalFilename();

	        String uploadRoot = System.getProperty("user.dir") + "/uploads";
	        java.nio.file.Path uploadDir = java.nio.file.Paths.get(uploadRoot);
	        java.nio.file.Files.createDirectories(uploadDir);

	        java.nio.file.Path filePath = uploadDir.resolve(fileName);
	        imageFile.transferTo(filePath.toFile());

	        String imagePath = "/uploads/" + fileName;
	        messageService.saveImageMessage(imagePath, user, group);

	    } catch (Exception e) {
	        e.printStackTrace();
	        redirectAttributes.addFlashAttribute("error", "画像のアップロードに失敗しました。");
	    }

	    return "redirect:/groups/" + groupId + "/messages";
	}

}
