package com.jeffles.konnect;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class NewsWrapperSerializer implements JsonSerializer<NewsWrapper>{
    @Override
    public JsonElement serialize(NewsWrapper src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("timeStamp", src.getTimeStamp().toString());

        JsonArray newsItems = new JsonArray();
        for (NewsItem item : src.getNewsItems()) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(NewsItem.class, new NewsItemSerializer())
                    .create();
            String itemJsonString = gson.toJson(item);
            newsItems.add(gson.fromJson(itemJsonString, JsonObject.class));
        }
        obj.add("newsItems", newsItems);

        return obj;
    }
}
