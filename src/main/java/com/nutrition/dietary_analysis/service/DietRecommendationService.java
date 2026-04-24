package com.nutrition.dietary_analysis.service;

import com.nutrition.dietary_analysis.model.DietRecommendation;
import com.nutrition.dietary_analysis.repository.DietRecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DietRecommendationService {

    @Autowired
    private DietRecommendationRepository repository;

    public DietRecommendation createRequest(DietRecommendation recommendation) {
        recommendation.setStatus("PENDING");
        recommendation.setCreatedAt(LocalDateTime.now());
        return repository.save(recommendation);
    }

    public List<DietRecommendation> getRecommendationsByUser(Long userId) {
        return repository.findByUserId(userId);
    }

    public List<DietRecommendation> getAllPendingRequests() {
        return repository.findByStatus("PENDING");
    }

    public List<DietRecommendation> getAllRecommendations() {
        return repository.findAll();
    }

    public DietRecommendation respondToRequest(Long id, Long nutritionistId, String note) {
        DietRecommendation recommendation = repository.findById(id).orElseThrow(() -> new RuntimeException("Request not found"));
        recommendation.setNutritionistId(nutritionistId);
        recommendation.setNutritionistNote(note);
        recommendation.setStatus("RESPONDED");
        recommendation.setRespondedAt(LocalDateTime.now());
        return repository.save(recommendation);
    }
}
