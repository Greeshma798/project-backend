package com.nutrition.dietary_analysis.repository;

import com.nutrition.dietary_analysis.model.NutritionalStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NutritionalStandardRepository extends JpaRepository<NutritionalStandard, Long> {
    List<NutritionalStandard> findByNutrientName(String nutrientName);
}
