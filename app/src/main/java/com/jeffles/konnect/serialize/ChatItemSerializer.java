package com.jeffles.konnect.serialize;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.jeffles.konnect.ChatItem;

import java.lang.reflect.Type;

public class ChatItemSerializer implements JsonSerializer<ChatItem> {
    @Override
    public JsonElement serialize(ChatItem src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();

        obj.addProperty("sender", src.getSender());
        obj.addProperty("timeStamp", src.getTimeStamp().toString());
        obj.addProperty("priority", new Gson().toJson(src.getPriority()));
        obj.addProperty("message", src.getMessage());
        return obj;
    }
}
