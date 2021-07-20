package com.azurealstn.blogproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

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
}
