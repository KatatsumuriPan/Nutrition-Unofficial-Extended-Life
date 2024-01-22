package ca.wescook.nutrition.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

import ca.wescook.nutrition.Tags;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.Nutrient.ScaledItemStack;
import ca.wescook.nutrition.nutrients.NutrientList;
import ca.wescook.nutrition.nutrients.NutrientUtils;
import ca.wescook.nutrition.utility.DataUpdater;

public class CommandEditNutrition extends CommandBase {

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getName() {
        return "nutrition-food";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return getName() + " [add|remove <nutrient> [...]]";
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
                                          @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "add", "remove");
        }
        if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args,
                    NutrientList.get().stream().map(nutrient -> nutrient.name).collect(Collectors.toList()));
        }
        return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayer player) {
            ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);
            if (!NutrientUtils.isValidFood(heldItem)) {
                throw new CommandException("Holding item is not food!");
            }
            switch (args.length) {
                case 0 -> sender.sendMessage(new TextComponentString(createInfo(heldItem, player)));
                case 2, 3 -> {
                    String type = args[0];
                    switch (type) {
                        case "add" -> {
                            Nutrient nutrient = getNutrient(args);
                            if (args.length == 2) {
                                if (nutrient.foodItems.stream().anyMatch(s -> s.itemStack.isItemEqual(heldItem)))
                                    throw new CommandException(
                                            args[1] + " is already added to " + heldItem.getItem().getRegistryName() +
                                                    "!");
                                nutrient.foodItems.add(new ScaledItemStack(heldItem, 1));
                                DataUpdater.add(nutrient, new ScaledItemStack(heldItem, 1));
                                sender.sendMessage(new TextComponentString(
                                        args[1] + " is added to " + heldItem.getItem().getRegistryName()));
                            } else {
                                float scale = (float) parseDouble(args[2], 0);
                                int index = Iterables.indexOf(nutrient.foodItems,
                                        s -> s.itemStack.isItemEqual(heldItem));
                                ScaledItemStack scaledItemStack = new ScaledItemStack(heldItem, scale);
                                if (index >= 0) {
                                    nutrient.foodItems.set(index, scaledItemStack);
                                    DataUpdater.edit(nutrient, scaledItemStack);
                                } else {
                                    nutrient.foodItems.add(scaledItemStack);
                                    DataUpdater.add(nutrient, scaledItemStack);
                                }
                            }
                        }
                        case "remove" -> {
                            Nutrient nutrient = getNutrient(args);
                            Optional<ScaledItemStack> first = nutrient.foodItems.stream()
                                    .filter(s -> s.itemStack.isItemEqual(heldItem)).findFirst();
                            if (!first.isPresent())
                                throw new CommandException(
                                        heldItem.getItem().getRegistryName() + "doesn't have " + args[1]);
                            nutrient.foodItems.remove(first.get());
                            DataUpdater.remove(nutrient, first.get());
                            sender.sendMessage(new TextComponentString(
                                    args[1] + " is removed from " + heldItem.getItem().getRegistryName()));
                        }
                    }
                }
                default -> throw new WrongUsageException(getUsage(sender));
            }
        }
    }

    @NotNull
    private static Nutrient getNutrient(String[] args) throws CommandException {
        if (args[1].isEmpty()) {
            switch (args[0]) {
                case "add" -> throw new WrongUsageException("add <nutrient> [<scale>]");
                case "remove" -> throw new WrongUsageException("remove <nutrient>");
            }
        }
        Nutrient nutrient = NutrientList.getByName(args[1]);
        if (nutrient == null)
            throw new CommandException("Unknown nutrient:" + args[1]);
        return nutrient;
    }

    private static String createInfo(ItemStack itemStack, EntityPlayer player) {
        Multimap<Float, Nutrient> nutritionValue2Nutrient = ArrayListMultimap.create();
        for (Entry<Nutrient, Float> entry : NutrientUtils.calculateNutrition(itemStack, player)
                .entrySet()) {
            nutritionValue2Nutrient.put(entry.getValue(), entry.getKey());
        }
        List<String> list = new ArrayList<>();
        for (Float key : nutritionValue2Nutrient.keySet()) {
            list.add(createTooltip(key, nutritionValue2Nutrient.get(key)));
        }
        if (list.isEmpty())
            return itemStack.getItem().getRegistryName() + " has no nutrients";
        return StringUtils.join(list, ", ");
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
