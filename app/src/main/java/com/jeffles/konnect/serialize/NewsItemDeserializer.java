package com.jeffles.konnect.serialize;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.jeffles.konnect.NewsItem;

import java.lang.reflect.Type;

public class NewsItemDeserializer implements JsonDeserializer<NewsItem> {
    @Override
    public NewsItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject input = json.getAsJsonObject();

        return new NewsItem(input.get("articleProvider").getAsString(),
                input.get("datePublished").getAsString(),
                input.get("headline").getAsString(),
                input.get("url").getAsString(),
                input.get("article").getAsString());
    }
}
