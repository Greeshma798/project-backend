package com.nutrition.dietary_analysis.controller;

import com.nutrition.dietary_analysis.model.WaterLog;
import com.nutrition.dietary_analysis.repository.WaterLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/water-logs")
@CrossOrigin(origins = "*")
public class WaterLogController {

    private final WaterLogRepository waterLogRepository;

    @Autowired
    public WaterLogController(WaterLogRepository waterLogRepository) {
        this.waterLogRepository = waterLogRepository;
    }

    @GetMapping
    public List<WaterLog> getWaterLogs(@RequestParam Long userId, @RequestParam(required = false) String date) {
        if (date != null) {
            return waterLogRepository.findByUserIdAndDate(userId, LocalDate.parse(date));
        }
        return waterLogRepository.findByUserId(userId);
    }

    @PostMapping
    public WaterLog createWaterLog(@RequestBody WaterLog waterLog) {
        if (waterLog.getDate() == null) {
            waterLog.setDate(LocalDate.now());
        }
        return waterLogRepository.save(waterLog);
    }
}
