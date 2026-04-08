package com.nutrition.dietary_analysis.controller;

import com.nutrition.dietary_analysis.model.User;
import com.nutrition.dietary_analysis.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setPassword(null); // Don't send password back
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    if (updatedUser.getAge() != null) user.setAge(updatedUser.getAge());
                    if (updatedUser.getWeight() != null) user.setWeight(updatedUser.getWeight());
                    if (updatedUser.getHeight() != null) user.setHeight(updatedUser.getHeight());
                    if (updatedUser.getGoal() != null) user.setGoal(updatedUser.getGoal());
                    if (updatedUser.getActivityLevel() != null) user.setActivityLevel(updatedUser.getActivityLevel());
                    if (updatedUser.getUsername() != null) user.setUsername(updatedUser.getUsername());
                    User saved = userRepository.save(user);
                    saved.setPassword(null);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
