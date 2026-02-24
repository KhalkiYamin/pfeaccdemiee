package com.pfe.pfeaccdemie.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.pfe.pfeaccdemie.entities.Role;
import com.pfe.pfeaccdemie.entities.User;
import com.pfe.pfeaccdemie.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    @GetMapping("/coaches/pending")
    public List<User> getPendingCoaches() {
        return userRepository.findByRoleAndEnabled(Role.COACH, false);
    }

    @PutMapping("/coaches/{id}/approve")
    public String approveCoach(@PathVariable Long id) {
        User coach = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coach introuvable"));
        coach.setEnabled(true);
        userRepository.save(coach);
        return "Coach approuvé";
    }

    @PutMapping("/coaches/{id}/reject")
    public String rejectCoach(@PathVariable Long id) {
        User coach = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coach introuvable"));
        userRepository.delete(coach); // أو تنجم تعمل status REJECTED إذا تحب
        return "Coach refusé";
    }
}