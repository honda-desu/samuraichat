package com.example.samuraichat.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraichat.entity.DmRoom;
import com.example.samuraichat.entity.User;
import com.example.samuraichat.security.CustomOAuth2User;
import com.example.samuraichat.security.UserDetailsImpl;
import com.example.samuraichat.service.DmService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/dm")
public class DmController {

    private final DmService dmService;

    public DmController(DmService dmService) {
        this.dmService = dmService;
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

    @GetMapping("/room/{partnerId}")
    public String openRoom(@PathVariable Long partnerId,
                           @AuthenticationPrincipal Object principal) {

        User me = extractUser(principal);
        Long myId = me.getId();

        DmRoom room = dmService.getOrCreateRoom(myId, partnerId);

        return "redirect:/dm/chat/" + room.getId();
    }

    @GetMapping("/chat/{roomId}")
    public String chat(@PathVariable Long roomId, Model model) {

        model.addAttribute("roomId", roomId);
        model.addAttribute("messages", dmService.getMessages(roomId));

        return "dm/chat";
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam Long roomId,
                              @RequestParam String content,
                              @AuthenticationPrincipal Object principal) {

        User me = extractUser(principal);
        Long senderId = me.getId();

        dmService.sendMessage(roomId, senderId, content, null);

        return "redirect:/dm/chat/" + roomId;
    }

    @GetMapping("/list")
    public String list(@AuthenticationPrincipal Object principal, HttpSession session, Model model) {

        User me = extractUser(principal);
        Long myId = me.getId();
        
        session.setAttribute("userId", myId);

        model.addAttribute("rooms", dmService.getRoomsForUser(myId));

        return "dm/list";
    }

    @PostMapping("/{roomId}/images/upload")
    public String uploadDmImage(@PathVariable Long roomId,
                                @AuthenticationPrincipal Object principal,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                RedirectAttributes redirectAttributes) {

        if (imageFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "画像ファイルが選択されていません。");
            return "redirect:/dm/chat/" + roomId;
        }

        // ★ Googleログインでもフォームログインでも User を取得
        User user = extractUser(principal);

        DmRoom room = dmService.findById(roomId);

        try {
            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();

            String uploadRoot = System.getProperty("user.dir") + "/uploads";
            Path uploadDir = Paths.get(uploadRoot);
            Files.createDirectories(uploadDir);

            Path filePath = uploadDir.resolve(fileName);
            imageFile.transferTo(filePath.toFile());

            String imagePath = "/uploads/" + fileName;

            dmService.saveImageMessage(room, user, imagePath);

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "画像のアップロードに失敗しました。");
        }

        return "redirect:/dm/chat/" + roomId;
    }
}