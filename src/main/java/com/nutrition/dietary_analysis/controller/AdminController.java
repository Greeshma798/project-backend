package com.nutrition.dietary_analysis.controller;

import com.nutrition.dietary_analysis.model.NutritionalStandard;
import com.nutrition.dietary_analysis.model.User;
import com.nutrition.dietary_analysis.repository.NutritionalStandardRepository;
import com.nutrition.dietary_analysis.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final NutritionalStandardRepository standardRepository;
    private final UserRepository userRepository;
    private final com.nutrition.dietary_analysis.repository.MedicationRepository medicationRepository;

    @Autowired
    public AdminController(NutritionalStandardRepository standardRepository, 
                           UserRepository userRepository,
                           com.nutrition.dietary_analysis.repository.MedicationRepository medicationRepository) {
        this.standardRepository = standardRepository;
        this.userRepository = userRepository;
        this.medicationRepository = medicationRepository;
    }

    // Nutritional Standards Management
    @GetMapping("/standards")
    public List<NutritionalStandard> getAllStandards() {
        return standardRepository.findAll();
    }

    @PostMapping("/standards")
    public NutritionalStandard createStandard(@RequestBody NutritionalStandard standard) {
        return standardRepository.save(standard);
    }

    @DeleteMapping("/standards/{id}")
    public ResponseEntity<?> deleteStandard(@PathVariable Long id) {
        standardRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // User Monitoring
    @GetMapping("/users-health")
    public List<Map<String, Object>> getUsersHealthSummary() {
        List<User> users = userRepository.findAll();
        List<Map<String, Object>> summary = new ArrayList<>();
        
        for (User user : users) {
             Map<String, Object> data = new HashMap<>();
             data.put("id", user.getId());
             data.put("username", user.getUsername());
             data.put("email", user.getEmail());
             data.put("age", user.getAge());
             data.put("weight", user.getWeight());
             data.put("height", user.getHeight());
             data.put("goal", user.getGoal());
             data.put("role", user.getRole());
             summary.add(data);
        }
        return summary;
    }

    // Medication Monitoring
    @GetMapping("/medications")
    public List<com.nutrition.dietary_analysis.model.Medication> getAllMedications() {
        return medicationRepository.findAll();
    }
}
