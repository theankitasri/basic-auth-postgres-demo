package com.example.springsecuritydemo.service;

import com.example.springsecuritydemo.model.User;
import com.example.springsecuritydemo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String username, String displayName, String rawPassword) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username must not be blank");
        }
        if (rawPassword == null || rawPassword.length() < 8) {
            throw new IllegalArgumentException("password must be at least 8 characters");
        }
        if (userRepository.existsById(username)) {
            throw new IllegalStateException("username already exists: " + username);
        }
        User user = new User(username, displayName != null ? displayName : username,
                passwordEncoder.encode(rawPassword));
        return userRepository.save(user);
    }

    public User findByName(String username) {
        return userRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("no user: " + username));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
