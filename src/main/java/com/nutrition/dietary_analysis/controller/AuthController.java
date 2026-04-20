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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;
    private final Map<String, String> captchaStore = new ConcurrentHashMap<>();

    @Autowired
    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/captcha")
    public Map<String, String> getCaptcha() {
        String id = UUID.randomUUID().toString();
        String text = generateRandomText(6);
        captchaStore.put(id, text);
        
        Map<String, String> response = new HashMap<>();
        response.put("captchaId", id);
        response.put("captchaText", text); // In a real app, you might return an image
        return response;
    }

    private String generateRandomText(int length) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
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
            
            User savedUser = userRepository.save(user);
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedUser.getId());
            response.put("email", savedUser.getEmail());
            response.put("username", savedUser.getUsername());
            response.put("role", savedUser.getRole());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error during registration: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during registration: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        String captchaId = loginRequest.get("captchaId");
        String captchaAnswer = loginRequest.get("captchaAnswer");

        System.out.println("Login request for email: " + email);

        // Validate Captcha
        if (captchaId == null || captchaAnswer == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Captcha missing");
        }
        String storedAnswer = captchaStore.get(captchaId);
        if (storedAnswer == null || !storedAnswer.equalsIgnoreCase(captchaAnswer)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired captcha");
        }
        captchaStore.remove(captchaId); // One-time use

        try {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during login: " + e.getMessage());
        }
    }
}
