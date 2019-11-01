package com.jeffles.konnect;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.client.BridgefyClient;
import com.bridgefy.sdk.client.Device;
import com.bridgefy.sdk.client.Message;
import com.bridgefy.sdk.client.MessageListener;
import com.bridgefy.sdk.client.RegistrationListener;
import com.bridgefy.sdk.client.Session;
import com.bridgefy.sdk.client.StateListener;
import com.bridgefy.sdk.framework.exceptions.MessageException;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "MainActivity";

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

        findViewById(R.id.sendButton).setOnClickListener((View v) -> sendMessage());
    }

    MessageListener messageListener = new MessageListener() {
        @Override
        public void onMessageSent(String messageId) {
            Log.i(TAG, "message sent");
        }

        @Override
        public void onBroadcastMessageReceived(Message message) {
            HashMap<String, Object> content = message.getContent();

            Log.d(TAG, "Received data");

            ((TextView) findViewById(R.id.broadcastedMessage)).setText(String.valueOf(content.get("message")));
        }

        @Override
        public void onMessageFailed(Message message, MessageException e) {
            Log.e(TAG, e.toString());
        }

        @Override
        public void onMessageReceivedException(java.lang.String sender, MessageException e) {
            Log.e(TAG, e.toString());
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

        EditText text = findViewById(R.id.editText);

        data.put("message", text.getText().toString());

        Bridgefy.sendBroadcastMessage(data);
        Log.d(TAG, "Data sent " + text.getText());
    }
}
