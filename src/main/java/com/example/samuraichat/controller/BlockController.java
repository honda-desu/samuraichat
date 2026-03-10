package com.example.samuraichat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.samuraichat.entity.User;
import com.example.samuraichat.repository.UserRepository;
import com.example.samuraichat.service.BlockAndReportService;

import jakarta.servlet.http.HttpSession;

@Controller
public class BlockController {

    private final BlockAndReportService blockAndReportService;
    private final UserRepository userRepository;

    public BlockController(BlockAndReportService blockAndReportService,
                           UserRepository userRepository) {
        this.blockAndReportService = blockAndReportService;
        this.userRepository = userRepository;
    }

    // 理由入力ページ
    @GetMapping("/block/{targetId}/reason")
    public String showBlockReasonForm(@PathVariable Long targetId, @RequestParam Long roomId, Model model) {

        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        model.addAttribute("target", target);
        model.addAttribute("targetId", targetId);
        model.addAttribute("roomId", roomId);

        return "block/reason-form";
    }

    // ブロック実行
    @PostMapping("/block/{targetId}")
    public String blockUser(
            @PathVariable Long targetId,
            @RequestParam(required = false) String reason,
            HttpSession session) {

        Long blockerId = (Long) session.getAttribute("userId");

        if (reason == null || reason.isBlank()) {
            reason = "ブロック時の自動通報";
        }

        blockAndReportService.blockUser(blockerId, targetId, reason);

        return "redirect:/dm/list"; // 適宜変更
    }
    
 // ★ ブロック解除
    @GetMapping("/block/{targetId}/unblock")
    public String unblockUser(
            @PathVariable Long targetId,
            HttpSession session,
            @RequestParam(required = false) Long roomId) {

        Long blockerId = (Long) session.getAttribute("userId");

        // ブロック解除処理
        blockAndReportService.unblock(blockerId, targetId);

        // DM画面から来た場合は DM画面へ戻す
        if (roomId != null) {
            return "redirect:/dm/chat/" + roomId;
        }

        // それ以外は DM一覧へ
        return "redirect:/dm/list";
    }

}