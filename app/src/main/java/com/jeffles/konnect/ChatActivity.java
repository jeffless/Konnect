package com.jeffles.konnect;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.jeffles.konnect.serialize.ChatItemDeserializer;
import com.jeffles.konnect.serialize.ChatItemSerializer;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
    private final static String TAG = "ChatActivity";

    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        createNotificationChannel();

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

        findViewById(R.id.sendMessage).setOnClickListener((View v) -> sendMessage());
        findViewById(R.id.sendSOS).setOnClickListener((View v) -> sendSOS());

        chatAdapter = new ChatAdapter(new ArrayList<ChatItem>());
        RecyclerView messagesView = findViewById(R.id.messages_view);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setReverseLayout(true);
        messagesView.setLayoutManager(mLinearLayoutManager);
        messagesView.setAdapter(chatAdapter);
    }

    MessageListener messageListener = new MessageListener() {
        @Override
        public void onBroadcastMessageReceived(Message message) {
            Log.i(TAG, "Received data");

            HashMap<String, Object> content = message.getContent();

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(ChatItem.class, new ChatItemDeserializer())
                    .create();

            ChatItem receivedChat = gson.fromJson((String) content.get("chat"), ChatItem.class);
            receivedChat.setMyMessage(false);
            chatAdapter.addMessage(receivedChat);

            if (receivedChat.getPriority() == ChatItem.Priority.HIGH) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "konnect")
                        .setSmallIcon(R.drawable.ic_sos_black_24dp)
                        .setContentTitle("Konnect")
                        .setContentText(receivedChat.getMessage())
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);


                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                notificationManager.notify(0, builder.build());
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
                ActivityCompat.requestPermissions(ChatActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        }
    };

    private void sendMessage() {
        broadcastMessage(ChatItem.Priority.LOW);
    }

    private void sendSOS() {
        broadcastMessage(ChatItem.Priority.HIGH);
    }

    private void broadcastMessage(ChatItem.Priority priority) {
        EditText messageBox = findViewById(R.id.editText);
        String message = messageBox.getText().toString().trim();
        if (message.length() == 0) {
            return;
        }

        HashMap<String, Object> data = new HashMap<>();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ChatItem.class, new ChatItemSerializer())
                .create();

        ChatItem item = new ChatItem("Don Joe", DateTime.now(), priority, message);
        data.put("chat", gson.toJson(item));
        chatAdapter.addMessage(item);

        Bridgefy.sendBroadcastMessage(data);
        messageBox.setText("");
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Konnect";
            String description = "Mesh Library";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("konnect", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
