package com.example.militaryasset.service;

import com.example.militaryasset.entity.Role;
import com.example.militaryasset.entity.User;
import com.example.militaryasset.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BaseScopeService {
    private final UserRepository userRepository;

    public BaseScopeService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User user(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    public Long effectiveBaseId(Authentication authentication, Long requestedBaseId) {
        User user = user(authentication);
        if (user.getRole() == Role.BASE_COMMANDER) {
            if (user.getBase() == null) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No base is assigned to this user");
            if (requestedBaseId != null && !user.getBase().getId().equals(requestedBaseId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this base");
            return user.getBase().getId();
        }
        return requestedBaseId;
    }

    public void requireBase(Authentication authentication, Long baseId) {
        effectiveBaseId(authentication, baseId);
    }
}
