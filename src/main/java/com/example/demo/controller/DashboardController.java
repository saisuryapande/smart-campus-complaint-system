package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Complaint;
import com.example.demo.entity.User;
import com.example.demo.service.ComplaintService;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/users")
public class DashboardController {

    @Autowired
    ComplaintService service;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");

        if (user == null) {
            return "redirect:/users/login";
        }

        List<Complaint> complaints = service.getComplaintsByUser(user.getUserId());
        
        long pendingCount = complaints.stream().filter(c -> "Pending".equals(c.getStatus())).count();
        long resolvedCount = complaints.stream().filter(c -> "Resolved".equals(c.getStatus())).count();
        
        model.addAttribute("complaints", complaints);
        model.addAttribute("username", user.getName());
        model.addAttribute("totalComplaints", complaints.size());
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("resolvedCount", resolvedCount);

        return "user-dashboard";
    }
}