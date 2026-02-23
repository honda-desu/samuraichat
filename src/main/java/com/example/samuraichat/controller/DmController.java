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
	
	//DmRoomの作成 or 取得 Dm画面へ遷移
	@GetMapping("/room/{partnerId}")
	public String openRoom(@PathVariable Long partnerId,
			HttpSession session,
			Model model) {
		
		Long myId = (Long) session.getAttribute("userId");
		
		// ★ ここにログを追加
	    System.out.println(">>> DMController: myId = " + myId);
	    System.out.println(">>> DMController: partnerId = " + partnerId);

		
		//Roomの取得または作成
		DmRoom room = dmService.getOrCreateRoom(myId, partnerId);
		
		return "redirect:/dm/chat/" + room.getId();
	}
	
	//DM画面
	@GetMapping("/chat/{roomId}")
	public String chat(
			@PathVariable Long roomId,
			Model model) {
		
		model.addAttribute("roomId", roomId);
		model.addAttribute("messages", dmService.getMessages(roomId));
		return "dm/chat"; //dm/chat.htmlを表示
		
	}
	
	//Messageを送信
	@PostMapping("/send")
	public String sendMessage(
			@RequestParam Long roomId,
			@RequestParam String content,
			HttpSession session) {
		
		Long senderId = (Long) session.getAttribute("userId");
		
		dmService.sendMessage(roomId, senderId, content, null);
		
		return "redirect:/dm/chat/" + roomId;
	}
	
	//DM一覧
	@GetMapping("/list")
	public String list(HttpSession session, Model model) {
		Long myId = (Long) session.getAttribute("userId");
		
		model.addAttribute("rooms", dmService.getRoomsForUser(myId));
		
		return "dm/list"; //dm/list.htmlを表示
	}
	
	@PostMapping("/{roomId}/images/upload")
	public String uploadDmImage(
	        @PathVariable Long roomId,
	        @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
	        @RequestParam("imageFile") MultipartFile imageFile,
	        RedirectAttributes redirectAttributes) {

	    if (imageFile.isEmpty()) {
	        redirectAttributes.addFlashAttribute("error", "画像ファイルが選択されていません。");
	        return "redirect:/dm/chat/" + roomId;
	    }

	    User user = userDetailsImpl.getUser();

	    // ★ ここが重要：orElseThrow は不要
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
