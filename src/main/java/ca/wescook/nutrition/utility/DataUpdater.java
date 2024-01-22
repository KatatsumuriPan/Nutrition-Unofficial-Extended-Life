package ca.wescook.nutrition.utility;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import ca.wescook.nutrition.Tags;
import ca.wescook.nutrition.nutrients.JsonNutrient;
import ca.wescook.nutrition.nutrients.JsonNutrient.Food.ItemId;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.Nutrient.ScaledItemStack;

public class DataUpdater {

    private static final Gson gson = new GsonBuilder().registerTypeAdapter(ItemId.class, new ItemId.Adapter())
            .enableComplexMapKeySerialization().setPrettyPrinting().create();

    public static void add(Nutrient nutrient, ScaledItemStack scaledItemStack) {
        File nutrientFile = new File(new File(Config.configDirectory, Tags.MODID + "/nutrients"),
                nutrient.name + ".json");
        try {
            JsonReader jsonReader = new JsonReader(new FileReader(nutrientFile)); // Read in JSON
            JsonNutrient jsonNutrient = gson.fromJson(jsonReader, JsonNutrient.class);
            {
                ItemId itemId = new ItemId();
                itemId.id = scaledItemStack.itemStack.getItem().getRegistryName().toString();
                itemId.meta = scaledItemStack.itemStack.getMetadata();
                itemId.scale = scaledItemStack.scale;
                jsonNutrient.food.items.add(itemId);
                Collections.sort(jsonNutrient.food.items, (a, b) -> CompareUtil.compareTo(a.id, b.id));
            }
            String json = toJson(jsonNutrient);
            FileUtils.writeByteArrayToFile(nutrientFile, json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void edit(Nutrient nutrient, ScaledItemStack scaledItemStack) {
        File nutrientFile = new File(new File(Config.configDirectory, Tags.MODID + "/nutrients"),
                nutrient.name + ".json");
        try {
            JsonReader jsonReader = new JsonReader(new FileReader(nutrientFile)); // Read in JSON
            JsonNutrient jsonNutrient = gson.fromJson(jsonReader, JsonNutrient.class);
            {
                Optional<ItemId> first = jsonNutrient.food.items.stream().filter(itemId -> {
                    return itemId.id.equals(scaledItemStack.itemStack.getItem().getRegistryName().toString()) &&
                            itemId.getMeta() == scaledItemStack.itemStack.getMetadata();
                }).findFirst();
                if (first.isPresent()) {
                    ItemId itemId = first.get();
                    itemId.scale = scaledItemStack.scale;
                }
            }
            String json = toJson(jsonNutrient);
            FileUtils.writeByteArrayToFile(nutrientFile, json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void remove(Nutrient nutrient, ScaledItemStack scaledItemStack) {
        File nutrientFile = new File(new File(Config.configDirectory, Tags.MODID + "/nutrients"),
                nutrient.name + ".json");
        try {
            JsonReader jsonReader = new JsonReader(new FileReader(nutrientFile)); // Read in JSON
            JsonNutrient jsonNutrient = gson.fromJson(jsonReader, JsonNutrient.class);
            {
                jsonNutrient.food.items.removeIf(itemId -> {
                    return itemId.id.equals(scaledItemStack.itemStack.getItem().getRegistryName().toString()) &&
                            itemId.getMeta() == scaledItemStack.itemStack.getMetadata();
                });
            }
            String json = toJson(jsonNutrient);
            FileUtils.writeByteArrayToFile(nutrientFile, json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String toJson(Object src) throws JsonIOException {
        StringWriter writer = new StringWriter();
        try {
            JsonWriter jsonWriter = gson.newJsonWriter(Streams.writerForAppendable(writer));
            jsonWriter.setIndent("\t");
            gson.toJson(src, src.getClass(), jsonWriter);
            return writer.toString();
        } catch (IOException e) {
            throw new JsonIOException(e);
        }
    }
}
