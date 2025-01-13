package ca.wescook.nutrition.api;

import net.minecraft.item.ItemStack;

import ca.wescook.nutrition.nutrients.NutrientItemEntry;

/**
 * For registering an item added by a mod to a nutrient.
 * Don't create a class that implements {@link INutrientItemEntry}.
 */
public interface INutrientItemEntry {

    /**
     * Create a new instance of {@link INutrientItemEntry}
     *
     * @param itemStack ItemStack
     * @return INutrientItemEntry instance
     */
    static INutrientItemEntry create(ItemStack itemStack) {
        return new NutrientItemEntry(itemStack.copy());
    }

    /**
     * Set {@link ItemStackCompareType} to specify how to compare itemStacks.
     *
     * @param compareType ItemStackCompareType
     * @return This instance
     */
    INutrientItemEntry setCompareType(ItemStackCompareType compareType);

    /**
     * Set nutrient scale (same as one in config).
     *
     * @param scale Nutrient scale value
     * @return This instance
     */
    INutrientItemEntry setScale(float scale);
}
