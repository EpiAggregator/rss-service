package com.epiaggregator.services.rss.web;

import com.epiaggregator.services.rss.FetcherService;
import com.epiaggregator.services.rss.model.Entry;
import com.epiaggregator.services.rss.model.EntryRepository;
import com.epiaggregator.services.rss.model.Feed;
import com.epiaggregator.services.rss.model.FeedRepository;
import com.epiaggregator.services.rss.web.httpentities.EntryResponse;
import com.epiaggregator.services.rss.web.httpentities.FeedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class FetcherTask {
    @Autowired
    private FetcherService fetcherService;
    @Autowired
    private FeedRepository feedRepository;
    @Autowired
    private EntryRepository entryRepository;

    @Scheduled(fixedRate = 3600000)
    public void fetchFeeds() {
        List<CompletableFuture<List<FeedResponse>>> futures = new ArrayList<>();
        List<Feed> allFeeds = feedRepository.findAll();
        for (Feed f : allFeeds) {
            futures.add(fetcherService.fetchFeedAsync(Collections.singletonList(f.getFeedUri()))
                    .whenComplete((feeds, throwable) -> {
                        for (FeedResponse feed : feeds) {
                            Entry last = entryRepository.findTopByFeedIdOrderByPubDateDesc(f.getId());
                            List<Entry> entries = new ArrayList<>();
                            for (EntryResponse e : feed.getEntries()) {
                                if (last == null || e.getPubDate().after(last.getPubDate())) {
                                    entries.add(new Entry(f.getId(), e.getAuthor(), e.getLink(), e.getTitle(), e.getPubDate(), e.getDescription()));
                                }
                            }
                            try {
                                entryRepository.save(entries);
                            } catch (DuplicateKeyException ignored) {
                            }
                        }
                    })
            );
        }
        for (CompletableFuture<List<FeedResponse>> future : futures) {
            future.join();
        }
    }
}
