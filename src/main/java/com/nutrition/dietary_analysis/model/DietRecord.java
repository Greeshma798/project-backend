package com.nutrition.dietary_analysis.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "diet_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DietRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String foodName;

    @Column(nullable = false)
    private Integer calories;

    private Double protein;
    private Double carbohydrates;
    private Double fat;

    // Micronutrients
    private Double vitaminA;
    private Double vitaminC;
    private Double vitaminD;
    private Double iron;
    private Double calcium;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "user_id")
    private Long userId;

}
