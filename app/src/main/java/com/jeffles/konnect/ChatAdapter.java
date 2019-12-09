package com.jeffles.konnect;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private static final int MY_SOS = 0;
    private static final int THEIR_SOS = 1;
    private static final int MY_MESSAGE = 2;
    private static final int THEIR_MESSAGE = 3;

    private final List<ChatItem> chatItems;

    public ChatAdapter(List<ChatItem> chatItems) {
        this.chatItems = chatItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View messageView = null;

        switch (viewType) {
            case MY_SOS:
                messageView = LayoutInflater.from(parent.getContext()).
                        inflate((R.layout.my_sos), parent, false);
                break;
            case THEIR_SOS:
                messageView = LayoutInflater.from(parent.getContext()).
                        inflate((R.layout.their_sos), parent, false);
                break;
            case MY_MESSAGE:
                messageView = LayoutInflater.from(parent.getContext()).
                        inflate((R.layout.my_message), parent, false);
                break;
            case THEIR_MESSAGE:
                messageView = LayoutInflater.from(parent.getContext()).
                        inflate((R.layout.their_message), parent, false);
                break;
        }

        return new ViewHolder(messageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setChatItem(chatItems.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        ChatItem current = chatItems.get(position);
        if (current.isSOS()) {
            if (current.isMyMessage()) {
                return MY_SOS;
            } else {
                return THEIR_SOS;
            }
        } else {
            if (current.isMyMessage()) {
                return MY_MESSAGE;
            } else {
                return THEIR_MESSAGE;
            }
        }
    }

    @Override
    public int getItemCount() {
        return chatItems.size();
    }

    void addMessage(ChatItem message) {
        chatItems.add(0, message);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView message;
        ChatItem chatItem;

        ViewHolder(View view) {
            super(view);

            message = view.findViewById(R.id.message_body);
            name = view.findViewById(R.id.name);
        }

        void setChatItem(ChatItem chatItem) {
            this.chatItem = chatItem;

            if (!chatItem.isMyMessage()) {
                name.setText(chatItem.getSender());
            }

            message.setText(chatItem.getMessage());
        }
    }
}
