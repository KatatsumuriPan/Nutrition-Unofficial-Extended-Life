package ca.wescook.nutrition.nutrients;

import net.minecraft.item.ItemStack;

// Holds food hints (isValidFood, healAmount) for nutrition calculation.
public class FoodHint {

    public ItemStack itemStack;
    public boolean isValidFood; // False if the ItemStack is not food.
    public float healAmount;   // Healing hunger amount.
                               // This takes precedence over the actual healAmount.
                               // (This is only used for the nutrition calculation, not for actual hunger recovery.)

    public boolean isTarget(ItemStack itemStack) {
        return this.itemStack.isItemEqual(itemStack);
    }
}
