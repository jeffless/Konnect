package com.jeffles.konnect;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.joda.time.DateTime;

import java.lang.reflect.Type;

public class NewsWrapperDeserializer implements JsonDeserializer<NewsWrapper> {
    @Override
    public NewsWrapper deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject input = json.getAsJsonObject();

        DateTime timeStamp = new DateTime(input.get("timeStamp").getAsString());

        NewsWrapper result = new NewsWrapper(timeStamp);

        JsonArray newsItems = input.get("newsItems").getAsJsonArray();
        for (JsonElement newsItem : newsItems) {
            JsonObject newsObject = newsItem.getAsJsonObject();

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(NewsItem.class, new NewsItemDeserializer())
                    .create();

            result.addNewsItem(gson.fromJson(newsObject, NewsItem.class));
        }

        return result;
    }
}
