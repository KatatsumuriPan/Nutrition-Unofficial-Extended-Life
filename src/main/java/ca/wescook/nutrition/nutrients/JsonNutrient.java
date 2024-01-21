package ca.wescook.nutrition.nutrients;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

// This class mimics the layout of the nutrient json files
public class JsonNutrient {

    public String name;
    public String icon;
    public String color;
    public Float decay;
    public Boolean visible;
    public Boolean enabled;
    public Food food = new Food();

    public static class Food {

        public List<String> oredict = new ArrayList<>();
        public List<ItemId> items = new ArrayList<>();

        public static class ItemId {

            public String id;
            public int meta;
            public double scale = 1;

            public static class Adapter extends TypeAdapter<ItemId> {

                @Override
                public ItemId read(JsonReader reader) throws IOException {
                    if (reader.peek() == JsonToken.NULL) {
                        reader.nextNull();
                        return null;
                    }

                    ItemId idScale = new ItemId();
                    // Examples
                    // - "minecraft:carrot"
                    // - {"id": "minecraft:carrot", "scale": 2}
                    if (reader.peek() == JsonToken.BEGIN_OBJECT) {
                        reader.beginObject();
                        while (reader.hasNext()) {
                            String key = reader.nextName();
                            switch (key) {
                                case "id" -> idScale.id = reader.nextString();
                                case "meta" -> idScale.meta = reader.nextInt();
                                case "scale" -> idScale.scale = reader.nextDouble();
                                default -> throw new JsonSyntaxException("Unknown key:" + key);
                            }
                        }
                        reader.endObject();
                        if (idScale.id == null)
                            throw new JsonSyntaxException("Missing id");
                    } else {

                        // Examples of idAndMeta
                        // - dirt
                        // - harvestcraft:biscuititem
                        // - actuallyadditions:item_food:2
                        String idAndMeta = reader.nextString();
                        if (StringUtils.countMatches(idAndMeta, ":") == 2) {
                            idScale.id = StringUtils.substringBeforeLast(idAndMeta, ":");
                            String metaString = StringUtils.substringAfterLast(idAndMeta, ":");
                            if (NumberUtils.isCreatable(metaString))
                                idScale.meta = Integer.decode(metaString);
                            else
                                throw new JsonSyntaxException(idAndMeta + " does not contain valid metadata");
                        } else {
                            idScale.id = idAndMeta;
                        }
                    }
                    return idScale;
                }

                @Override
                public void write(JsonWriter writer, ItemId value) throws IOException {
                    if (value == null) {
                        writer.nullValue();
                        return;
                    }
                    if (value.scale == 1) {
                        if (value.meta == 0)
                            writer.value(value.id);
                        else
                            writer.value(value.id + ":" + value.meta);
                    } else {
                        writer.beginObject();
                        writer.name("id").value(value.id);
                        if (value.meta != 0)
                            writer.name("meta").value(value.meta);
                        if (value.scale != 1)
                            writer.name("scale").value(value.scale);
                        writer.endObject();
                    }
                }
            }
        }
    }
}
