package com.epiaggregator.services.rss.web;

import com.epiaggregator.services.rss.web.httpentities.FeedResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient("fetcher-service")
public interface FetcherClient {
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/fetch",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    List<FeedResponse> fetchFeed(List<String> feedUris);
}
