package com.nutrition.dietary_analysis.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "diet_recommendations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DietRecommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private Long nutritionistId; 

    @Column(columnDefinition = "TEXT")
    private String userMessage; 

    @Column(columnDefinition = "TEXT")
    private String nutritionistNote; 

    private String status = "PENDING"; 

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime respondedAt;
}
