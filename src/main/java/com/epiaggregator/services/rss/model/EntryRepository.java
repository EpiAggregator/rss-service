package com.epiaggregator.services.rss.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EntryRepository extends MongoRepository<Entry, String> {
    Entry findTopByFeedIdOrderByPubDateDesc(ObjectId feedId);
}
