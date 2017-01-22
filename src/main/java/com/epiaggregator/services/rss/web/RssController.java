package com.epiaggregator.services.rss.web;

import com.epiaggregator.services.rss.model.Entry;
import com.epiaggregator.services.rss.model.EntryRepository;
import com.epiaggregator.services.rss.model.Feed;
import com.epiaggregator.services.rss.model.FeedRepository;
import com.epiaggregator.services.rss.web.httpentities.EntryResponse;
import com.epiaggregator.services.rss.web.httpentities.FeedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping(method = RequestMethod.POST, path = "/rss")
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
}
