package ca.wescook.nutrition.api;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ca.wescook.nutrition.nutrients.NutrientList;
import ca.wescook.nutrition.nutrients.NutritionUtilImpl;

/**
 * Nutrition API methods.
 */
public class NutritionUtil {

    /**
     * Calculate nutrition value for supplied food.
     *
     * @param itemStack Eating food stack.
     * @param player    Eating player(Nullable).
     * @return Nutrient and its value.
     */
    public static Map<? extends INutrient, Float> calculateNutrition(ItemStack itemStack,
                                                                     @Nullable EntityPlayer player) {
        return NutritionUtilImpl.calculateNutrition(itemStack, player);
    }

    /**
     * Return whether the itemStack is a valid food item.
     *
     * @param itemStack Food stack.
     * @return Whether the itemStack is a valid food item.
     */
    public static boolean isValidFood(ItemStack itemStack) {
        return NutritionUtilImpl.isValidFood(itemStack);
    }

    /**
     * Add nutrients of the itemStack to the player.
     *
     * @param player    Eating player.
     * @param itemStack Food stack.
     * @return The process is succeeded.
     */
    public static boolean addNutrientsToPlayer(EntityPlayer player, ItemStack itemStack) {
        return NutritionUtilImpl.addNutrientsToPlayer(player, itemStack);
    }

    /**
     * Get all nutrients.
     *
     * @return Unmodifiable list of Nutrients.
     */
    public static List<? extends INutrient> getNutrients() {
        return Collections.unmodifiableList(NutrientList.get());
    }

    /**
     * Get nutrient value of the player.
     *
     * @param player   Target player.
     * @param nutrient Nutrient to get its value.
     * @return Nutrient value.
     */
    public static float getNutrient(EntityPlayer player, INutrient nutrient) {
        return NutritionUtilImpl.getNutrient(player, nutrient);
    }

    /**
     * Set nutrient value to the player.
     *
     * @param player   Target player.
     * @param nutrient Nutrient.
     * @param value    Nutrient value. (Min: 0, Max: 100)
     */
    public static void setNutrient(EntityPlayer player, INutrient nutrient, float value) {
        NutritionUtilImpl.setNutrient(player, nutrient, value);
    }

    /**
     * Add nutrient value to the player.
     *
     * @param player   Target player.
     * @param nutrient Nutrient to add.
     * @param amount   Nutrient value to add. (negative value allowed)
     */
    public static void addNutrient(EntityPlayer player, INutrient nutrient, float amount) {
        NutritionUtilImpl.addNutrient(player, nutrient, amount);
    }

    /**
     * Reset nutrient value of the player.
     *
     * @param player   Target player.
     * @param nutrient Nutrient to reset.
     */
    public static void resetNutrient(EntityPlayer player, INutrient nutrient) {
        NutritionUtilImpl.resetNutrient(player, nutrient);
    }
}
