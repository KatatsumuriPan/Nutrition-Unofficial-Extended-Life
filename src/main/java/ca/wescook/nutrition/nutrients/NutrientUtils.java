package ca.wescook.nutrition.nutrients;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.block.BlockCake;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import ca.wescook.nutrition.api.INutritionFood;
import ca.wescook.nutrition.nutrients.Nutrient.ScaledItemStack;
import ca.wescook.nutrition.utility.Config;
import ca.wescook.nutrition.utility.Log;

public class NutrientUtils {

    /**
     * Calculate nutrition value for supplied food.
     *
     * @param itemStack Eating food stack.
     * @param player    Eating player(Nullable).
     * @return Nutrient and its value.
     */
    public static Map<Nutrient, Float> calculateNutrition(ItemStack itemStack, @Nullable EntityPlayer player) {
        Map<Nutrient, Float> result = new LinkedHashMap<>();
        for (Nutrient nutrient : NutrientList.get()) {
            Float nutrientValue = getNutritionValue(nutrient, itemStack, player);
            if (nutrientValue != null)
                result.put(nutrient, nutrientValue);
        }
        applyNutritionLoss(result);
        return result;
    }

    @Nullable
    private static Float getNutritionValue(Nutrient nutrient, ItemStack itemStack, @Nullable EntityPlayer player) {
        // Search foods
        for (ScaledItemStack listedFood : nutrient.foodItems) {
            if (!listedFood.itemStack.isItemEqual(itemStack))
                continue;

            float baseFoodValue = getBaseFoodValue(itemStack, player);
            float adjustedFoodValue = adjustFoodValue(baseFoodValue);
            // Remains are skipped.
            // (Only the first element is applied if it has duplicated ones.)
            return adjustedFoodValue * listedFood.scale;
        }

        // Search ore dictionary
        for (String listedOreDict : nutrient.foodOreDict) {
            // Example
            // - listAllmilk
            for (ItemStack itemStack1 : OreDictionary.getOres(listedOreDict)) {
                if (!itemStack1.isItemEqual(itemStack))
                    continue;

                float baseFoodValue = getBaseFoodValue(itemStack, player);
                return adjustFoodValue(baseFoodValue);
            }
        }
        return null;
    }

    private static float getBaseFoodValue(ItemStack itemStack, @Nullable EntityPlayer player) {
        Item item = itemStack.getItem();
        if (item instanceof INutritionFood)
            return ((INutritionFood) item).getHealAmount(itemStack, player);
        else if (item instanceof ItemFood)
            return ((ItemFood) item).getHealAmount(itemStack);
        else if (item instanceof ItemBlock || item instanceof ItemBlockSpecial) // Cake, most likely
            return 2; // Hardcoded value from vanilla
        else if (item instanceof ItemBucketMilk)
            return 4; // Hardcoded milk value
        else
            return 0;
    }

    private static float adjustFoodValue(float baseFoodValue) {
        float adjustedFoodValue = baseFoodValue * 0.5f; // Halve to start at reasonable starting point
        adjustedFoodValue = adjustedFoodValue * Config.nutritionMultiplier;
        return adjustedFoodValue;
    }

    private static void applyNutritionLoss(Map<Nutrient, Float> nutritionValues) {
        // Lose 15% (configurable) for each nutrient added after the first nutrient
        // Examples
        // - [Grain] only -> 0% loss
        // - [Protain, Vegetable] -> 15% loss
        // - [Dairy, Fruit, Protain] -> 30% loss
        // Max loss is 100%. XD
        float lossRatio = Math.min(1, (float) Config.lossPerNutrient / 100f * (nutritionValues.size() - 1));

        nutritionValues.replaceAll((nutrition, nutritionValue) -> nutritionValue * (1 - lossRatio));
    }

    // Verify it meets a valid type
    // Little bit of guesswork in this one...
    public static boolean isValidFood(ItemStack itemStack) {
        Item item = itemStack.getItem();

        // Regular ItemFood
        if (item instanceof ItemFood)
            return true;

        // Cake - Vanilla
        if (item instanceof ItemBlock && ((ItemBlock) item).getBlock() instanceof BlockCake)
            return true;

        // Cake - Modded
        if (item instanceof ItemBlockSpecial && ((ItemBlockSpecial) item).getBlock() instanceof BlockCake)
            return true;

        // Milk Bucket
        if (item instanceof ItemBucketMilk)
            return true;

        // INutritionFood
        if (item instanceof INutritionFood)
            return true;

        return false;
    }

    // Log all foods registered in-game without nutrients
    public static void logMissingNutrients() {
        for (Item item : Item.REGISTRY) {
            ItemStack itemStack = new ItemStack(item);
            if (isValidFood(itemStack) && calculateNutrition(itemStack, null).isEmpty())
                Log.warn("Registered food without nutrients: " + item.getRegistryName());
        }
    }
}
