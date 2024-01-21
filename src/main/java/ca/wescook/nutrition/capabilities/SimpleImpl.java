package ca.wescook.nutrition.capabilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.primitives.Floats;

import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import ca.wescook.nutrition.utility.Config;

// Baseline implementation of Capability
// Contains basic logic for each method defined in the Interface
public class SimpleImpl implements INutrientManager {

    // Stored nutrition for the attached player
    private Map<Nutrient, Float> nutrition = new HashMap<>();

    public SimpleImpl() {
        updateCapability();
    }

    @Override
    public Map<Nutrient, Float> get() {
        return nutrition;
    }

    @Override
    public Float get(Nutrient nutrient) {
        return nutrition.get(nutrient);
    }

    @Override
    public void set(Nutrient nutrient, Float value) {
        nutrition.put(nutrient, value);
    }

    @Override
    public void set(Map<Nutrient, Float> nutrientData) {
        for (Map.Entry<Nutrient, Float> entry : nutrientData.entrySet()) {
            nutrition.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void add(Nutrient nutrient, float amount) {
        float currentAmount = nutrition.get(nutrient);
        nutrition.put(nutrient, Floats.constrainToRange(currentAmount + amount, 0, 100));
    }

    @Override
    public void add(Map<Nutrient, Float> nutritionAmounts) {
        for (Entry<Nutrient, Float> entry : nutritionAmounts.entrySet()) {
            Nutrient nutrient = entry.getKey();
            Float amount = entry.getValue();
            nutrition.put(nutrient, Floats.constrainToRange(nutrition.get(nutrient) + amount, 0, 100));

        }
    }

    @Override
    public void subtract(Nutrient nutrient, float amount) {
        float currentAmount = nutrition.get(nutrient);
        nutrition.put(nutrient, Floats.constrainToRange(currentAmount - amount, 0, 100));
    }

    @Override
    public void subtract(List<Nutrient> nutrientData, float amount) {
        for (Nutrient nutrient : nutrientData) {
            nutrition.put(nutrient, Floats.constrainToRange(nutrition.get(nutrient) - amount, 0, 100));
        }
    }

    @Override
    public void reset(Nutrient nutrient) {
        set(nutrient, (float) Config.startingNutrition);
    }

    @Override
    public void reset() {
        for (Nutrient nutrient : nutrition.keySet()) // Loop through player's nutrients
        {
            reset(nutrient);
        }
    }

    @Override
    public void updateCapability() {
        // Copy map by value, not by reference
        Map<Nutrient, Float> nutritionOld = new HashMap<>(nutrition);

        // If nutrient already exists (by name), copy nutrition. Else reset from starting nutrition.
        nutrition.clear();
        loop:
        for (Nutrient nutrient : NutrientList.get()) {
            for (Map.Entry<Nutrient, Float> nutrientOld : nutritionOld.entrySet()) {
                if (nutrient.name.equals(nutrientOld.getKey().name)) {
                    nutrition.put(nutrient, nutrientOld.getValue());
                    continue loop;
                }
            }
            nutrition.put(nutrient, (float) Config.startingNutrition);
        }
    }
}
