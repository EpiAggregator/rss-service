package com.epiaggregator.services.rss.model;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface FeedRepository extends MongoRepository<Feed, String> {
    Feed findByFeedUri(String feedUri);
}
