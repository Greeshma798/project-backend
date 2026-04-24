package com.nutrition.dietary_analysis.controller;

import com.nutrition.dietary_analysis.model.DietRecord;
import com.nutrition.dietary_analysis.service.DietRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/diet-records")
@CrossOrigin(origins = "*")
public class DietRecordController {

    private final DietRecordService service;

    @Autowired
    public DietRecordController(DietRecordService service) {
        this.service = service;
    }

    @GetMapping
    public List<DietRecord> getRecords(@RequestParam(required = false) Long userId) {
        if (userId != null) {
            return service.getRecordsByUserId(userId);
        }
        return service.getAllRecords();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DietRecord> getRecordById(@PathVariable Long id) {
        return service.getRecordById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public DietRecord createRecord(@RequestBody DietRecord record) {
        return service.createRecord(record);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DietRecord> updateRecord(@PathVariable Long id, @RequestBody DietRecord record) {
        try {
            DietRecord updated = service.updateRecord(id, record);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        try {
            service.deleteRecord(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> clearUserHistory(@PathVariable Long userId) {
        service.clearHistoryByUserId(userId);
        return ResponseEntity.ok().build();
    }
}
