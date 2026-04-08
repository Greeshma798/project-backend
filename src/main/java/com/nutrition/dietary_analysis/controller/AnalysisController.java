package com.nutrition.dietary_analysis.controller;

import com.nutrition.dietary_analysis.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "*")
public class AnalysisController {

    private final AnalysisService analysisService;

    @Autowired
    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserAnalysis(@PathVariable Long userId) {
        Map<String, Object> analysis = analysisService.analyzeUserNutrients(userId);
        return ResponseEntity.ok(analysis);
    }
}
