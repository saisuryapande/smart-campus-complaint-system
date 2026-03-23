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
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    ComplaintService complaintService;
    
    @Autowired
    EmailService emailService;  // NEW AUTOWIRE

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/users/login";
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
            return "redirect:/users/login";
        }
        
        // Get complaint and user before update
        Optional<Complaint> complaintOpt = complaintService.getComplaintById(id);
        if (complaintOpt.isPresent()) {
            Complaint complaint = complaintOpt.get();
            String oldStatus = complaint.getStatus();
            User user = complaint.getUser();
            
            // Update status
            complaintService.updateStatus(id, status);
            
            // NEW: Send email if status changed and user exists
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
            return "redirect:/users/login";
        }
        
        try {
            complaintService.deleteComplaint(id);
            ra.addFlashAttribute("success", "Complaint #" + id + " has been deleted successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error deleting complaint #" + id);
        }
        return "redirect:/admin/dashboard";
    }
}