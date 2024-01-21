package ca.wescook.nutrition.events;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.StringJoiner;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import ca.wescook.nutrition.Tags;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientUtils;

public class EventTooltip {

    @SubscribeEvent
    public void tooltipEvent(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();

        // Get out if not a food item
        if (!NutrientUtils.isValidFood(itemStack))
            return;

        Multimap<Float, Nutrient> nutritionValue2Nutrient = ArrayListMultimap.create();
        for (Entry<Nutrient, Float> entry : NutrientUtils.calculateNutrition(itemStack, event.getEntityPlayer())
                .entrySet()) {
            nutritionValue2Nutrient.put(entry.getValue(), entry.getKey());
        }
        for (Float key : nutritionValue2Nutrient.keySet()) {
            event.getToolTip().add(createTooltip(key, nutritionValue2Nutrient.get(key)));
        }
    }

    private static String createTooltip(float nutritionValue, Collection<Nutrient> nutrients) {
        StringJoiner stringJoiner = new StringJoiner(", ");
        for (Nutrient nutrient : nutrients) // Loop through nutrients from food
        {
            if (nutrient.visible)
                stringJoiner.add(I18n.format("nutrient." + Tags.MODID + ":" + nutrient.name));
        }
        String nutrientString = stringJoiner.toString();
        return I18n.format("tooltip." + Tags.MODID + ":nutrients") + " " + TextFormatting.DARK_GREEN + nutrientString +
                TextFormatting.DARK_AQUA + " (" + String.format("%.1f", nutritionValue) + "%)";
    }
}
