package ca.wescook.nutrition.nutrients;

import net.minecraft.item.ItemStack;

import ca.wescook.nutrition.api.INutrientItemEntry;
import ca.wescook.nutrition.api.ItemStackCompareType;

/**
 * FOR INTERNAL USE
 * Implementation of {@link INutrientItemEntry}.
 */
public class NutrientItemEntry implements INutrientItemEntry {

    // Food item
    public final ItemStack itemStack;
    // How to compare ItemStacks
    public ItemStackCompareType compareType = ItemStackCompareType.DEFAULT;
    // Nutrient scale (same as one in config)
    public float scale = 1;

    public NutrientItemEntry(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public INutrientItemEntry setCompareType(ItemStackCompareType compareType) {
        this.compareType = compareType;
        return this;
    }

    @Override
    public INutrientItemEntry setScale(float scale) {
        this.scale = scale;
        return this;
    }
}
