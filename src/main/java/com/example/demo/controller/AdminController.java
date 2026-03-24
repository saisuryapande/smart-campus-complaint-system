package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.demo.entity.Complaint;
import com.example.demo.entity.User;
import com.example.demo.service.ComplaintService;
import com.example.demo.service.EmailService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    ComplaintService complaintService;
    
    @Autowired
    EmailService emailService;
    
    @Autowired
    UserService userService;

    @GetMapping("/login")
    public String adminLoginPage() {
        return "admin-login";
    }

    @PostMapping("/login")
    public String adminLogin(@RequestParam String email,
                             @RequestParam String password,
                             HttpSession session,
                             RedirectAttributes ra) {

        User user = userService.login(email, password);

        if (user == null || !"ADMIN".equals(user.getRole())) {
            ra.addFlashAttribute("error", "❌ Invalid admin credentials");
            return "redirect:/admin/login";
        }

        session.setAttribute("loggedUser", user);
        ra.addFlashAttribute("success", "✅ Welcome Admin!");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/admin/login";
        }
        
        List<Complaint> complaints = complaintService.getAllComplaints();
        
        long total = complaints.size();
        long pending = complaints.stream().filter(c -> "Pending".equals(c.getStatus())).count();
        long resolved = complaints.stream().filter(c -> "Resolved".equals(c.getStatus())).count();
        long rejected = complaints.stream().filter(c -> "Rejected".equals(c.getStatus())).count();
        
        model.addAttribute("complaints", complaints);
        model.addAttribute("total", total);
        model.addAttribute("pending", pending);
        model.addAttribute("resolved", resolved);
        model.addAttribute("rejected", rejected);
        
        return "admin-dashboard";
    }

    @PostMapping("/update/{id}")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam String status,
                               RedirectAttributes ra,
                               HttpSession session) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/admin/login";
        }
        
        Optional<Complaint> complaintOpt = complaintService.getComplaintById(id);
        if (complaintOpt.isPresent()) {
            Complaint complaint = complaintOpt.get();
            String oldStatus = complaint.getStatus();
            User user = complaint.getUser();
            
            complaintService.updateStatus(id, status);
            
            if (user != null && !oldStatus.equals(status)) {
                emailService.sendStatusUpdateEmail(complaint, user, oldStatus, status);
            }
        }
        
        ra.addFlashAttribute("success", "Complaint #" + id + " status updated to " + status);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/delete/{id}")
    public String deleteComplaint(@PathVariable Long id,
                                  RedirectAttributes ra,
                                  HttpSession session) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/admin/login";
        }
        
        try {
            complaintService.deleteComplaint(id);
            ra.addFlashAttribute("success", "Complaint #" + id + " has been deleted");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error deleting complaint");
        }
        return "redirect:/admin/dashboard";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes ra) {
        session.invalidate();
        ra.addFlashAttribute("success", "Logged out successfully");
        return "redirect:/";
    }
}