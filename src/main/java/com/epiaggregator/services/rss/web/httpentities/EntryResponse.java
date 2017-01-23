package com.epiaggregator.services.rss.web.httpentities;

import java.util.Date;

public class EntryResponse {
    private String id;
    private String author;
    private String link;
    private String title;
    private Date pubDate;
    private String description;

    public EntryResponse() {
    }

    public EntryResponse(String id, String author, String link, String title, Date pubDate, String description) {
        this.id = id;
        this.author = author;
        this.link = link;
        this.title = title;
        this.pubDate = pubDate;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
