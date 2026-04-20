package com.nutrition.dietary_analysis.controller;

import com.nutrition.dietary_analysis.model.Medication;
import com.nutrition.dietary_analysis.repository.MedicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/medications")
@CrossOrigin(origins = "*")
public class MedicationController {

    private final MedicationRepository medicationRepository;

    @Autowired
    public MedicationController(MedicationRepository medicationRepository) {
        this.medicationRepository = medicationRepository;
    }

    @GetMapping("/user/{userId}")
    public List<Medication> getByUserId(@PathVariable Long userId) {
        return medicationRepository.findByUserId(userId);
    }

    @PostMapping
    public Medication add(@RequestBody Medication medication) {
        if (medication.getStartDate() == null) {
            medication.setStartDate(java.time.LocalDate.now());
        }
        return medicationRepository.save(medication);
    }
}
