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

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes ra) {

        User user = userService.login(email, password);

        if (user == null) {
            ra.addFlashAttribute("error", "❌ Invalid email or password");
            return "redirect:/user/login";
        }

        session.setAttribute("loggedUser", user);

        if ("ADMIN".equals(user.getRole())) {
            ra.addFlashAttribute("success", "✅ Welcome Admin!");
            return "redirect:/admin/dashboard";
        }

        ra.addFlashAttribute("success", "✅ Welcome " + user.getName() + "!");
        return "redirect:/users/dashboard";
    }

    @PostMapping("/register")
    public String registerUser(User user,
                               RedirectAttributes ra) {
        
        User existingUser = userService.findByEmail(user.getEmail());
        if (existingUser != null) {
            ra.addFlashAttribute("error", "❌ Email already registered");
            return "redirect:/user/register";
        }
        
        userService.registerUser(user);
        ra.addFlashAttribute("success", "✅ Account created successfully! Please login");
        return "redirect:/user/login";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes ra) {
        session.invalidate();
        ra.addFlashAttribute("success", "✅ Logged out successfully");
        return "redirect:/";
    }
}