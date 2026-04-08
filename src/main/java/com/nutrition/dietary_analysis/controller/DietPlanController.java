package com.nutrition.dietary_analysis.controller;

import com.nutrition.dietary_analysis.repository.UserRepository;
import com.nutrition.dietary_analysis.service.DietPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/diet-plan")
@CrossOrigin(origins = "*")
public class DietPlanController {

    private final DietPlanService dietPlanService;
    private final UserRepository userRepository;

    @Autowired
    public DietPlanController(DietPlanService dietPlanService, UserRepository userRepository) {
        this.dietPlanService = dietPlanService;
        this.userRepository = userRepository;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getDietPlan(@PathVariable Long userId) {
        if (userId == null) return ResponseEntity.badRequest().build();
        return userRepository.findById(userId)
                .map(user -> ResponseEntity.ok(dietPlanService.generatePlan(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{userId}/weekly")
    public ResponseEntity<Map<String, Object>> getWeeklyDietPlan(@PathVariable Long userId) {
        if (userId == null) return ResponseEntity.badRequest().build();
        return userRepository.findById(userId)
                .map(user -> ResponseEntity.ok(dietPlanService.generateWeeklyPlan(user)))
                .orElse(ResponseEntity.notFound().build());
    }
}
