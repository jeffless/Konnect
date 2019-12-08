package com.jeffles.konnect;

import org.joda.time.DateTime;

public class NewsItem {
    private static final String TAG = "NewsItem";

    private String articleProvider;
    private DateTime datePublished;
    private String headline;
    private String url;
    private String article;

    public NewsItem(String articleProvider, String datePublished, String headline, String url, String article) {
        this.articleProvider = articleProvider;
        this.datePublished = new DateTime(datePublished);
        this.headline = headline;
        this.url = url;
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

    public String getUrl() {
        return url;
    }

    public String getArticle() {
        return article;
    }
}
