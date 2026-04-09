package com.nutrition.dietary_analysis.service;

import com.nutrition.dietary_analysis.model.DietRecord;
import com.nutrition.dietary_analysis.model.NutritionalStandard;
import com.nutrition.dietary_analysis.model.User;
import com.nutrition.dietary_analysis.repository.DietRecordRepository;
import com.nutrition.dietary_analysis.repository.NutritionalStandardRepository;
import com.nutrition.dietary_analysis.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class AnalysisService {

    private final DietRecordRepository dietRecordRepository;
    private final NutritionalStandardRepository standardRepository;
    private final UserRepository userRepository;

    @Autowired
    public AnalysisService(DietRecordRepository dietRecordRepository,
            NutritionalStandardRepository standardRepository,
            UserRepository userRepository) {
        this.dietRecordRepository = dietRecordRepository;
        this.standardRepository = standardRepository;
        this.userRepository = userRepository;
    }

    public Map<String, Object> analyzeUserNutrients(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty())
            return Collections.emptyMap();

        User user = userOpt.get();
        Integer age = user.getAge();
        String gender = user.getGender();

        if (age == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Age is required for nutritional analysis.");
            return error;
        }

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(7);
        List<DietRecord> records = dietRecordRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
        List<DietRecord> todayRecords = dietRecordRepository.findByUserIdAndDateBetween(userId, endDate, endDate);

        
        double avgCalories = 0, avgProtein = 0, avgCarbs = 0, avgFat = 0;
        double avgVitA = 0, avgVitC = 0, avgVitD = 0, avgIron = 0, avgCalcium = 0;
        if (!records.isEmpty()) {
            avgCalories = records.stream().mapToDouble(DietRecord::getCalories).sum() / 7.0;
            avgProtein = records.stream().mapToDouble(r -> r.getProtein() != null ? r.getProtein() : 0).sum() / 7.0;
            avgCarbs = records.stream().mapToDouble(r -> r.getCarbohydrates() != null ? r.getCarbohydrates() : 0).sum()
                    / 7.0;
            avgFat = records.stream().mapToDouble(r -> r.getFat() != null ? r.getFat() : 0).sum() / 7.0;

            avgVitA = records.stream().mapToDouble(r -> r.getVitaminA() != null ? r.getVitaminA() : 0).sum() / 7.0;
            avgVitC = records.stream().mapToDouble(r -> r.getVitaminC() != null ? r.getVitaminC() : 0).sum() / 7.0;
            avgVitD = records.stream().mapToDouble(r -> r.getVitaminD() != null ? r.getVitaminD() : 0).sum() / 7.0;
            avgIron = records.stream().mapToDouble(r -> r.getIron() != null ? r.getIron() : 0).sum() / 7.0;
            avgCalcium = records.stream().mapToDouble(r -> r.getCalcium() != null ? r.getCalcium() : 0).sum() / 7.0;
        }

        
        double todayCalories = 0, todayProtein = 0, todayCarbs = 0, todayFat = 0;
        double todayVitA = 0, todayVitC = 0, todayVitD = 0, todayIron = 0, todayCalcium = 0;
        if (!todayRecords.isEmpty()) {
            todayCalories = todayRecords.stream().mapToDouble(DietRecord::getCalories).sum();
            todayProtein = todayRecords.stream().mapToDouble(r -> r.getProtein() != null ? r.getProtein() : 0).sum();
            todayCarbs = todayRecords.stream().mapToDouble(r -> r.getCarbohydrates() != null ? r.getCarbohydrates() : 0)
                    .sum();
            todayFat = todayRecords.stream().mapToDouble(r -> r.getFat() != null ? r.getFat() : 0).sum();

            todayVitA = todayRecords.stream().mapToDouble(r -> r.getVitaminA() != null ? r.getVitaminA() : 0).sum();
            todayVitC = todayRecords.stream().mapToDouble(r -> r.getVitaminC() != null ? r.getVitaminC() : 0).sum();
            todayVitD = todayRecords.stream().mapToDouble(r -> r.getVitaminD() != null ? r.getVitaminD() : 0).sum();
            todayIron = todayRecords.stream().mapToDouble(r -> r.getIron() != null ? r.getIron() : 0).sum();
            todayCalcium = todayRecords.stream().mapToDouble(r -> r.getCalcium() != null ? r.getCalcium() : 0).sum();
        }

        Map<String, Double> personalizedTargets = calculateDailyTargets(user);

        List<Map<String, String>> alerts = new ArrayList<>();
        List<NutritionalStandard> standards = standardRepository.findAll();

        for (NutritionalStandard std : standards) {
            if (isApplicable(std, age, gender)) {
                double intake = 0;
                switch (std.getNutrientName().toLowerCase()) {
                    case "calories":
                        intake = avgCalories;
                        break;
                    case "protein":
                        intake = avgProtein;
                        break;
                    case "carbs":
                        intake = avgCarbs;
                        break;
                    case "fat":
                        intake = avgFat;
                        break;
                    case "vitamina":
                        intake = avgVitA;
                        break;
                    case "vitaminc":
                        intake = avgVitC;
                        break;
                    case "vitamind":
                        intake = avgVitD;
                        break;
                    case "iron":
                        intake = avgIron;
                        break;
                    case "calcium":
                        intake = avgCalcium;
                        break;
                }

                if (intake < std.getTargetValue() * 0.8) {
                    Map<String, String> alert = new HashMap<>();
                    alert.put("nutrient", std.getNutrientName());
                    alert.put("message", "High risk of " + std.getNutrientName() + " deficiency detected.");
                    alert.put("recommendation", getRecommendation(std.getNutrientName()));
                    alerts.add(alert);
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("averageIntake", Map.of(
                "calories", avgCalories, "protein", avgProtein, "carbs", avgCarbs, "fat", avgFat,
                "vitaminA", avgVitA, "vitaminC", avgVitC, "vitaminD", avgVitD, "iron", avgIron, "calcium", avgCalcium));
        result.put("todayIntake", Map.of(
                "calories", todayCalories, "protein", todayProtein, "carbs", todayCarbs, "fat", todayFat,
                "vitaminA", todayVitA, "vitaminC", todayVitC, "vitaminD", todayVitD, "iron", todayIron, "calcium",
                todayCalcium));
        result.put("targets", personalizedTargets);
        result.put("alerts", alerts);
        result.put("classification", getAgeClassification(age));
        result.put("smartInsight", generateSmartInsight(todayCalories, todayProtein, alerts));
        result.put("deficiencies", alerts.stream().map(a -> a.get("nutrient")).toList());

        return result;
    }

    public List<String> getDeficiencies(Long userId) {
        Map<String, Object> analysis = analyzeUserNutrients(userId);
        if (analysis.containsKey("deficiencies")) {
            return (List<String>) analysis.get("deficiencies");
        }
        return Collections.emptyList();
    }

    private String generateSmartInsight(double calories, double protein, List<Map<String, String>> alerts) {
        if (alerts.isEmpty() && calories > 0) {
            return "You're crushing your nutritional goals! Your intake perfectly matches the recommended standards for your age group.";
        }
        if (protein < 30 && calories > 500) {
            return "You're doing well on energy, but your protein is a bit low. Muscles need fuel to grow—try adding some lean protein to your next meal!";
        }
        if (!alerts.isEmpty()) {
            return "We've noticed a few gaps in your nutrition history. Check your alerts below to see how you can fuel your body better.";
        }
        return "Keep logging your meals to see detailed insights about your nutritional balance!";
    }

    private Map<String, Double> calculateDailyTargets(User user) {
        double tdee = 2000;
        if (user.getWeight() != null && user.getHeight() != null && user.getAge() != null) {
            double bmr;
            if ("MALE".equalsIgnoreCase(user.getGender())) {
                bmr = (10 * user.getWeight()) + (6.25 * user.getHeight()) - (5 * user.getAge()) + 5;
            } else {
                bmr = (10 * user.getWeight()) + (6.25 * user.getHeight()) - (5 * user.getAge()) - 161;
            }

            double multiplier = 1.2;
            if (user.getActivityLevel() != null) {
                switch (user.getActivityLevel().toLowerCase()) {
                    case "moderate":
                        multiplier = 1.55;
                        break;
                    case "active":
                        multiplier = 1.725;
                        break;
                    case "very_active":
                        multiplier = 1.9;
                        break;
                }
            }
            tdee = bmr * multiplier;

            if (user.getGoal() != null) {
                switch (user.getGoal().toLowerCase()) {
                    case "weight_loss":
                        tdee -= 500;
                        break;
                    case "muscle_gain":
                        tdee += 300;
                        break;
                }
            }
        }

        Map<String, Double> targets = new HashMap<>();
        targets.put("calories", tdee);
        targets.put("protein", (tdee * 0.25) / 4);
        targets.put("carbs", (tdee * 0.50) / 4);
        targets.put("fat", (tdee * 0.25) / 9);

        boolean isMale = "MALE".equalsIgnoreCase(user.getGender());
        targets.put("vitaminA", isMale ? 900.0 : 700.0);
        targets.put("vitaminC", isMale ? 90.0 : 75.0);
        targets.put("vitaminD", 15.0);
        targets.put("iron", isMale ? 8.0 : 18.0);
        targets.put("calcium", 1000.0);

        return targets;
    }

    private boolean isApplicable(NutritionalStandard std, int age, String gender) {
        if (std.getAgeRange() == null || std.getAgeRange().isEmpty()) return true;
        
        int min = 0;
        int max = 150;
        
        try {
            if (std.getAgeRange().contains("-")) {
                String[] range = std.getAgeRange().split("-");
                min = Integer.parseInt(range[0].trim());
                max = Integer.parseInt(range[1].trim());
            } else if (std.getAgeRange().contains("+")) {
                min = Integer.parseInt(std.getAgeRange().replace("+", "").trim());
            } else {
                min = Integer.parseInt(std.getAgeRange().trim());
                max = min;
            }
        } catch (Exception e) {
            // Default to broad range on parsing error
        }

        boolean ageMatch = age >= min && age <= max;
        boolean genderMatch = std.getGender() == null || 
                          std.getGender().equalsIgnoreCase("BOTH") || 
                          std.getGender().equalsIgnoreCase(gender);

        return ageMatch && genderMatch;
    }

    private String getRecommendation(String nutrient) {
        switch (nutrient.toLowerCase()) {
            case "protein":
                return "Increase intake of lean meats, eggs, beans, or lentils.";
            case "calories":
                return "Ensure adequate portion sizes and include healthy fats like avocado or nuts.";
            case "iron":
                return "Include more spinach, red meat, or iron-fortified cereals.";
            case "calcium":
                return "Consume more dairy products, broccoli, or fortified plant milks.";
            default:
                return "Consult with a pediatrician for a detailed dietary plan.";
        }
    }

    private String getAgeClassification(int age) {
        if (age < 12)
            return "CHILD";
        if (age < 19)
            return "ADOLESCENT";
        return "ADULT";
    }
}
