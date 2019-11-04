package com.jeffles.konnect;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.format.DateTimeFormat;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    List<NewsItem> newsList;

    public NewsAdapter(List<NewsItem> newsList) {
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.news_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewsItem currentItem = newsList.get(position);
        holder.headlineTextView.setText(currentItem.getHeadline());
        holder.dateTextView.setText(currentItem.getDatePublished().toString(DateTimeFormat.longDateTime()));
        holder.articleTextView.setText(currentItem.getArticle());
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView headlineTextView;
        TextView dateTextView;
        TextView articleTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            headlineTextView = itemView.findViewById(R.id.headlineTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            articleTextView = itemView.findViewById(R.id.articleTextView);
        }
    }
}
