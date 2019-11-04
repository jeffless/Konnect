package com.jeffles.konnect;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.client.BridgefyClient;
import com.bridgefy.sdk.client.Device;
import com.bridgefy.sdk.client.Message;
import com.bridgefy.sdk.client.MessageListener;
import com.bridgefy.sdk.client.RegistrationListener;
import com.bridgefy.sdk.client.Session;
import com.bridgefy.sdk.client.StateListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;

import static com.jeffles.konnect.DateHandler.getTime;
import static com.jeffles.konnect.NewsHandler.searchNews;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    private final static String searchTerm = "world news";

    RecyclerView newsView;
    NewsAdapter newsAdapter;

    NewsWrapper newsWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bridgefy.initialize(getApplicationContext(), "60771531-18ed-4bc8-bac1-df3908df319c", new RegistrationListener() {
            @Override
            public void onRegistrationSuccessful(BridgefyClient bridgefyClient) {
                // Once the registration process has been successful, we can start operations
                Log.e(TAG, "Successful Registration");
                Bridgefy.start(messageListener, stateListener);
            }

            @Override
            public void onRegistrationFailed(int i, String e) {
                Log.e(TAG, e);
            }
        });

        if (hasConnection()) {
            try {
                new TimeTask().execute();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    MessageListener messageListener = new MessageListener() {
        @Override
        public void onMessageSent(String messageId) {
            Log.i(TAG, "message sent");
        }

        @Override
        public void onBroadcastMessageReceived(Message message) {
            Log.d(TAG, "Received data");

            HashMap<String, Object> content = message.getContent();

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(NewsWrapper.class, new NewsWrapperDeserializer())
                    .create();

            newsWrapper = gson.fromJson((String) content.get("news"), NewsWrapper.class);

            newsView = findViewById(R.id.newsView);
            newsAdapter = new NewsAdapter(newsWrapper.getNewsItems());

            newsView.setAdapter(newsAdapter);
        }
    };

    StateListener stateListener = new StateListener() {
        @Override
        public void onStarted() {
            Log.i(TAG, "onStarted: Bridgefy started");
        }

        @Override
        public void onDeviceConnected(final Device device, Session session) {
            Log.i(TAG, "onDeviceConnected: " + device.getUserId());
        }

        @Override
        public void onDeviceLost(Device device) {
            Log.w(TAG, "onDeviceLost: " + device.getUserId());
        }

        @Override
        public void onStartError(String message, int errorCode) {
            Log.e(TAG, "onStartError: " + message + " " + errorCode);

            if (errorCode == StateListener.INSUFFICIENT_PERMISSIONS) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //retry again after permissions have been granted
            Bridgefy.start(messageListener, stateListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Bridgefy.stop();
    }

    private void sendMessage() {
        HashMap<String, Object> data = new HashMap<>();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(NewsWrapper.class, new NewsWrapperSerializer())
                .create();

        data.put("news", gson.toJson(newsWrapper));

        Bridgefy.sendBroadcastMessage(data);
    }

    private boolean hasConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }

        Network network = connectivityManager.getActiveNetwork();
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
        if (capabilities == null) {
            return false;
        }
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
    }

    private class TimeTask extends AsyncTask<String, String, String> {
        protected String doInBackground(String... urls) {
            try {
                newsWrapper = new NewsWrapper(getTime());

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            new NewsListTask().execute();
        }
    }

    private class NewsListTask extends AsyncTask<String, String, String> {
        protected String doInBackground(String... urls) {
            try {
                JsonObject result = searchNews(searchTerm);
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

                    newsWrapper.addNewsItem(new NewsItem(provider, date, headline, url, article));
                }

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            newsView = findViewById(R.id.newsView);
            newsAdapter = new NewsAdapter(newsWrapper.getNewsItems());

            newsView.setAdapter(newsAdapter);

            sendMessage();
        }
    }
}
