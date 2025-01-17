package ca.wescook.nutrition.compat.ic2;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

import org.jetbrains.annotations.Nullable;

import ca.wescook.nutrition.api.INutritionFood;
import ca.wescook.nutrition.api.INutritionFoodAdapter;
import ca.wescook.nutrition.api.NutrientApplicationPhase;
import ca.wescook.nutrition.api.NutritionUtil;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;

public class CompatIC2 {

    public static void init() {
        if (!Loader.isModLoaded("ic2"))
            return;
        NutritionUtil.register(new Adapter());
    }

    private static class Adapter implements INutritionFoodAdapter {

        @Override
        public boolean canApply(ItemStack itemStack) {
            return ItemStack.areItemsEqual(itemStack, IC2Items.getItem("filled_tin_can"));
        }

        @Override
        public INutritionFood apply(ItemStack itemStack) {
            return FilledTinCan.INSTANCE;
        }

        private static class FilledTinCan implements INutritionFood {

            public static final FilledTinCan INSTANCE = new FilledTinCan();

            private FilledTinCan() {}

            @Override
            public int getHealAmount(ItemStack itemStack, @Nullable EntityPlayer player) {
                if (player == null)
                    return 1;
                return Math.min(itemStack.getCount(), 20 - player.getFoodStats().getFoodLevel());
            }

            @Override
            public void setAlwaysEdible(ItemStack itemStack, @Nullable EntityPlayer player) {
                // Can't make always-edible
            }

            @Override
            public NutrientApplicationPhase getNutrientApplicationPhase(ItemStack itemStack) {
                return NutrientApplicationPhase.ON_RIGHT_CLICK;
            }
        }
    }
}
