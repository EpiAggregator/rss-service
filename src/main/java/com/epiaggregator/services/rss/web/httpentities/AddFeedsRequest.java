package com.epiaggregator.services.rss.web.httpentities;

import javax.validation.constraints.NotNull;

public class AddFeedsRequest {
    @NotNull
    private String feedUri;

    public AddFeedsRequest() {
    }

    public AddFeedsRequest(String feedUri) {
        this.feedUri = feedUri;
    }

    public String getFeedUri() {
        return feedUri;
    }

    public void setFeedUri(String feedUri) {
        this.feedUri = feedUri;
    }
}
