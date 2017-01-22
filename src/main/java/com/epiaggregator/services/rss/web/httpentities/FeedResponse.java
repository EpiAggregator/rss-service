package com.epiaggregator.services.rss.web.httpentities;

import java.util.ArrayList;
import java.util.List;

public class FeedResponse {
    private String description;
    private String title;
    private String link;
    private String feedUri;
    private List<EntryResponse> entries = new ArrayList<>();
    private String image;

    public FeedResponse() {
    }

    public FeedResponse(String description, String title, String link, String feedUri, List<EntryResponse> entries, String image) {
        this.description = description;
        this.title = title;
        this.link = link;
        this.feedUri = feedUri;
        this.entries = entries;
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getFeedUri() {
        return feedUri;
    }

    public void setFeedUri(String feedUri) {
        this.feedUri = feedUri;
    }

    public List<EntryResponse> getEntries() {
        return entries;
    }

    public void setEntries(List<EntryResponse> entries) {
        this.entries = entries;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
