package com.epiaggregator.services.rss.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.Date;

public class Entry {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId feedId;
    private String author;
    private String link;
    private String title;
    private Date pubDate;
    private String description;
    private Boolean read;
    private Boolean favorite;

    public Entry() {
    }

    public Entry(ObjectId feedId, String author, String link, String title, Date pubDate, String description) {
        this.id = id;
        this.feedId = feedId;
        this.author = author;
        this.link = link;
        this.title = title;
        this.pubDate = pubDate;
        this.description = description;
    }

    public Entry(ObjectId id, ObjectId feedId, String author, String link, String title, Date pubDate, String description, Boolean read, Boolean favorite) {
        this.id = id;
        this.feedId = feedId;
        this.author = author;
        this.link = link;
        this.title = title;
        this.pubDate = pubDate;
        this.description = description;
        this.read = read;
        this.favorite = favorite;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public ObjectId getFeedId() {
        return feedId;
    }

    public void setFeedId(ObjectId feedId) {
        this.feedId = feedId;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
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
