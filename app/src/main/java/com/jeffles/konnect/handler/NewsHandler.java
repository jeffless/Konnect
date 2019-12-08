package com.jeffles.konnect.handler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class NewsHandler {
    private static final String TAG = "NewsHandler";

    private static final String subscriptionKey = "098d46ad537a4cc9835a79f71721dfc4";

    private static final String host = "https://centralus.api.cognitive.microsoft.com/";
    private static final String path = "/bing/v7.0/news/search";

    public static JsonObject searchNews(String searchQuery) throws Exception {
        // construct URL of search request (endpoint + query string)
        URL url = new URL(host + path + "?q=" + URLEncoder.encode(searchQuery, "UTF-8"));
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);

        // receive JSON body
        InputStream stream = connection.getInputStream();
        String response = new Scanner(stream).useDelimiter("\\A").next();
        stream.close();

        JsonParser parser = new JsonParser();
        return parser.parse(response).getAsJsonObject();
    }
}
