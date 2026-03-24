package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";  // This should be your landing page
    }
    
    @GetMapping("/login-choice")
    public String loginChoice() {
        return "login-choice";
    }
}