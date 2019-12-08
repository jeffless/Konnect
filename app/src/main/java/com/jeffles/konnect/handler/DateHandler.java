package com.jeffles.konnect.handler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.joda.time.DateTime;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class DateHandler {
    public static DateTime getTime() throws Exception {
        URL url = new URL("http://worldclockapi.com/api/json/utc/now");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // receive JSON body
        InputStream stream = connection.getInputStream();
        String response = new Scanner(stream).useDelimiter("\\A").next();
        stream.close();

        JsonParser parser = new JsonParser();
        JsonObject resultObject = parser.parse(response).getAsJsonObject();
        String time = resultObject.get("currentDateTime").getAsString();

        return new DateTime(time);
    }
}
