package com.nutrition.dietary_analysis.repository;

import com.nutrition.dietary_analysis.model.DietRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DietRecommendationRepository extends JpaRepository<DietRecommendation, Long> {
    List<DietRecommendation> findByUserId(Long userId);
    List<DietRecommendation> findByStatus(String status);
}
