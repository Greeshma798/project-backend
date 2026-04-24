package com.nutrition.dietary_analysis.service;

import com.nutrition.dietary_analysis.model.User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DietPlanService {

    private final AnalysisService analysisService;

    public DietPlanService(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    /**
     * Calculates BMR using the Mifflin-St Jeor equation and returns
     * a personalized diet plan with calorie/macro targets.
     */
    public Map<String, Object> generatePlan(User user) {
        try {
            Map<String, Object> plan = calculateTargets(user);
            if (plan.containsKey("error")) return plan;

            List<String> deficiencies = analysisService.getDeficiencies(user.getId());
            String health = user.getHealthConditions() != null ? user.getHealthConditions().toLowerCase() : "";

            // Suggested meal structure
            Object targetCalObj = plan.get("targetCalories");
            double targetCalories = (targetCalObj instanceof Long) ? (Long) targetCalObj : ((Number) targetCalObj).doubleValue();
            
            Map<String, Object> meals = new HashMap<>();
            meals.put("breakfast", Map.of("calories", Math.round(targetCalories * 0.25), "suggestion", getSafeSuggestion("breakfast", user, health, deficiencies, 0)));
            meals.put("lunch", Map.of("calories", Math.round(targetCalories * 0.35), "suggestion", getSafeSuggestion("lunch", user, health, deficiencies, 0)));
            meals.put("dinner", Map.of("calories", Math.round(targetCalories * 0.30), "suggestion", getSafeSuggestion("dinner", user, health, deficiencies, 0)));
            meals.put("snack", Map.of("calories", Math.round(targetCalories * 0.10), "suggestion", getSafeSuggestion("snack", user, health, deficiencies, 0)));

            plan.put("meals", meals);
            plan.put("deficiencies", deficiencies);
            plan.put("healthConsiderations", getHealthAdvice(health));
            plan.put("customDietPlan", user.getCustomDietPlan());

            return plan;
        } catch (Exception e) {
            Map<String, Object> errorPlan = new HashMap<>();
            errorPlan.put("error", "An error occurred while generating your plan: " + e.getMessage());
            return errorPlan;
        }
    }

    public Map<String, Object> generateWeeklyPlan(User user) {
        Map<String, Object> weeklyPlan = new LinkedHashMap<>();
        Map<String, Object> targets = calculateTargets(user);
        if (targets.containsKey("error")) return targets;

        List<String> deficiencies = new ArrayList<>();
        try {
            deficiencies = analysisService.getDeficiencies(user.getId());
        } catch (Exception e) {
            // Log error but continue with empty deficiencies
        }
        
        String health = user.getHealthConditions() != null ? user.getHealthConditions().toLowerCase() : "";
        Object targetCalObj = targets.get("targetCalories");
        double targetCalories = (targetCalObj instanceof Long) ? (Long) targetCalObj : ((Number) targetCalObj).doubleValue();

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (int i = 0; i < days.length; i++) {
            Map<String, Object> dayPlan = new HashMap<>();
            dayPlan.put("breakfast", getSafeSuggestion("breakfast", user, health, deficiencies, i));
            dayPlan.put("lunch", getSafeSuggestion("lunch", user, health, deficiencies, i));
            dayPlan.put("dinner", getSafeSuggestion("dinner", user, health, deficiencies, i));
            dayPlan.put("snack", getSafeSuggestion("snack", user, health, deficiencies, i));
            dayPlan.put("calories", targetCalories);
            weeklyPlan.put(days[i], dayPlan);
        }

        return weeklyPlan;
    }

    private Map<String, Object> calculateTargets(User user) {
        Map<String, Object> plan = new HashMap<>();
        if (user.getAge() == null || user.getWeight() == null || user.getHeight() == null) {
            plan.put("error", "Please complete your profile (age, weight, height) to get a personalized plan.");
            return plan;
        }

        // Default gender to Female if missing as it's a safer baseline for BMR
        String gender = user.getGender() != null ? user.getGender() : "FEMALE";
        double bmr = (10 * user.getWeight()) + (6.25 * user.getHeight()) - (5 * user.getAge()) + ("MALE".equalsIgnoreCase(gender) ? 5 : -161);
        double activityMultiplier = switch (user.getActivityLevel() != null ? user.getActivityLevel() : "moderate") {
            case "sedentary" -> 1.2;
            case "light" -> 1.375;
            case "moderate" -> 1.55;
            case "active" -> 1.725;
            case "very_active" -> 1.9;
            default -> 1.55;
        };
        double tdee = bmr * activityMultiplier;
        double targetCalories = switch (user.getGoal() != null ? user.getGoal() : "maintain") {
            case "lose" -> tdee - 500;
            case "gain" -> tdee + 400;
            default -> tdee;
        };

        plan.put("bmr", Math.round(bmr));
        plan.put("tdee", Math.round(tdee));
        plan.put("targetCalories", Math.round(targetCalories));
        plan.put("proteinGrams", Math.round((targetCalories * 0.30) / 4));
        plan.put("carbGrams", Math.round((targetCalories * 0.40) / 4));
        plan.put("fatGrams", Math.round((targetCalories * 0.30) / 9));
        plan.put("goal", user.getGoal() != null ? user.getGoal() : "maintain");
        return plan;
    }

    private String getSafeSuggestion(String mealType, User user, String health, List<String> deficiencies, int dayOffset) {
        try {
            return getSuggestion(mealType, user, health, deficiencies, dayOffset);
        } catch (Exception e) {
            return "Balanced healthy meal";
        }
    }

    private String getSuggestion(String mealType, User user, String health, List<String> deficiencies, int dayOffset) {
        List<String> breakfastIdeas = List.of(
            "Idli (3) with Sambar and Tomato Chutney", 
            "Ragi Dosa with Peanut Chutney", 
            "Vegetable Millet Upma", 
            "Pesarattu (Moong Dal Dosa) with Ginger Chutney", 
            "Brown Rice Poha (Aval) with Veggies"
        );
        List<String> lunchIdeas = List.of(
            "Red Rice (1 cup) with Sambar and Cabbage Poriyal", 
            "Millet Bisi Bele Bath with a side of Curd", 
            "2 Multigrain Rotis with Dal Thoy and Beans Palya", 
            "Lemon Rice (Brown Rice) with Roasted Peanuts & Cucumber", 
            "Brown Rice with Avial (Mixed Veg in Coconut-Curd)"
        );
        List<String> dinnerIdeas = List.of(
            "Oats Idli with Roasted Chana Dal Chutney", 
            "Ragi Mudde with Mixed Greens Saaru (Curry)", 
            "Vegetable Semiya (Vermicelli) Upma", 
            "Tomato Bath with Cucumber Raita", 
            "Millet Pongal with Coconut Chutney"
        );
        List<String> snackIdeas = List.of(
            "Masala Sundal (Boiled Chana with Tadka)", 
            "Buttermilk with Roasted Cumin and Curry Leaves", 
            "Roasted Makhana (Fox Nuts)", 
            "A bowl of Papaya / seasonal fruit", 
            "Sprouted Moong Salad with lemon juice"
        );

        String base = switch (mealType) {
            case "breakfast" -> breakfastIdeas.get(dayOffset % breakfastIdeas.size());
            case "lunch" -> lunchIdeas.get(dayOffset % lunchIdeas.size());
            case "dinner" -> dinnerIdeas.get(dayOffset % dinnerIdeas.size());
            case "snack" -> snackIdeas.get(dayOffset % snackIdeas.size());
            default -> "Balanced South Indian meal";
        };

        // Add health-specific modifications
        if (health.contains("diabetes")) base += " (Low Glycemic Index)";
        if (health.contains("hypertension")) base += " (Low Sodium)";
        if (health.contains("anemic") || deficiencies.contains("iron")) base += " + Squeeze of lemon for Iron absorption";
        if (deficiencies.contains("calcium")) base += " + Fortified almonds/dairy side";

        return base;
    }

    private String getHealthAdvice(String health) {
        if (health == null || health.isEmpty()) return "Focus on balanced whole foods.";
        StringBuilder advice = new StringBuilder("Personalized tips: ");
        if (health.contains("diabetes")) advice.append("Limit refined sugars and monitor carb portions. ");
        if (health.contains("hypertension")) advice.append("Reduce processed salt and focus on DASH diet principles. ");
        if (health.contains("anemic")) advice.append("Pair iron sources with Vitamin C for better absorption. ");
        return advice.toString();
    }
}
