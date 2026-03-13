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
import com.example.samuraichat.security.CustomOAuth2User;
import com.example.samuraichat.security.UserDetailsImpl;
import com.example.samuraichat.service.ChatGroupService;
import com.example.samuraichat.service.FavoriteService;
import com.example.samuraichat.service.MessageService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class FavoriteController {

    private final ChatGroupService chatGroupService;
    private final FavoriteService favoriteService;
    private final MessageService messageService;

    public FavoriteController(ChatGroupService chatGroupService, FavoriteService favoriteService, MessageService messageService) {
        this.chatGroupService = chatGroupService;
        this.favoriteService = favoriteService;
        this.messageService = messageService;
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

    @GetMapping("/favorites")
    public String index(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
            @AuthenticationPrincipal Object principal,
            RedirectAttributes redirectAttributes,
            Model model) {

        User user = extractUser(principal);
        Long myId = user.getId();

        Page<Favorite> favoritePage = favoriteService.findFavoritesByUserOrderByCreatedAtDesc(user, pageable);

        // ★★★ 各お気に入りの ChatGroup に未読数をセット（重要）★★★
        for (Favorite favorite : favoritePage.getContent()) {
            ChatGroup group = favorite.getChatGroup();
            int unread = messageService.getUnreadCount(group.getId(), myId);
            group.setUnreadCount(unread);
        }

        model.addAttribute("favoritePage", favoritePage);

        return "favorites/index";
    }

    @PostMapping("/groups/{groupId}/favorites/create")
    public String create(
            @PathVariable(name = "groupId") Long groupId,
            @AuthenticationPrincipal Object principal,
            RedirectAttributes redirectAttributes,
            Model model) {

        User user = extractUser(principal);

        Optional<ChatGroup> optionalChatGroup = chatGroupService.findById(groupId);

        if (optionalChatGroup.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");
            return "redirect:/groups";
        }

        ChatGroup chatGroup = optionalChatGroup.get();

        favoriteService.createFavorite(chatGroup, user);
        redirectAttributes.addFlashAttribute("successMessage", "お気に入りに追加しました。");

        return "redirect:/groups/{groupId}/messages";
    }

    @PostMapping("/favorites/{favoriteId}/delete")
    public String delete(
            @PathVariable(name = "favoriteId") Integer favoriteId,
            @AuthenticationPrincipal Object principal,
            RedirectAttributes redirectAttributes,
            HttpServletRequest httpServletRequest) {

        User user = extractUser(principal);

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