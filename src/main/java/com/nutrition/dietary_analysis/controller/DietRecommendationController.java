package com.nutrition.dietary_analysis.controller;

import com.nutrition.dietary_analysis.model.DietRecommendation;
import com.nutrition.dietary_analysis.service.DietRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "http://localhost:5173")
public class DietRecommendationController {

    @Autowired
    private DietRecommendationService service;

    @PostMapping("/request")
    public ResponseEntity<DietRecommendation> createRequest(@RequestBody DietRecommendation recommendation) {
        return ResponseEntity.ok(service.createRequest(recommendation));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DietRecommendation>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getRecommendationsByUser(userId));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<DietRecommendation>> getPending() {
        return ResponseEntity.ok(service.getAllPendingRequests());
    }

    @GetMapping("/all")
    public ResponseEntity<List<DietRecommendation>> getAll() {
        return ResponseEntity.ok(service.getAllRecommendations());
    }

    @PutMapping("/respond/{id}")
    public ResponseEntity<DietRecommendation> respond(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        Long nutritionistId = Long.valueOf(payload.get("nutritionistId").toString());
        String note = payload.get("nutritionistNote").toString();
        return ResponseEntity.ok(service.respondToRequest(id, nutritionistId, note));
    }
}
