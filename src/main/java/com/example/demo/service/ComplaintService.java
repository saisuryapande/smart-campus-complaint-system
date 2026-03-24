package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.entity.Complaint;
import com.example.demo.entity.User;
import com.example.demo.repository.ComplaintRepository;

@Service
public class ComplaintService {
    
    @Autowired
    private ComplaintRepository complaintRepository;
    
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }
    
    public Optional<Complaint> getComplaintById(Long id) {
        return complaintRepository.findById(id);
    }
    
    public List<Complaint> getComplaintsByUser(Long userId) {
        return complaintRepository.findByUser_UserId(userId);
    }
    
    public Complaint createComplaint(Complaint complaint, User user) {
        complaint.setUser(user);
        complaint.setCreatedDate(LocalDateTime.now());
        complaint.setStatus("Pending");
        return complaintRepository.save(complaint);
    }
    
    public void updateStatus(Long id, String status) {
        Complaint complaint = complaintRepository.findById(id).orElse(null);
        if (complaint != null) {
            complaint.setStatus(status);
            complaintRepository.save(complaint);
        }
    }
    
    public void deleteComplaint(Long id) {
        complaintRepository.deleteById(id);
    }
    
    public long countByStatus(String status) {
        return complaintRepository.countByStatus(status);
    }
}