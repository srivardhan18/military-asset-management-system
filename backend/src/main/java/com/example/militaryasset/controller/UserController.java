package com.example.militaryasset.controller;

import com.example.militaryasset.entity.User;
import com.example.militaryasset.repository.UserRepository;
import com.example.militaryasset.repository.BaseRepository;
import com.example.militaryasset.service.AuditService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserRepository userRepository;
    private final BaseRepository baseRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public UserController(UserRepository userRepository, BaseRepository baseRepository, PasswordEncoder passwordEncoder, AuditService auditService) {
        this.userRepository = userRepository;
        this.baseRepository = baseRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public User createUser(@Valid @RequestBody User user, Authentication authentication) {
        user.setBase(user.getBase() == null ? null : baseRepository.findById(user.getBase().getId()).orElseThrow(() -> new RuntimeException("Base not found")));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);
        auditService.record(userRepository.findByEmail(authentication.getName()).orElseThrow(), "CREATE_USER", "USER", saved.getId(), saved.getEmail());
        return saved;
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public User updateUser(@PathVariable Long id, @RequestBody User updated, Authentication authentication) {
        User existing = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        existing.setName(updated.getName());
        existing.setRole(updated.getRole());
        existing.setActive(updated.isActive());
        existing.setBase(updated.getBase() == null ? null : baseRepository.findById(updated.getBase().getId()).orElseThrow(() -> new RuntimeException("Base not found")));
        if (updated.getPassword() != null && !updated.getPassword().isBlank()) existing.setPassword(passwordEncoder.encode(updated.getPassword()));
        User saved = userRepository.save(existing);
        auditService.record(userRepository.findByEmail(authentication.getName()).orElseThrow(), "UPDATE_USER", "USER", saved.getId(), saved.getEmail());
        return saved;
    }
}
