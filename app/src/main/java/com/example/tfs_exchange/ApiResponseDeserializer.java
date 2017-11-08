package com.example.tfs_exchange;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

/**
 * Created by pusya on 07.11.17.
 * При десериализации получаем из JSON base, дату, из entrySet собираем RateObject, все вместе кладем в конструктор ApiResponse
 */

public class ApiResponseDeserializer implements JsonDeserializer<ApiResponse> {
    @Override
    public ApiResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        ApiResponse api = null;
        RateObject rateObject = null;
        String base;
        String date;
        if (json.isJsonObject()) {
            base = json.getAsJsonObject().get("base").getAsString();
            date = json.getAsJsonObject().get("date").getAsString();
            Set<Map.Entry<String, JsonElement>> entries = json.getAsJsonObject().get("rates").getAsJsonObject().entrySet();
            if (entries.size() > 0) {
                Map.Entry<String, JsonElement> entry = entries.iterator().next();
                rateObject = new RateObject(entry.getKey(), entry.getValue().getAsDouble());
            }
            api = new ApiResponse(base, date, rateObject);
        }
        return api;
    }
}
