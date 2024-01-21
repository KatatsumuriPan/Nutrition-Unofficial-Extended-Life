package ca.wescook.nutrition.api;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * For items not extends {@link net.minecraft.item.ItemFood ItemFood}.
 * You can use this to items extending {@link net.minecraft.item.ItemFood ItemFood} and get {@link EntityPlayer}.
 */
public interface INutritionFood {

    /**
     * Get healing level of the food.
     * 
     * @param itemStack Stack of eating item.
     *                  You can get meta and NBT from it.
     * @param player    Eating player.
     * @return Healing food level. (Not saturation level.)
     */
    int getHealAmount(ItemStack itemStack, @Nullable EntityPlayer player);

    /**
     * Force the food always-edible.
     * The food will be edible even if the player don't need to eat.
     * 
     * @param itemStack Stack of eating item.
     *                  * You can get meta and NBT from it.
     * @param player    Player that will eat this.
     */
    void setAlwaysEdible(ItemStack itemStack, @Nullable EntityPlayer player);
}
