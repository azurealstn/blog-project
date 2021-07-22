package com.azurealstn.blogproject.controller;

import com.azurealstn.blogproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;

    /**
     * 회원가입 페이지
     */
    @GetMapping("/auth/user/save")
    public String userSave() {
        return "layout/user/user-save";
    }

    /**
     * 로그인 페이지
     */
    @GetMapping("/auth/user/login")
    public String userLogin() {
        return "layout/user/user-login";
    }

    /**
     * 회원수정 페이지
     */
    @GetMapping("/user/update")
    public String userUpdate() {
        return "layout/user/user-update";
    }
}
