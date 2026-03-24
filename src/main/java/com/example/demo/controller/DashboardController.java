package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.demo.entity.User;
import com.example.demo.service.ComplaintService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/users")
public class DashboardController {

    @Autowired
    ComplaintService service;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model){

        User user = (User) session.getAttribute("loggedUser");

        if(user == null){
            return "redirect:/user/login";
        }

        model.addAttribute("complaints", service.getComplaintsByUser(user.getUserId()));
        model.addAttribute("username", user.getName());

        return "user-dashboard";
    }
}