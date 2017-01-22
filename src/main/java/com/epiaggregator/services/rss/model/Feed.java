package com.epiaggregator.services.rss.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Feed {
    @Id
    private ObjectId id;
    private String description;
    private String title;
    private String link;
    @Indexed(unique = true, dropDups = true)
    private String feedUri;
    private String image;

    public Feed() {
    }

    public Feed(String description, String title, String link, String feedUri, String image) {
        this.description = description;
        this.title = title;
        this.link = link;
        this.feedUri = feedUri;
        this.image = image;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getFeedUri() {
        return feedUri;
    }

    public void setFeedUri(String feedUri) {
        this.feedUri = feedUri;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}