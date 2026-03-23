package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes ra) {

        User user = userService.login(email, password);

        if (user == null) {
            ra.addFlashAttribute("error", "❌ Invalid email or password");
            return "redirect:/users/login";
        }

        session.setAttribute("loggedUser", user);

        if ("ADMIN".equals(user.getRole())) {
            ra.addFlashAttribute("success", "✅ Welcome Admin! Login successful");
            return "redirect:/admin/dashboard";
        }

        ra.addFlashAttribute("success", "✅ Welcome " + user.getName() + "! Login successful");
        return "redirect:/users/dashboard";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(User user,
                               RedirectAttributes ra) {
        
        User existingUser = userService.findByEmail(user.getEmail());
        if (existingUser != null) {
            ra.addFlashAttribute("error", "❌ Email already registered");
            return "redirect:/users/register";
        }
        
        userService.registerUser(user);
        ra.addFlashAttribute("success", "✅ Account created successfully! Please login");
        return "redirect:/users/login";
    }
    
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }
    
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes ra) {
        
        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "❌ Passwords do not match");
            return "redirect:/users/forgot-password";
        }
        
        User user = userService.findByEmail(email);
        if (user == null) {
            ra.addFlashAttribute("error", "❌ Email not found");
            return "redirect:/users/forgot-password";
        }
        
        user.setPassword(newPassword);
        userService.registerUser(user);
        ra.addFlashAttribute("success", "✅ Password reset successfully! Please login");
        return "redirect:/users/login";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes ra) {
        session.invalidate();
        ra.addFlashAttribute("success", "✅ Logged out successfully");
        return "redirect:/users/login";
    }
}