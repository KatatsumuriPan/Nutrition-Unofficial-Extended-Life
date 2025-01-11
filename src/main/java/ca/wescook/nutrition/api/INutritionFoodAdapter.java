package ca.wescook.nutrition.api;

import net.minecraft.item.ItemStack;

/**
 * To treat some modded items that don't extend `INutritionFood` as `INutritionFood`.
 * This is for an addon to NutritionUEL.
 */
public interface INutritionFoodAdapter {

    /**
     * Return whether the itemStack can be applied by this.
     * 
     * @param itemStack Target itemStack
     * @return Whether the itemStack can be applied
     */
    boolean canApply(ItemStack itemStack);

    /**
     * Return an INutritionFood adapted to the itemStack.
     * 
     * @param itemStack Target itemStack
     * @return INutritionFood referencing the itemStack.
     */
    INutritionFood apply(ItemStack itemStack);
}
