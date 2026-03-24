package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.example.demo.entity.Complaint;
import com.example.demo.entity.User;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendComplaintConfirmation(Complaint complaint, User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("✅ Complaint #" + complaint.getComplaintId() + " Submitted");
        
        String emailBody = String.format(
            "Dear %s,\n\nYour complaint has been submitted successfully!\n\n" +
            "Complaint ID: #%d\nTitle: %s\nCategory: %s\nPriority: %s\nStatus: %s\n\n" +
            "Thank you,\nSmart Campus Team",
            user.getName(), complaint.getComplaintId(), complaint.getTitle(),
            complaint.getCategory(), complaint.getPriority(), complaint.getStatus()
        );
        
        message.setText(emailBody);
        
        try {
            mailSender.send(message);
            System.out.println("✅ Email sent to: " + user.getEmail());
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }

    public void sendStatusUpdateEmail(Complaint complaint, User user, String oldStatus, String newStatus) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("🔄 Complaint #" + complaint.getComplaintId() + " Status Updated");
        
        String emailBody = String.format(
            "Dear %s,\n\nYour complaint status has been updated!\n\n" +
            "Complaint ID: #%d\nTitle: %s\nPrevious Status: %s\nNew Status: %s\n\n" +
            "Thank you,\nSmart Campus Team",
            user.getName(), complaint.getComplaintId(), complaint.getTitle(), oldStatus, newStatus
        );
        
        message.setText(emailBody);
        
        try {
            mailSender.send(message);
            System.out.println("✅ Status email sent to: " + user.getEmail());
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }
}