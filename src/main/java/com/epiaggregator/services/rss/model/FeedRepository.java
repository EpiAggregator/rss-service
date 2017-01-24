package com.epiaggregator.services.rss.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FeedRepository extends MongoRepository<Feed, String> {
    Feed findByFeedUri(String feedUri);

    List<Feed> findAllByUserId(ObjectId userId);

    Feed findOneByIdAndUserId(String id, ObjectId userId);
}
