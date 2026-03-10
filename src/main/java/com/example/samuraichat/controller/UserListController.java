package com.example.samuraichat.controller;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.samuraichat.entity.User;
import com.example.samuraichat.service.UserService;

@Controller
public class UserListController {

    private final UserService userService;

    public UserListController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public String showUserList(@RequestParam(defaultValue = "0") int page, Model model) {

        Page<User> userPage = userService.getUserPage(page);
        model.addAttribute("userPage", userPage);

        return "user/user-list";
    }
}