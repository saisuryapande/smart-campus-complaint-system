package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Complaint;
import com.example.demo.entity.User;
import com.example.demo.service.ComplaintService;
import com.example.demo.service.EmailService;  // NEW IMPORT

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    ComplaintService complaintService;
    
    @Autowired
    EmailService emailService;  // NEW AUTOWIRE

    @GetMapping("/complaint/new")
    public String form(Model model, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("loggedUser");
        
        if (user == null) {
            ra.addFlashAttribute("error", "Please login first to create a complaint");
            return "redirect:/users/login";
        }
        
        model.addAttribute("complaint", new Complaint());
        return "create-complaint";
    }

    @PostMapping("/complaint/save")
    public String save(@ModelAttribute Complaint complaint,
                       HttpSession session,
                       RedirectAttributes ra) {

        User user = (User) session.getAttribute("loggedUser");

        if (user == null) {
            ra.addFlashAttribute("error", "Session expired. Please login again");
            return "redirect:/users/login";
        }

        Complaint savedComplaint = complaintService.createComplaint(complaint, user);
        
        // NEW: Send email notification
        emailService.sendComplaintConfirmation(savedComplaint, user);
        
        ra.addFlashAttribute("success", "Your complaint has been submitted successfully! Check your email for confirmation.");

        return "redirect:/users/dashboard";
    }
}