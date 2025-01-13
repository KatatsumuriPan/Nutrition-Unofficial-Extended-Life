package ca.wescook.nutrition.nutrients;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.Iterables;

import ca.wescook.nutrition.api.INutrient;
import ca.wescook.nutrition.api.INutrientItemEntry;
import ca.wescook.nutrition.api.ItemStackCompareType;
import ca.wescook.nutrition.utility.Log;

// Nutrient object represents a type of food group
public class Nutrient implements INutrient {

    public String name;
    public ItemStack icon;
    public int color;
    public float decay;
    public boolean visible;
    public List<String> foodOreDict = new ArrayList<>();
    public List<ScaledItemStack> foodItems = new ArrayList<>();

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ItemStack getIcon() {
        return icon;
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public boolean isContainedIn(ItemStack itemStack) {
        // Search foods
        for (ScaledItemStack listedFood : foodItems) {
            if (listedFood.isMatch(itemStack))
                return true;
        }

        // Search ore dictionary
        for (String listedOreDict : foodOreDict) {
            // Example
            // - listAllmilk
            for (ItemStack itemStack1 : OreDictionary.getOres(listedOreDict)) {
                if (itemStack1.isItemEqual(itemStack))
                    return true;
            }
        }
        return false;
    }

    @Override
    public void registerFoodItem(INutrientItemEntry itemEntry) {
        NutrientItemEntry impl = (NutrientItemEntry) itemEntry;
        if (!addOrReplaceScaledItemStack(
                new ScaledItemStack(impl.itemStack, impl.scale, impl.compareType)))
            Log.warn(impl.itemStack.getDisplayName() + " is duplicated in " + getName());
    }

    @Nullable
    public Float getNutrientScale(ItemStack itemStack) {
        // Search foods
        for (ScaledItemStack listedFood : foodItems) {
            if (!listedFood.isMatch(itemStack))
                continue;
            // Remains are skipped.
            // (Only the first element is applied if it has duplicated ones.)
            return listedFood.scale;
        }

        // Search ore dictionary
        for (String listedOreDict : foodOreDict) {
            // Example
            // - listAllmilk
            for (ItemStack itemStack1 : OreDictionary.getOres(listedOreDict)) {
                if (!itemStack1.isItemEqual(itemStack))
                    continue;

                return 1.0F;
            }
        }
        return null;
    }

    /**
     * Return the first ScaledItemStack that matches to the itemStack.
     *
     * @param itemStack Food item
     * @return ScaledItemStack (nullable).
     */
    @Nullable
    public ScaledItemStack getScaledItemStack(ItemStack itemStack) {
        for (ScaledItemStack listedFood : foodItems) {
            if (listedFood.isMatch(itemStack))
                return listedFood;
        }
        return null;
    }

    /**
     * Add the food item to the nutrient.
     *
     * @param scaledItemStack Food item
     */
    public void addScaledItemStack(ScaledItemStack scaledItemStack) {
        foodItems.add(scaledItemStack);
    }

    /**
     * Add or replace the food item to the nutrient.
     *
     * @param scaledItemStack Food item
     * @return True: added, false: replaced
     */
    public boolean addOrReplaceScaledItemStack(ScaledItemStack scaledItemStack) {
        int index = Iterables.indexOf(foodItems, s -> s.isMatch(scaledItemStack.itemStack));
        if (index >= 0) {
            foodItems.set(index, scaledItemStack);
            return false;
        } else {
            foodItems.add(scaledItemStack);
            return true;
        }
    }

    /**
     * Remove the food item to the nutrient.
     *
     * @param itemStack Food item
     * @return True: added, false: replaced
     */
    @Nullable
    public ScaledItemStack removeScaledItemStack(ItemStack itemStack) {
        for (int i = 0; i < foodItems.size(); i++) {
            ScaledItemStack listedFood = foodItems.get(i);
            if (listedFood.isMatch(itemStack)) {
                foodItems.remove(i);
                return listedFood;
            }
        }
        return null;
    }

    public static class ScaledItemStack {

        public final ItemStack itemStack;
        public final float scale;
        public final ItemStackCompareType compareType;

        public ScaledItemStack(ItemStack itemStack, float scale) {
            this(itemStack, scale, ItemStackCompareType.META_SENSITIVE);
        }

        public ScaledItemStack(ItemStack itemStack, float scale, ItemStackCompareType compareType) {
            this.itemStack = itemStack;
            this.scale = scale;
            this.compareType = compareType;
        }

        public boolean isMatch(ItemStack itemStack) {
            return switch (compareType) {
                case DEFAULT -> !itemStack.isEmpty() && this.itemStack.getItem() == itemStack.getItem();
                case META_SENSITIVE -> this.itemStack.isItemEqual(itemStack);
                case ONLY_NBT_SENSITIVE -> ItemStack.areItemStackTagsEqual(this.itemStack, itemStack);
                case ALL_SENSITIVE -> this.itemStack.isItemEqual(itemStack) &&
                        ItemStack.areItemStackTagsEqual(this.itemStack, itemStack);
            };
        }
    }
}
