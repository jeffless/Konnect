package com.jeffles.konnect;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import static com.jeffles.konnect.DateHandler.getTime;

public class NewsHandler {
    private static final String TAG = "NewsHandler";

    private static final String subscriptionKey = "12e5ab397fc043c5a6bad8fc16967cc5";

    private static final String host = "https://eastus.api.cognitive.microsoft.com/";
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

    public static void main(String[] args) {
        try {
            NewsWrapper newsWrapper = new NewsWrapper(getTime());

            JsonObject result = searchNews("world news");
            JsonArray newsArray = result.get("value").getAsJsonArray();

            for (JsonElement news : newsArray) {
                JsonObject newsObject = news.getAsJsonObject();

                JsonArray providerArray = newsObject.get("provider").getAsJsonArray();
                JsonObject providerObject = providerArray.get(0).getAsJsonObject();

                String provider = providerObject.get("name").getAsString();
                String date = newsObject.get("datePublished").getAsString();
                String headline = newsObject.get("name").getAsString();
                String url = newsObject.get("url").getAsString();
                String article = newsObject.get("description").getAsString();

                NewsItem item = new NewsItem(provider, date, headline, url, article);
                newsWrapper.addNewsItem(item);
            }

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(NewsWrapper.class, new NewsWrapperSerializer())
                    .setPrettyPrinting()
                    .create();
            System.out.println(gson.toJson(newsWrapper));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
