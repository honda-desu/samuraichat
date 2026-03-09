package com.example.samuraichat.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.samuraichat.entity.ChatGroup;
import com.example.samuraichat.entity.DmRoom;
import com.example.samuraichat.entity.User;
import com.example.samuraichat.security.CustomOAuth2User;
import com.example.samuraichat.security.UserDetailsImpl;
import com.example.samuraichat.service.ChatGroupService;
import com.example.samuraichat.service.DmService;
import com.example.samuraichat.service.FavoriteService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	
	private final DmService dmService;
	private final ChatGroupService chatGroupService;
	private final FavoriteService favoriteService;
	
	public HomeController(DmService dmService, ChatGroupService chatGroupService, FavoriteService favoriteService) {
		this.dmService = dmService;
		this.chatGroupService = chatGroupService;
		this.favoriteService = favoriteService;
	}
	
	// ★ principal から User を取り出す共通メソッド
    private User extractUser(Object principal) {
        if (principal instanceof UserDetailsImpl userDetails) {
            return userDetails.getUser();
        }
        if (principal instanceof CustomOAuth2User oauthUser) {
            return oauthUser.getUser();
        }
        throw new IllegalStateException("Unknown principal type: " + principal.getClass());
    }
    
	@GetMapping("/")
    public String home(@AuthenticationPrincipal Object principal, HttpSession session, Model model) {
		
		System.out.println("principal class = " + (principal == null ? "null" : principal.getClass()));
		
		if (principal == null) {
	        return "redirect:/login";
	    }


        User me = extractUser(principal);
        Long myId = me.getId();
        
        List<DmRoom> recent = dmService.getRecentRoomsForUser(myId, 3);
        
     // ★ ここで中身を確認する
        System.out.println("=== Home画面の recentDms ===");
        System.out.println(recent);

        
        session.setAttribute("userId", myId); 

        // 最新3件だけ
        model.addAttribute("recentDms", dmService.getRecentRoomsForUser(myId, 3));
        
        // ★ グループ 最新3件
        List<ChatGroup> recentGroups = chatGroupService.findRecentGroups(3);
        model.addAttribute("recentGroups", recentGroups);
        
     // ★ お気に入りグループ 最新3件
        model.addAttribute("recentFavoriteGroups",
                favoriteService.findRecentFavoriteGroups(me, 3));



        return "index";
    }


}
