package com.epiaggregator.services.rss.web;


import com.epiaggregator.services.rss.model.Entry;
import com.epiaggregator.services.rss.model.EntryRepository;
import com.epiaggregator.services.rss.model.Feed;
import com.epiaggregator.services.rss.model.FeedRepository;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RssControllerTests {
    private static ObjectId userId = new ObjectId("588ce88f8c59d43000e747a2");
    private static String jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoLXNlcnZpY2UiLCJpYXQiOjE0ODU5Nzc1ODMsImV4cCI6NDEwNTk3MTE4MywiYXVkIjoicnNzLXNlcnZpY2UiLCJzdWIiOiI1ODhjZTg4ZjhjNTlkNDMwMDBlNzQ3YTIifQ.p4qnERijeyRuNBnOg9GOPvq59CM0I-RulA9a8vsT0L8";
    private static List<String> feedUris = new ArrayList<>(Arrays.asList(
            "https://rakyll.org/index.xml",
            "http://www.nytimes.com/services/xml/rss/nyt/HomePage.xml"
    ));
    @MockBean
    private EntryRepository entryRepository;
    @MockBean
    private FeedRepository feedRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MockHttpServletRequest mockHttpServletRequest;

    @Test
    public void testAddFeed() throws Exception {
        ArgumentCaptor<Feed> captor = ArgumentCaptor.forClass(Feed.class);
        when(feedRepository.save(captor.capture()))
                .thenAnswer(invocation -> {
                    Feed f = captor.getValue();
                    f.setId(new ObjectId());
                    return f;
                });
        when(entryRepository.findTopByFeedIdOrderByPubDateDesc(any()))
                .thenReturn(null);
        when(entryRepository.save(any(Entry.class)))
                .thenReturn(null);

        mockMvc.perform(post("/v1/feeds")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8, MediaType.APPLICATION_JSON)
                .content("[\"https://rakyll.org/index.xml\", \"http://www.nytimes.com/services/xml/rss/nyt/HomePage.xml\"]"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    public void testGetEntriesByFeedShouldFail() throws Exception {
        ObjectId feedId = new ObjectId();
        when(feedRepository.findOne(feedId.toHexString()))
                .thenReturn(new Feed(feedId, userId, "A description", "Feed title",
                        "http://example.com", "http://example.com/feed.rss", null));
        when(entryRepository.findByFeedId(feedId)).thenAnswer(invocation -> {
            List<Entry> entries = new ArrayList<>();
            entries.add(new Entry(new ObjectId(), feedId, "Me", "link 1", "Title 1", new Date(), "Content 1", false, false));
            entries.add(new Entry(new ObjectId(), feedId, "Me", "link 2", "Title 2", new Date(), "Content 2", true, false));
            entries.add(new Entry(new ObjectId(), feedId, "Me", "link 3", "Title 3", new Date(), "Content 3", false, true));
            return entries;
        });
        mockMvc.perform(get("/v1/feeds/{id}/entries", feedId.toHexString())
                .accept(MediaType.APPLICATION_JSON_UTF8, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetEntriesByFeedShouldSuccess() throws Exception {
        ObjectId feedId = new ObjectId();
        when(feedRepository.findOne(feedId.toHexString()))
                .thenReturn(new Feed(feedId, userId, "A description", "Feed title",
                        "http://example.com", "http://example.com/feed.rss", null));
        when(entryRepository.findByFeedId(feedId)).thenAnswer(invocation -> {
            List<Entry> entries = new ArrayList<>();
            entries.add(new Entry(new ObjectId(), feedId, "Me", "link 1", "Title 1", new Date(), "Content 1", false, false));
            entries.add(new Entry(new ObjectId(), feedId, "Me", "link 2", "Title 2", new Date(), "Content 2", true, false));
            entries.add(new Entry(new ObjectId(), feedId, "Me", "link 3", "Title 3", new Date(), "Content 3", false, true));
            return entries;
        });

        mockMvc.perform(get("/v1/feeds/{id}/entries", feedId.toHexString())
                .accept(MediaType.APPLICATION_JSON_UTF8, MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void testGetFeedsShouldSuccess() throws Exception {
        when(feedRepository.findAllByUserId(userId))
                .thenAnswer(invocation -> {
                    List<Feed> feeds = new ArrayList<>();
                    feeds.add(new Feed(new ObjectId(), userId, "A description", "Feed title",
                            "http://example.com", "http://example.com/feed.rss", null));
                    feeds.add(new Feed(new ObjectId(), userId, "A description 2", "Feed title 2",
                            "http://example.com/2", "http://example.com/feed2.rss", null));
                    return feeds;
                });

        mockMvc.perform(get("/v1/feeds")
                .accept(MediaType.APPLICATION_JSON_UTF8, MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testGetFeedsShouldFail() throws Exception {
        when(feedRepository.findAllByUserId(userId))
                .thenAnswer(invocation -> {
                    List<Feed> feeds = new ArrayList<>();
                    feeds.add(new Feed(new ObjectId(), userId, "A description", "Feed title",
                            "http://example.com", "http://example.com/feed.rss", null));
                    feeds.add(new Feed(new ObjectId(), userId, "A description 2", "Feed title 2",
                            "http://example.com/2", "http://example.com/feed2.rss", null));
                    return feeds;
                });
        mockMvc.perform(get("/v1/feeds")
                .accept(MediaType.APPLICATION_JSON_UTF8, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetAllEntriesShouldSuccess() throws Exception {
        ObjectId feedId1 = new ObjectId();
        ObjectId feedId2 = new ObjectId();
        when(feedRepository.findAllByUserId(userId))
                .thenAnswer(invocation -> {
                    List<Feed> feeds = new ArrayList<>();
                    feeds.add(new Feed(feedId1, userId, "A description", "Feed title",
                            "http://example.com", "http://example.com/feed.rss", null));
                    feeds.add(new Feed(feedId2, userId, "A description 2", "Feed title 2",
                            "http://example.com/2", "http://example.com/feed2.rss", null));
                    return feeds;
                });
        when(entryRepository.findByFeedId(feedId1)).thenAnswer(invocation -> {
            List<Entry> entries = new ArrayList<>();
            entries.add(new Entry(new ObjectId(), feedId1, "Me", "link 1", "Title 1", new Date(), "Content 1", false, false));
            entries.add(new Entry(new ObjectId(), feedId1, "Me", "link 2", "Title 2", new Date(), "Content 2", true, false));
            return entries;
        });
        when(entryRepository.findByFeedId(feedId2)).thenAnswer(invocation -> {
            List<Entry> entries = new ArrayList<>();
            entries.add(new Entry(new ObjectId(), feedId2, "Me", "link 3", "Title 3", new Date(), "Content 3", false, true));
            entries.add(new Entry(new ObjectId(), feedId2, "Me", "link 4", "Title 4", new Date(), "Content 4", false, true));
            return entries;
        });

        mockMvc.perform(get("/v1/feeds/entries")
                .accept(MediaType.APPLICATION_JSON_UTF8, MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    public void testGetAllEntriesShouldFail() throws Exception {
        ObjectId feedId = new ObjectId();

        mockMvc.perform(get("/v1/feeds/{id}", feedId.toHexString())
                .accept(MediaType.APPLICATION_JSON_UTF8, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdateEntryShouldSuccess() throws Exception {
        ObjectId feedId = new ObjectId();
        ObjectId entryId = new ObjectId();

        when(entryRepository.findByIdAndFeedId(entryId, feedId))
                .thenReturn(
                        new Entry(entryId, feedId, "Me", "link 4", "Title 4", new Date(), "Content 4", false, true)
                );

        mockMvc.perform(patch("/v1/feeds/{id}/entries/{entryId}", feedId.toHexString(), entryId.toHexString())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8, MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt)
                .content("{\"read\": true}"))
                .andDo(print())
                .andExpect(jsonPath("$.read", equalTo(true)))
                .andExpect(jsonPath("$.favorite", equalTo(true)));
        mockMvc.perform(patch("/v1/feeds/{id}/entries/{entryId}", feedId.toHexString(), entryId.toHexString())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8, MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwt)
                .content("{\"favorite\": false}"))
                .andDo(print())
                .andExpect(jsonPath("$.read", equalTo(true)))
                .andExpect(jsonPath("$.favorite", equalTo(false)));
    }

    @Test
    public void testUpdateEntryShouldFail() throws Exception {
        ObjectId feedId = new ObjectId();
        ObjectId entryId = new ObjectId();

        mockMvc.perform(patch("/v1/feeds/{id}/entries/{entryId}", feedId.toHexString(), entryId.toHexString())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8, MediaType.APPLICATION_JSON)
                .content("{\"read\": true}"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
