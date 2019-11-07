package com.jeffles.konnect;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

    private SwipeRefreshLayout swipeContainer;
    private RecyclerView newsView;
    private NewsAdapter newsAdapter;

    private NewsWrapper newsWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(() -> {
            if (hasConnection()) {
                new TimeTask().execute();
            }
        });

        Bridgefy.initialize(getApplicationContext(), "60771531-18ed-4bc8-bac1-df3908df319c", new RegistrationListener() {
            @Override
            public void onRegistrationSuccessful(BridgefyClient bridgefyClient) {
                Log.i(TAG, "Successful Registration");
                Bridgefy.start(messageListener, stateListener);
            }

            @Override
            public void onRegistrationFailed(int i, String e) {
                Log.e(TAG, e);
            }
        });

        if (hasConnection()) {
            new TimeTask().execute();
        }
    }

    MessageListener messageListener = new MessageListener() {
        @Override
        public void onBroadcastMessageReceived(Message message) {
            Log.i(TAG, "Received data");

            HashMap<String, Object> content = message.getContent();

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(NewsWrapper.class, new NewsWrapperDeserializer())
                    .create();

            NewsWrapper receivedWrapper = gson.fromJson((String) content.get("news"), NewsWrapper.class);

            if (receivedWrapper.getTimeStamp().isAfter(newsWrapper.getTimeStamp())) {
                newsWrapper = receivedWrapper;
                newsView = findViewById(R.id.newsView);
                newsAdapter = new NewsAdapter(newsWrapper.getNewsItems());

                newsView.setAdapter(newsAdapter);
            } else {
                broadcastNews();
            }
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
            Log.i(TAG, "onDeviceLost: " + device.getUserId());
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

    private void broadcastNews() {
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
                Log.e(TAG, "Get Time Error", e);
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

                    newsWrapper.addNewsItem(new NewsItem(providerObject.get("name").getAsString(),
                            newsObject.get("datePublished").getAsString(),
                            newsObject.get("name").getAsString(),
                            newsObject.get("url").getAsString(),
                            newsObject.get("description").getAsString()));
                }

            } catch (Exception e) {
                Log.e(TAG, "Search News Error", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            newsView = findViewById(R.id.newsView);
            newsAdapter = new NewsAdapter(newsWrapper.getNewsItems());

            newsView.setAdapter(newsAdapter);

            newsAdapter.setOnItemClickListener(position -> {
                Uri webpage = Uri.parse(newsWrapper.getNewsItems().get(position).getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            });

            broadcastNews();
            swipeContainer.setRefreshing(false);
        }
    }
}
