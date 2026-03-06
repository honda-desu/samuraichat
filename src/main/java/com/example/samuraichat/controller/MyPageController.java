package com.example.samuraichat.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.samuraichat.entity.User;
import com.example.samuraichat.security.CustomOAuth2User;
import com.example.samuraichat.security.UserDetailsImpl;
import com.example.samuraichat.service.UserService;

@Controller
public class MyPageController {

    private final UserService userService;

    public MyPageController(UserService userService) {
        this.userService = userService;
    }

    // 共通：principal から User を取り出す
    private User extractUser(Object principal) {
        if (principal instanceof UserDetailsImpl userDetails) {
            return userDetails.getUser();
        }
        if (principal instanceof CustomOAuth2User oauthUser) {
            return oauthUser.getUser();
        }
        throw new IllegalStateException("Unknown principal type: " + principal.getClass());
    }

    // マイページ表示
    @GetMapping("/mypage")
    public String myPage(Model model, @org.springframework.security.core.annotation.AuthenticationPrincipal Object principal) {

        User user = extractUser(principal);
        model.addAttribute("user", user);

        return "myPage/mypage";
    }

    // 編集画面表示
    @GetMapping("/mypage/edit")
    public String editPage(Model model, @org.springframework.security.core.annotation.AuthenticationPrincipal Object principal) {

        User user = extractUser(principal);
        model.addAttribute("user", user);

        return "myPage/edit";
    }

    // プロフィール更新
    @PostMapping("/mypage/edit")
    public String updateProfile(
            @RequestParam String name,
            @RequestParam String furigana,
            @RequestParam String profileText,
            @RequestParam("profileImage") MultipartFile profileImage,
            @AuthenticationPrincipal Object principal) {

        // DB 更新
        User updatedUser = userService.updateProfile(
                extractUser(principal).getEmail(),
                name,
                furigana,
                profileText,
                profileImage
        );

        // ★ principal の中身も更新する（ここが重要）
        if (principal instanceof UserDetailsImpl userDetails) {
            userDetails.getUser().setName(updatedUser.getName());
            userDetails.getUser().setFurigana(updatedUser.getFurigana());
            userDetails.getUser().setProfileText(updatedUser.getProfileText());
            userDetails.getUser().setProfileImage(updatedUser.getProfileImage());
        }

        if (principal instanceof CustomOAuth2User oauthUser) {
            oauthUser.getUser().setName(updatedUser.getName());
            oauthUser.getUser().setFurigana(updatedUser.getFurigana());
            oauthUser.getUser().setProfileText(updatedUser.getProfileText());
            oauthUser.getUser().setProfileImage(updatedUser.getProfileImage());
        }

        return "redirect:/mypage";
    }

}