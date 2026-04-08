package com.nutrition.dietary_analysis.repository;

import com.nutrition.dietary_analysis.model.WaterLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WaterLogRepository extends JpaRepository<WaterLog, Long> {
    List<WaterLog> findByUserIdAndDate(Long userId, LocalDate date);

    List<WaterLog> findByUserId(Long userId);
}
