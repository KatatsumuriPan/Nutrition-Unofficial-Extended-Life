package ca.wescook.nutrition.api;

import net.minecraft.item.ItemStack;

/**
 * Nutrient API.
 * Don't create a class that implements INutrition.
 */
public interface INutrient {

    String getName();

    ItemStack getIcon();

    int getColor();
}
