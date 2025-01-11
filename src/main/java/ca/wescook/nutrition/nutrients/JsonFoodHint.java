package ca.wescook.nutrition.nutrients;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

// This class mimics the layout of the foodHint json file
public class JsonFoodHint {

    public List<FoodHintRaw> hints = new ArrayList<>();

    public static class FoodHintRaw {

        public String id;
        public @Nullable Integer meta = null;
        public boolean isValidFood = true;
        public double healAmount = 0;

        public int getMeta() {
            return meta == null ? 0 : meta;
        }

        public static class Adapter extends TypeAdapter<FoodHintRaw> {

            @Override
            public FoodHintRaw read(JsonReader reader) throws IOException {
                if (reader.peek() == JsonToken.NULL) {
                    reader.nextNull();
                    return null;
                }

                FoodHintRaw foodHintRaw = new FoodHintRaw();
                // Examples
                // - {"id": "minecraft:carrot", "healAmount": 2}
                // - {"id": "minecraft:fish", "meta": 1, "healAmount": 100}
                // - {"id": "minecraft:fish", "meta": 3, "isValidFood": false}
                reader.beginObject();
                while (reader.hasNext()) {
                    String key = reader.nextName();
                    switch (key) {
                        case "id" -> foodHintRaw.id = reader.nextString();
                        case "meta" -> foodHintRaw.meta = reader.nextInt();
                        case "isValidFood" -> foodHintRaw.isValidFood = reader.nextBoolean();
                        case "healAmount" -> foodHintRaw.healAmount = reader.nextDouble();
                        default -> throw new JsonSyntaxException("Unknown key:" + key);
                    }
                }
                reader.endObject();
                if (foodHintRaw.id == null)
                    throw new JsonSyntaxException("Missing id");
                return foodHintRaw;
            }

            @Override
            public void write(JsonWriter writer, FoodHintRaw value) throws IOException {
                if (value == null) {
                    writer.nullValue();
                    return;
                }
                writer.beginObject();
                writer.name("id").value(value.id);
                if (value.meta != null)
                    writer.name("meta").value(value.meta);
                if (!value.isValidFood)
                    writer.name("isValidFood").value(false);
                if (value.healAmount != 1)
                    writer.name("healAmount").value(value.healAmount);
                writer.endObject();
            }
        }
    }
}
