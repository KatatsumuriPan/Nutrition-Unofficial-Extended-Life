package ca.wescook.nutrition.nutrients;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

// Nutrient object represents a type of food group
public class Nutrient {

    public String name;
    public ItemStack icon;
    public int color;
    public float decay;
    public boolean visible;
    public List<String> foodOreDict = new ArrayList<>();
    public List<ScaledItemStack> foodItems = new ArrayList<>();

    public static class ScaledItemStack {

        public final ItemStack itemStack;
        public final float scale;

        public ScaledItemStack(ItemStack itemStack, float scale) {
            this.itemStack = itemStack;
            this.scale = scale;
        }
    }
}
