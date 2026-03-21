package com.example.samuraichat.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraichat.entity.ChatGroup;
import com.example.samuraichat.entity.User;
import com.example.samuraichat.form.ChatRegisterForm;
import com.example.samuraichat.security.CustomOAuth2User;
import com.example.samuraichat.security.UserDetailsImpl;
import com.example.samuraichat.service.ChatGroupService;
import com.example.samuraichat.service.MessageService;

@Controller
@RequestMapping("/groups")
public class ChatGroupController {
	private final ChatGroupService chatGroupService;
	private final MessageService messageService;

	
	public ChatGroupController(ChatGroupService chatGroupService, MessageService messageService) {
		this.chatGroupService = chatGroupService;
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

	
    @GetMapping
    public String index(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
            @AuthenticationPrincipal Object principal,
            Model model) {

        Page<ChatGroup> chatGroupPage = chatGroupService.findAllChatGroups(pageable);

        // ★ ログインユーザー取得
        User me = extractUser(principal);
        Long myId = me.getId();

        // ★★★ 各グループに未読数をセット（重要）★★★
        for (ChatGroup group : chatGroupPage.getContent()) {
            int unread = messageService.getUnreadCount(group.getId(), myId);
            group.setUnreadCount(unread);
        }

        model.addAttribute("chatGroupPage", chatGroupPage);

        return "chatGroup/index";
    }

	
	@GetMapping("/register")
	public String register(Model model) {
		model.addAttribute("chatRegisterForm", new ChatRegisterForm());
		return "chatGroup/register";
	}
	
	@PostMapping("/create")
	public String create(@ModelAttribute @Validated ChatRegisterForm chatRegisterForm,
						 BindingResult bindingResult,
						 RedirectAttributes redirectAttributes,
						 Model model)
	{
		if (bindingResult.hasErrors()) {
			model.addAttribute("chatRegisterForm", chatRegisterForm);
			
			return "chatGroup/register";
		}
		
		chatGroupService.create(chatRegisterForm);
		redirectAttributes.addFlashAttribute("successMessage", "新しいチャットグループを登録しました。");
		
		return "redirect:/groups";
	}
}
