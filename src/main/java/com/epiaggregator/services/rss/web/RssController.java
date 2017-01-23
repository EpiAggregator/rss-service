package com.epiaggregator.services.rss.web;

import com.epiaggregator.services.rss.model.Entry;
import com.epiaggregator.services.rss.model.EntryRepository;
import com.epiaggregator.services.rss.model.Feed;
import com.epiaggregator.services.rss.model.FeedRepository;
import com.epiaggregator.services.rss.web.httpentities.EntryResponse;
import com.epiaggregator.services.rss.web.httpentities.FeedResponse;
import com.epiaggregator.services.rss.web.httpentities.UpdateEntryRequest;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class RssController {
    @Autowired
    private FeedRepository feedRepository;
    @Autowired
    private EntryRepository entryRepository;
    @Autowired
    private FetcherService fetcherService;

    @RequestMapping(method = RequestMethod.POST, path = "/feeds")
    public ResponseEntity addFeed(@RequestBody List<String> feedUris) {
        List<FeedResponse> feeds = fetcherService.fetchFeed(feedUris);

        for (FeedResponse feed : feeds) {
            Feed f = new Feed(feed.getDescription(), feed.getTitle(), feed.getLink(), feed.getFeedUri(), feed.getImage());
            try {
                f = feedRepository.save(f);
            } catch (DuplicateKeyException e) {
                f = feedRepository.findByFeedUri(feed.getFeedUri());
            }
            Entry last = entryRepository.findTopByFeedIdOrderByPubDateDesc(f.getId());
            List<Entry> entries = new ArrayList<>();
            for (EntryResponse e : feed.getEntries()) {
                if (last == null || e.getPubDate().after(last.getPubDate())) {
                    entries.add(new Entry(f.getId(), e.getAuthor(), e.getLink(), e.getTitle(), e.getPubDate(), e.getDescription()));
                }
            }
            try {
                entryRepository.save(entries);
            } catch (DuplicateKeyException ignored) {}
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/feeds/{id}/entries")
    public ResponseEntity<List<Entry>> getEntriesByFeed(@PathVariable(value = "id") String feedId) {
        return new ResponseEntity<>(entryRepository.findByFeedId(new ObjectId(feedId)), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/feeds")
    public ResponseEntity<List<Feed>> getFeeds() {
        return new ResponseEntity<>(feedRepository.findAll(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/feeds/{id}")
    public ResponseEntity<Feed> getFeed(@PathVariable(value = "id") String feedId) {
        return new ResponseEntity<>(feedRepository.findOne(feedId), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PATCH, path = "/feeds/{id}/entries/{entryId}")
    public ResponseEntity<Entry> updateEntry(@PathVariable(value = "id") String feedId,
                                      @PathVariable(value = "entryId") String entryId,
                                      @RequestBody UpdateEntryRequest request) {
        Entry entry = entryRepository.findByIdAndFeedId(new ObjectId(entryId), new ObjectId(feedId));
        entry.setRead(request.getRead());
        entry.setFavorite(request.getFavorite());
        entryRepository.save(entry);
        return new ResponseEntity<>(entry, HttpStatus.OK);
    }
}
