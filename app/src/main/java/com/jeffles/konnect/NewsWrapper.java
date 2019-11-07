package com.jeffles.konnect;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class NewsWrapper {
    private DateTime timeStamp;
    private List<NewsItem> newsItems;

    public NewsWrapper(DateTime timeStamp) {
        this.timeStamp = timeStamp;
        this.newsItems = new ArrayList<>();
    }

    public DateTime getTimeStamp() {
        return timeStamp;
    }

    public void addNewsItem(NewsItem item) {
        newsItems.add(item);
    }

    public List<NewsItem> getNewsItems() {
        return newsItems;
    }
}
