package com.jeffles.konnect;

import org.joda.time.DateTime;

import java.net.MalformedURLException;
import java.net.URL;

public class NewsItem {
    private static final String TAG = "NewsItem";

    private String articleProvider;
    private DateTime datePublished;
    private String headline;
    private URL url;
    private String article;

    public NewsItem(String articleProvider, String datePublished, String headline, String url, String article) {
        this.articleProvider = articleProvider;

        this.datePublished = new DateTime(datePublished);

        this.headline = headline;

        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        this.article = article;
    }

    public String getArticleProvider() {
        return articleProvider;
    }

    public DateTime getDatePublished() {
        return datePublished;
    }

    public String getHeadline() {
        return headline;
    }

    public URL getUrl() {
        return url;
    }

    public String getArticle() {
        return article;
    }
}
