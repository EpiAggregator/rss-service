package com.epiaggregator.services.rss.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EntryRepository extends MongoRepository<Entry, String> {
    Entry findTopByFeedIdOrderByPubDateDesc(ObjectId feedId);

    List<Entry> findByFeedId(ObjectId feedId);

    Entry findByIdAndFeedId(ObjectId entryId, ObjectId feedId);
}
