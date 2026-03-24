package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserPageController {

    @GetMapping("/login")
    public String userLoginPage() {
        return "user-login";
    }
    
    @GetMapping("/register")
    public String userRegisterPage() {
        return "user-register";
    }
}