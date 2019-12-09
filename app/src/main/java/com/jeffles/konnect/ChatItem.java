package com.jeffles.konnect;

import org.joda.time.DateTime;

public class ChatItem {
    private static final String TAG = "ChatItem";

    private String sender;
    private DateTime timeStamp;
    private boolean isSOS;
    private String message;
    private boolean isMyMessage;

    public ChatItem(String sender, DateTime timeStamp, boolean isSOS, String message) {
        this.sender = sender;
        this.timeStamp = timeStamp;
        this.isSOS = isSOS;
        this.message = message;

        isMyMessage = true;
    }

    public String getSender() {
        return sender;
    }

    public DateTime getTimeStamp() {
        return timeStamp;
    }

    public boolean isSOS() {
        return isSOS;
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
