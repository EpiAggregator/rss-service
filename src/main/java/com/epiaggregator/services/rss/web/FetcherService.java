package com.epiaggregator.services.rss.web;

import com.epiaggregator.services.rss.web.httpentities.FeedResponse;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class FetcherService {
    @Autowired
    @LoadBalanced
    private RestTemplate restTemplate;


    public List<FeedResponse> fetchFeed(List<String> feedUris) {
        return ImmutableList.copyOf(
                restTemplate.postForObject("http://fetcher-service/fetch", feedUris, FeedResponse[].class)
        );
    }
}
