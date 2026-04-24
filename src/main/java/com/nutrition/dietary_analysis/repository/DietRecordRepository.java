package com.nutrition.dietary_analysis.repository;

import com.nutrition.dietary_analysis.model.DietRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DietRecordRepository extends JpaRepository<DietRecord, Long> {
    List<DietRecord> findByUserId(Long userId);

    List<DietRecord> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    
    void deleteByUserId(Long userId);
}
