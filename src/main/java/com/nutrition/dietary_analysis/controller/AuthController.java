package com.nutrition.dietary_analysis.controller;

import com.nutrition.dietary_analysis.model.User;
import com.nutrition.dietary_analysis.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;

    @Autowired
    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        System.out.println("Registration request for: " + (user != null ? user.getEmail() : "null"));
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request body is missing.");
        }
        try {
            if (user.getEmail() == null || !user.getEmail().endsWith("@gmail.com")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only @gmail.com emails are accepted.");
            }
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
            }
            
            // Log role assignment
            System.out.println("Assigning role: " + user.getRole());
            
            User savedUser = userRepository.save(user);
            System.out.println("User saved successfully with ID: " + savedUser.getId() + " and Role: " + savedUser.getRole());
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedUser.getId());
            response.put("email", savedUser.getEmail());
            response.put("username", savedUser.getUsername());
            response.put("role", savedUser.getRole());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error during registration: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during registration: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        System.out.println("Login request for email: " + (loginRequest != null ? loginRequest.getEmail() : "null"));
        if (loginRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request body is missing.");
        }
        try {
            Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
            if (userOpt.isPresent() && userOpt.get().getPassword().equals(loginRequest.getPassword())) {
                User user = userOpt.get();
                Map<String, Object> response = new HashMap<>();
                response.put("id", user.getId());
                response.put("email", user.getEmail());
                response.put("username", user.getUsername());
                response.put("role", user.getRole());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during login: " + e.getMessage());
        }
    }
}
