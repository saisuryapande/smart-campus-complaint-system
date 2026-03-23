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

    /**
     * Send complaint confirmation email
     */
    public void sendComplaintConfirmation(Complaint complaint, User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("✅ Complaint #" + complaint.getComplaintId() + " Submitted Successfully");
        
        String emailBody = String.format(
            "Dear %s,\n\n" +
            "Your complaint has been submitted successfully!\n\n" +
            "═══════════════════════════════════\n" +
            "COMPLAINT DETAILS:\n" +
            "═══════════════════════════════════\n" +
            "Complaint ID: #%d\n" +
            "Title: %s\n" +
            "Category: %s\n" +
            "Priority: %s\n" +
            "Status: %s\n" +
            "Date: %s\n" +
            "═══════════════════════════════════\n\n" +
            "We will review your complaint and update you on the status.\n\n" +
            "You can track your complaint at: http://localhost:8080/users/dashboard\n\n" +
            "Thank you,\n" +
            "Smart Campus Support Team",
            user.getName(),
            complaint.getComplaintId(),
            complaint.getTitle(),
            complaint.getCategory(),
            complaint.getPriority(),
            complaint.getStatus(),
            complaint.getCreatedDate().toString()
        );
        
        message.setText(emailBody);
        
        try {
            mailSender.send(message);
            System.out.println("✅ Email sent to: " + user.getEmail());
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }

    /**
     * Send status update email
     */
    public void sendStatusUpdateEmail(Complaint complaint, User user, String oldStatus, String newStatus) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("🔄 Complaint #" + complaint.getComplaintId() + " Status Updated");
        
        String emailBody = String.format(
            "Dear %s,\n\n" +
            "Your complaint status has been updated!\n\n" +
            "═══════════════════════════════════\n" +
            "COMPLAINT DETAILS:\n" +
            "═══════════════════════════════════\n" +
            "Complaint ID: #%d\n" +
            "Title: %s\n" +
            "Previous Status: %s\n" +
            "New Status: %s\n" +
            "═══════════════════════════════════\n\n" +
            "You can view your complaint at: http://localhost:8080/users/dashboard\n\n" +
            "Thank you,\n" +
            "Smart Campus Support Team",
            user.getName(),
            complaint.getComplaintId(),
            complaint.getTitle(),
            oldStatus,
            newStatus
        );
        
        message.setText(emailBody);
        
        try {
            mailSender.send(message);
            System.out.println("✅ Status update email sent to: " + user.getEmail());
        } catch (Exception e) {
            System.err.println("❌ Failed to send status email: " + e.getMessage());
        }
    }
}