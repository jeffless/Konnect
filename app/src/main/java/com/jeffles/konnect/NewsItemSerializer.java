package com.jeffles.konnect;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class NewsItemSerializer implements JsonSerializer<NewsItem> {
    @Override
    public JsonElement serialize(NewsItem src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();

        obj.addProperty("articleProvider", src.getArticleProvider());
        obj.addProperty("datePublished", src.getDatePublished().toString());
        obj.addProperty("headline", src.getHeadline());
        obj.addProperty("url", src.getUrl().toString());
        obj.addProperty("article", src.getArticle());

        return obj;
    }
}
