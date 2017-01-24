package com.epiaggregator.services.rss;

import com.epiaggregator.services.rss.web.FetcherClient;
import com.epiaggregator.services.rss.web.httpentities.FeedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class FetcherService {
    private static Logger log = LoggerFactory.getLogger(FetcherService.class);

    @Autowired
    private FetcherClient fetcherClient;

    @Async
    public CompletableFuture<List<FeedResponse>> fetchFeedAsync(List<String> feedUris) {
        return CompletableFuture.completedFuture(fetcherClient.fetchFeed(feedUris));
    }

    public List<FeedResponse> fetchFeed(List<String> feedUris) {
        return fetcherClient.fetchFeed(feedUris);
    }
}
