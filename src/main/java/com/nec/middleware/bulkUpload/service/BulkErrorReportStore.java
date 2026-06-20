package com.nec.middleware.bulkUpload.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Holds temp error-report files in memory, keyed by a UUID token.
 *
 * Flow:
 *   1. After a failed bulk upload, store(file) → returns a token.
 *   2. Token is embedded in the JSON response as a GET download URL.
 *   3. User clicks the URL in browser → GET endpoint calls claim(token) → file streams back.
 *
 * Tokens expire after 24 hours. The background sweeper deletes
 * unclaimed files so the temp directory never fills up.
 */
@Slf4j
@Component
public class BulkErrorReportStore {


    private static final long TTL_HOURS = 24;

    private record Entry(File file, long expiresAt) {}

    private final Map<String, Entry> store = new ConcurrentHashMap<>();

    private final ScheduledExecutorService sweeper =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "bulk-error-sweeper");
                t.setDaemon(true);
                return t;
            });

    public BulkErrorReportStore() {
        sweeper.scheduleAtFixedRate(this::evictExpired, 1, 1, TimeUnit.HOURS);
    }

    /** Store a file and return the token to embed in the download URL. */
    public String store(File file) {
        String token = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        long expiresAt = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(TTL_HOURS);
        store.put(token, new Entry(file, expiresAt));
        log.debug("Stored error report token={} expires={}min file='{}'",
                token, TTL_HOURS, file.getAbsolutePath());
        return token;
    }

    /**
     * Consume the token and return its file. Returns null if the token
     * is unknown or expired.
     */
    public File claim(String token) {
        Entry entry = store.get(token);
        if (entry == null) {
            log.warn("Claim for unknown/expired token={}", token);
            return null;
        }
        if (System.currentTimeMillis() > entry.expiresAt()) {
            log.warn("Token={} expired at claim time", token);
            deleteQuietly(entry.file());
            return null;
        }
        return entry.file();
    }

    private void evictExpired() {
        long now = System.currentTimeMillis();
        store.entrySet().removeIf(e -> {
            if (now > e.getValue().expiresAt()) {
                log.debug("Evicting expired token={}", e.getKey());
                deleteQuietly(e.getValue().file());
                return true;
            }
            return false;
        });
    }

    private void deleteQuietly(File f) {
        try {
            if (f != null && f.exists()) f.delete();
        } catch (Exception ex) {
            log.warn("Could not delete temp error report: {}", ex.getMessage());
        }
    }
}