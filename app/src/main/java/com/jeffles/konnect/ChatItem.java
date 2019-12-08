package com.jeffles.konnect;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

public class ChatItem {
    private static final String TAG = "ChatItem";

    private String sender;
    private DateTime timeStamp;
    private Priority priority;
    private String message;
    private boolean isMyMessage;

    public enum Priority {
        @SerializedName("high")
        HIGH,

        @SerializedName("low")
        LOW
    }

    public ChatItem(String sender, DateTime timeStamp, Priority priority, String message) {
        this.sender = sender;
        this.timeStamp = timeStamp;
        this.priority = priority;
        this.message = message;

        isMyMessage = true;
    }

    public String getSender() {
        return sender;
    }

    public DateTime getTimeStamp() {
        return timeStamp;
    }

    public Priority getPriority() {
        return priority;
    }

    public String getMessage() {
        return message;
    }

    public boolean isMyMessage() {
        return isMyMessage;
    }

    public void setMyMessage(boolean myMessage) {
        isMyMessage = myMessage;
    }
}
