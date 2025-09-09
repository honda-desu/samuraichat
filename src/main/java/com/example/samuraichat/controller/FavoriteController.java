package com.example.samuraichat.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraichat.entity.ChatGroup;
import com.example.samuraichat.entity.Favorite;
import com.example.samuraichat.entity.User;
import com.example.samuraichat.security.UserDetailsImpl;
import com.example.samuraichat.service.ChatGroupService;
import com.example.samuraichat.service.FavoriteService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class FavoriteController {
	private final ChatGroupService chatGroupService;
	private final FavoriteService favoriteService;
	
	public FavoriteController(ChatGroupService chatGroupService, FavoriteService favoriteService) {
		this.chatGroupService = chatGroupService;
		this.favoriteService = favoriteService;
	}
	
	@GetMapping("/favorites")
	public String index(@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			            RedirectAttributes redirectAttributes,
			            Model model)
	{
		User user = userDetailsImpl.getUser();
		
		Page<Favorite> favoritePage = favoriteService.findFavoritesByUserOrderByCreatedAtDesc(user, pageable);
		
		model.addAttribute("favoritePage", favoritePage);
		
		return "favorites/index";
	}
	
	@PostMapping("/groups/{groupId}/favorites/create")
	public String create(@PathVariable(name = "groupId") Integer groupId,
			             @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			             RedirectAttributes redirectAttributes,
			             Model model)
	{
		User user = userDetailsImpl.getUser();
		
		Optional<ChatGroup> optionalChatGroup = chatGroupService.findById(groupId);
		
		if (optionalChatGroup.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");
			
			return "redirect:/groups";
		}
		
		ChatGroup chatGroup = optionalChatGroup.get();
		
		favoriteService.createFavorite(chatGroup, user);
		redirectAttributes.addFlashAttribute("successMessage","お気に入りに追加しました。");
		
		return "redirect:/groups/{groupId}/messages";
	}
	
	@PostMapping("/favorites/{favoriteId}/delete")
	public String delete(@PathVariable(name = "favoriteId") Integer favoriteId,
			             @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			             RedirectAttributes redirectAttributes,
			             HttpServletRequest httpServletRequest)
	{
		User user = userDetailsImpl.getUser();
		
		Optional<Favorite> optionalFavorite = favoriteService.findFavoriteById(favoriteId);
		String referer = httpServletRequest.getHeader("Referer");
		
		if (optionalFavorite.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "お気に入りが存在しません。");
			
			return "redirect:" + (referer != null ? referer : "/favorites");
		}
		
		Favorite favorite = optionalFavorite.get();
		
		if (!favorite.getUser().getId().equals(user.getId())) {
	           redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");

	           return "redirect:" + (referer != null ? referer : "/favorites");
	       }
		
		favoriteService.deleteFavorite(favorite);
		redirectAttributes.addFlashAttribute("successMessage", "お気に入りを解除しました。");
		
		return "redirect:" + (referer != null ? referer : "/favorites");
	}

}
