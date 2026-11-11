package com.example.demosecurity.controller;

import com.example.demosecurity.dto.UserRequest;
import com.example.demosecurity.dto.UserResponse;
import com.example.demosecurity.model.User;
import com.example.demosecurity.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRequest request) {
        User saved = userService.register(
                request.getUsername(), request.getDisplayName(), request.getPassword());
        UserResponse body = new UserResponse(saved.getUsername(), saved.getDisplayName());
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/users")
    public List<UserResponse> listUsers() {
        return userService.findAll().stream()
                .map(u -> new UserResponse(u.getUsername(), u.getDisplayName()))
                .toList();
    }

    @GetMapping("/users/{username}")
    public UserResponse getUser(@PathVariable String username) {
        User user = userService.findByName(username);
        return new UserResponse(user.getUsername(), user.getDisplayName());
    }

    @GetMapping("/me")
    public Map<String, String> me() {
        return Map.of(
                "username", userService.findByName(
                        org.springframework.security.core.context.SecurityContextHolder
                                .getContext().getAuthentication().getName()).getUsername(),
                "message", "Authenticated via HTTP Basic"
        );
    }
}
