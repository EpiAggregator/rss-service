package com.epiaggregator.services.rss.web;

import com.epiaggregator.services.rss.FetcherService;
import com.epiaggregator.services.rss.model.Entry;
import com.epiaggregator.services.rss.model.EntryRepository;
import com.epiaggregator.services.rss.model.Feed;
import com.epiaggregator.services.rss.model.FeedRepository;
import com.epiaggregator.services.rss.web.httpentities.EntryResponse;
import com.epiaggregator.services.rss.web.httpentities.FeedResponse;
import com.epiaggregator.services.rss.web.httpentities.UpdateEntryRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1")
public class RssController {
    private static Logger log = LoggerFactory.getLogger(RssController.class);

    @Autowired
    private FeedRepository feedRepository;
    @Autowired
    private EntryRepository entryRepository;
    @Autowired
    private FetcherService fetcherService;
    @Value("${jwt.secretKey}")
    private String base64EncodedSecretKey;

    @RequestMapping(method = RequestMethod.POST, path = "/feeds")
    public ResponseEntity addFeed(@RequestBody List<String> feedUris) {
        ObjectId userId = verifyJwtAndReturnUserId();
        fetcherService.fetchFeedAsync(feedUris)
                .whenComplete((feeds, throwable) -> {
                    for (FeedResponse feed : feeds) {
                        Feed f = new Feed(userId, feed.getDescription(), feed.getTitle(), feed.getLink(), feed.getFeedUri(), feed.getImage());
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
                        } catch (DuplicateKeyException ignored) {
                        }
                    }
                });
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }


    @RequestMapping(method = RequestMethod.GET, path = "/feeds/{id}/entries")
    public ResponseEntity<List<Entry>> getEntriesByFeed(@PathVariable(value = "id") String feedId) {
        ObjectId userId = verifyJwtAndReturnUserId();
        if (userId == null) {
            throw new UnauthorizedException();
        }

        Feed feed = feedRepository.findOne(feedId);
        if (!Objects.equals(userId, feed.getUserId())) {
            throw new UnauthorizedException();
        }

        return new ResponseEntity<>(entryRepository.findByFeedId(new ObjectId(feedId)), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/feeds")
    public ResponseEntity<List<Feed>> getFeeds() {
        ObjectId userId = verifyJwtAndReturnUserId();
        if (userId == null) {
            throw new UnauthorizedException();
        }

        return new ResponseEntity<>(feedRepository.findAllByUserId(userId), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/feeds/{id}")
    public ResponseEntity<Feed> getFeed(@PathVariable(value = "id") String feedId) {
        ObjectId userId = verifyJwtAndReturnUserId();
        if (userId == null) {
            throw new UnauthorizedException();
        }

        return new ResponseEntity<>(feedRepository.findOneByIdAndUserId(feedId, userId), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/feeds/entries")
    public ResponseEntity<List<Entry>> getAllEntries() {
        ObjectId userId = verifyJwtAndReturnUserId();
        if (userId == null) {
            throw new UnauthorizedException();
        }

        List<Entry> entries = new ArrayList<>();
        List<Feed> feeds = feedRepository.findAllByUserId(userId);
        for (Feed f : feeds) {
            entries.addAll(entryRepository.findByFeedId(f.getId()));
        }

        return new ResponseEntity<>(entries, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PATCH, path = "/feeds/{id}/entries/{entryId}")
    public ResponseEntity<Entry> updateEntry(@PathVariable(value = "id") String feedId,
                                             @PathVariable(value = "entryId") String entryId,
                                             @RequestBody UpdateEntryRequest request) {
        ObjectId userId = verifyJwtAndReturnUserId();
        if (userId == null) {
            throw new UnauthorizedException();
        }

        Entry entry = entryRepository.findByIdAndFeedId(new ObjectId(entryId), new ObjectId(feedId));
        if (request.getRead() != null) {
            entry.setRead(request.getRead());
        }
        if (request.getFavorite() != null) {
            entry.setFavorite(request.getFavorite());
        }
        entryRepository.save(entry);
        return new ResponseEntity<>(entry, HttpStatus.OK);
    }

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({JwtException.class, UnauthorizedException.class})
    private void handleJwtVerificationFailure(RuntimeException e) {
    }

    private ObjectId verifyJwtAndReturnUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String header = request.getHeader("Authorization");
        if (header == null || header.isEmpty() || !header.startsWith("Bearer ")) {
            return null;
        }

        header = header.replace("Bearer ", "");
        if ("".equals(header)) {
            return null;
        }
        Jws<Claims> claimsJws = Jwts.parser()
                .setSigningKey(base64EncodedSecretKey)
                .parseClaimsJws(header);
        return new ObjectId(claimsJws.getBody().getSubject());
    }
}
