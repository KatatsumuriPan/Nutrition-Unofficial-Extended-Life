package ca.wescook.nutrition.nutrients;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import ca.wescook.nutrition.api.INutritionFood;
import ca.wescook.nutrition.api.INutritionFoodAdapter;
import ca.wescook.nutrition.api.NutritionUtil;

public class NutritionAdapterManager {

    private static final List<INutritionFoodAdapter> ADAPTERS = new ArrayList<>();

    /**
     * Use {@link NutritionUtil#register(INutritionFoodAdapter)}.
     */
    @Deprecated
    public static void register(INutritionFoodAdapter adapter) {
        ADAPTERS.add(adapter);
    }

    @Nullable
    public static INutritionFood apply(ItemStack itemStack) {
        for (INutritionFoodAdapter adapter : ADAPTERS) {
            if (adapter.canApply(itemStack))
                return adapter.apply(itemStack);
        }
        return null;
    }
}
