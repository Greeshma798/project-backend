package com.nutrition.dietary_analysis.controller;

import com.nutrition.dietary_analysis.model.Routine;
import com.nutrition.dietary_analysis.repository.RoutineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routines")
@CrossOrigin(origins = "*")
public class RoutineController {

    private final RoutineRepository routineRepository;

    @Autowired
    public RoutineController(RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;
    }

    @GetMapping
    public List<Routine> getRoutines(@RequestParam Long userId) {
        return routineRepository.findByUserId(userId);
    }

    @PostMapping
    public Routine createRoutine(@RequestBody Routine routine) {
        return routineRepository.save(routine);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoutine(@PathVariable Long id) {
        routineRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
