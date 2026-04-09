package com.nutrition.controller;

import org.springframework.web.bind.annotation.*;

import com.nutrition.dietary_analysis.model.User;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class NutritionController {

   
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();
        if(user.getUsername().equals("admin") && user.getPassword().equals("admin123")) {
            response.put("role", "ADMIN");
            return response;
        }
        response.put("role", "USER");
        return response;
    }

    @PostMapping("/analyze")
    public Map<String, Object> analyze(@RequestBody Map<String, Object> data) {
        double iron = Double.parseDouble(data.get("iron").toString());
        String age = data.get("ageGroup").toString();
        
        List<String> advice = new ArrayList<>();
        if (iron < 10 && age.equals("Child")) {
            advice.add("Iron is low. Suggestion: Spinach, beans, or fortified cereal.");
        } else {
            advice.add("Intake looks normal. Maintain a balanced diet.");
        }

        Map<String, Object> res = new HashMap<>();
        res.put("interventions", advice);
        return res;
    }
}