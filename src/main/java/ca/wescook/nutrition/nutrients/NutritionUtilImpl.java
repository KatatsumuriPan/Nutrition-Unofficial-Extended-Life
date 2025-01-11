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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.primitives.Floats;

import ca.wescook.nutrition.api.INutrient;
import ca.wescook.nutrition.api.INutritionFood;
import ca.wescook.nutrition.api.NutritionUtil;
import ca.wescook.nutrition.capabilities.INutrientManager;
import ca.wescook.nutrition.nutrients.Nutrient.ScaledItemStack;
import ca.wescook.nutrition.proxy.ClientProxy;
import ca.wescook.nutrition.utility.Config;

/***
 * Don't use the class from other mods. (Use {@link NutritionUtil}.)
 * This may change between different versions.
 */
public class NutritionUtilImpl {

    @CapabilityInject(INutrientManager.class)
    private static final Capability<INutrientManager> NUTRITION_CAPABILITY = null;

    // Calculate nutrition value for supplied food.
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
        Float healAmount = FoodHintList.getHealAmount(itemStack);
        if (healAmount != null)
            return healAmount;
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
        float lossRatio = getLossRatio(nutritionValues.size());
        nutritionValues.replaceAll((nutrition, nutritionValue) -> nutritionValue * (1 - lossRatio));
    }

    /**
     * Lose 15% (configurable) for each nutrient added after the first nutrient
     * Examples
     * - [Grain] only -> 0% loss
     * - [Protain, Vegetable] -> 15% loss
     * - [Dairy, Fruit, Protain] -> 30% loss
     * Max loss is 100%. XD
     *
     * @param nutritionNum Number of nutritions
     * @return Nutrition loss
     */
    private static float getLossRatio(int nutritionNum) {
        return Math.min(1, (float) Config.lossPerNutrient / 100f * (nutritionNum - 1));
    }

    // Verify it meets a valid type
    // Little bit of guesswork in this one...
    public static boolean isValidFood(ItemStack itemStack) {
        // FoodHint
        Boolean result = FoodHintList.isValidFood(itemStack);
        if (result != null)
            return result;

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

    // Add nutrition values of the itemStack to the player.
    public static boolean addNutrientsToPlayer(EntityPlayer player, ItemStack itemStack) {
        // Get out if not food item
        if (!NutritionUtil.isValidFood(itemStack))
            return false;

        // Calculate nutrition
        Map<Nutrient, Float> nutrientValues = NutritionUtilImpl.calculateNutrition(itemStack, player);

        // Add to each nutrient
        INutrientManager nutrientManager = getNutrientManager(player);
        nutrientManager.add(nutrientValues);
        return true;
    }

    // Get nutrient value of the player.
    public static float getNutrient(EntityPlayer player, INutrient nutrient) {
        INutrientManager nutrientManager = getNutrientManager(player);
        return nutrientManager.get((Nutrient) nutrient);
    }

    // Set nutrient value to the player.
    public static void setNutrient(EntityPlayer player, INutrient nutrient, float value) {
        INutrientManager nutrientManager = getNutrientManager(player);
        nutrientManager.set((Nutrient) nutrient, Floats.constrainToRange(value, 0, 100));
    }

    // Add nutrient value to the player.
    public static void addNutrient(EntityPlayer player, INutrient nutrient, float amount) {
        INutrientManager nutrientManager = getNutrientManager(player);
        nutrientManager.add((Nutrient) nutrient, amount); // negative allowed
    }

    // Reset nutrient value of the player.
    public static void resetNutrient(EntityPlayer player, INutrient nutrient) {
        INutrientManager nutrientManager = getNutrientManager(player);
        nutrientManager.reset((Nutrient) nutrient);
    }

    private static INutrientManager getNutrientManager(EntityPlayer player) {
        INutrientManager nutrientManager;
        if (!player.getEntityWorld().isRemote) {// Server
            nutrientManager = player.getCapability(NUTRITION_CAPABILITY, null);
            if (nutrientManager == null)
                throw new IllegalStateException("Player \"" + player.getName() + "\" doesn't have the capability!");
        } else {// Client
            nutrientManager = ClientProxy.localNutrition;
            if (nutrientManager == null)
                throw new IllegalStateException("ClientProxy.localNutrition is null");
        }
        return nutrientManager;
    }
}
