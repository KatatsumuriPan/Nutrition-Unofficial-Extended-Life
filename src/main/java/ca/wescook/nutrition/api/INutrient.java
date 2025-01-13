package ca.wescook.nutrition.api;

import net.minecraft.item.ItemStack;

/**
 * Nutrient API.
 * Don't create a class that implements {@link INutrient}.
 */
public interface INutrient {

    /**
     * Return the name of the nutrient (dairy, fruit, grain, protein, vegetable).
     *
     * @return Name string
     */
    String getName();

    /**
     * Return the icon for GUI.
     *
     * @return ItemStack (Don't change the instance)
     */
    ItemStack getIcon();

    /**
     * Return the packed color (a, r, g, b) for GUI.
     *
     * @return Packed color int (a is always 0xFF)
     */
    int getColor();

    /**
     * Return whether the nutrient is contained in the itemStack.
     *
     * @param itemStack ItemStack (won't be changed in this)
     * @return Whether the nutrient is contained in the itemStack
     */
    boolean isContainedIn(ItemStack itemStack);
}
