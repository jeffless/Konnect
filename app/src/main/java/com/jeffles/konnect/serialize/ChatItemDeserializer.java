package com.jeffles.konnect.serialize;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.jeffles.konnect.ChatItem;

import org.joda.time.DateTime;

import java.lang.reflect.Type;

public class ChatItemDeserializer implements JsonDeserializer<ChatItem> {
    @Override
    public ChatItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject input = json.getAsJsonObject();

        DateTime timeStamp = new DateTime(input.get("timeStamp").getAsString());

        return new ChatItem(input.get("sender").getAsString(), timeStamp,
                new Gson().fromJson(input.get("priority"), ChatItem.Priority.class),
                input.get("message").getAsString());
    }
}
