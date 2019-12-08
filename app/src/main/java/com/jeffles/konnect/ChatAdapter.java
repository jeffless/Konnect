package com.jeffles.konnect;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private final List<ChatItem> chatItems;

    public ChatAdapter(List<ChatItem> chatItems) {
        this.chatItems = chatItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View messageView;

        if (viewType == 0) {
            messageView = LayoutInflater.from(parent.getContext()).
                    inflate((R.layout.their_message), parent, false);
        } else {

            messageView = LayoutInflater.from(parent.getContext()).
                    inflate((R.layout.my_message), parent, false);
        }

        return new ViewHolder(messageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setChatItem(chatItems.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return chatItems.get(position).isMyMessage() ? 1 : 0;
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

        ViewHolder(@NonNull View view) {
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
