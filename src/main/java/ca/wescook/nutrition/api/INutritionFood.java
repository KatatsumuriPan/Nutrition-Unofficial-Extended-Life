package ca.wescook.nutrition.api;

import net.minecraft.item.ItemStack;

public interface INutritionFood {

    int getHealAmount(ItemStack itemStack);

    void setAlwaysEdible();
}
