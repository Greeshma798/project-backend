package com.nutrition.dietary_analysis.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "nutritional_standards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NutritionalStandard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nutrientName;

    @Column(nullable = false)
    private String ageRange;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private Double targetValue;

    private String unit;
}
