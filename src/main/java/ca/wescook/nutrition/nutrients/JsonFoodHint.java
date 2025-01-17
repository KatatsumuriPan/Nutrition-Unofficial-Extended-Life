package ca.wescook.nutrition.nutrients;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import ca.wescook.nutrition.api.NutrientApplicationPhase;

// This class mimics the layout of the foodHint json file
public class JsonFoodHint {

    public List<FoodHintRaw> hints = new ArrayList<>();

    public static class FoodHintRaw {

        public String id;
        public @Nullable Integer meta = null;
        public boolean isValidFood = true;
        public double healAmount = 0;
        public NutrientApplicationPhase nutrientApplicationPhase = NutrientApplicationPhase.FINISH_USING;

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
                // - {"id": "minecraft:carrot", "heal_amount": 2}
                // - {"id": "minecraft:fish", "meta": 1, "heal_amount": 100}
                // - {"id": "minecraft:fish", "meta": 3, "is_valid_food": false}
                reader.beginObject();
                while (reader.hasNext()) {
                    String key = reader.nextName();
                    switch (key) {
                        case "id" -> foodHintRaw.id = reader.nextString();
                        case "meta" -> foodHintRaw.meta = reader.nextInt();
                        case "is_valid_food", "isValidFood" -> foodHintRaw.isValidFood = reader.nextBoolean();
                        case "heal_amount", "healAmount" -> foodHintRaw.healAmount = reader.nextDouble();
                        case "nutrient_application_phase" -> foodHintRaw.nutrientApplicationPhase = Enum
                                .valueOf(NutrientApplicationPhase.class, reader.nextString().toUpperCase(Locale.ROOT));
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
                    writer.name("is_valid_food").value(false);
                if (value.healAmount != 1)
                    writer.name("heal_amount").value(value.healAmount);
                if (value.nutrientApplicationPhase != NutrientApplicationPhase.FINISH_USING)
                    writer.name("nutrient_application_phase")
                            .value(value.nutrientApplicationPhase.name().toLowerCase(Locale.ROOT));
                writer.endObject();
            }
        }
    }
}
