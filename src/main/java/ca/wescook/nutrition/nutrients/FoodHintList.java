package ca.wescook.nutrition.nutrients;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import ca.wescook.nutrition.api.NutrientApplicationPhase;

// Maintains information about food hint
// Stored client and server-side
public class FoodHintList {

    private static List<FoodHint> foodHints = new ArrayList<>();

    // Register list of JSON objects
    public static void register(List<FoodHint> nutrientsIn) {
        foodHints.clear();
        foodHints.addAll(nutrientsIn);
    }

    // INTERNAL
    // Returns whether the ItemStack is a valid food (or null if there is no hint for it).
    @Nullable
    static Boolean isValidFood(ItemStack itemStack) {
        FoodHint hint = getHint(itemStack);
        return hint != null ? hint.isValidFood : null;
    }

    // INTERNAL
    // Returns the healAmount of the ItemStack (or null if there is no hint for it).
    @Nullable
    static Float getHealAmount(ItemStack itemStack) {
        FoodHint hint = getHint(itemStack);
        return hint != null ? hint.healAmount : null;
    }

    // INTERNAL
    // Returns the healAmount of the ItemStack (or null if there is no hint for it).
    @Nullable
    public static NutrientApplicationPhase getNutrientApplicationPhase(ItemStack itemStack) {
        FoodHint hint = getHint(itemStack);
        return hint != null ? hint.nutrientApplicationPhase : null;
    }

    // Returns the hint of the food (or null if there is no hint for it).
    @Nullable
    private static FoodHint getHint(ItemStack itemStack) {
        for (FoodHint foodHint : foodHints) {
            if (foodHint.isTarget(itemStack))
                return foodHint;
        }
        return null;
    }
}
