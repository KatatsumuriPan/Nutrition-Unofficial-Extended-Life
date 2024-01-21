package ca.wescook.nutrition.api;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface INutritionFood {

    int getHealAmount(ItemStack itemStack, @Nullable EntityPlayer player);

    void setAlwaysEdible(ItemStack itemStack, @Nullable EntityPlayer player);
}
