package com.nutrition.dietary_analysis.service;

import com.nutrition.dietary_analysis.model.DietRecord;
import com.nutrition.dietary_analysis.repository.DietRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DietRecordService {

    private final DietRecordRepository repository;

    @Autowired
    public DietRecordService(DietRecordRepository repository) {
        this.repository = repository;
    }

    public List<DietRecord> getAllRecords() {
        return repository.findAll();
    }

    public List<DietRecord> getRecordsByUserId(@NonNull Long userId) {
        return repository.findByUserId(userId);
    }

    public Optional<DietRecord> getRecordById(@NonNull Long id) {
        return repository.findById(id);
    }

    public DietRecord createRecord(@NonNull DietRecord record) {
        return repository.save(record);
    }

    public DietRecord updateRecord(@NonNull Long id, DietRecord updatedRecord) {
        return repository.findById(id).map(record -> {
            record.setFoodName(updatedRecord.getFoodName());
            record.setCalories(updatedRecord.getCalories());
            record.setProtein(updatedRecord.getProtein());
            record.setCarbohydrates(updatedRecord.getCarbohydrates());
            record.setFat(updatedRecord.getFat());
            record.setDate(updatedRecord.getDate());
            record.setUserId(updatedRecord.getUserId());
            return repository.save(record);
        }).orElseThrow(() -> new RuntimeException("DietRecord not found with id " + id));
    }

    public void deleteRecord(@NonNull Long id) {
        repository.deleteById(id);
    }
}
