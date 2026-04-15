package com.eventsourcing.workshop.clients;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnProperty(name = "workshop.storage.type", havingValue = "memory", matchIfMissing = true)
public final class MemoryStorageClient implements StorageClient {
    private final Map<String, Map<String, Object>> buckets = new ConcurrentHashMap<>();

    @Override
    public <T> void put(String bucket, String id, T value) {
        buckets.computeIfAbsent(bucket, k -> new ConcurrentHashMap<>()).put(id, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String bucket, String id) {
        Map<String, Object> m = buckets.get(bucket);
        if (m == null)
            return Optional.empty();
        return Optional.ofNullable((T) m.get(id));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> delete(String bucket, String id) {
        Map<String, Object> m = buckets.get(bucket);
        if (m == null)
            return Optional.empty();
        return Optional.ofNullable((T) m.remove(id));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Collection<T> list(String bucket) {
        Map<String, Object> m = buckets.get(bucket);
        if (m == null || m.isEmpty())
            return List.of();

        return (Collection<T>) m.values();
    }

    @Override
    public void clearBucket(String bucket) {
        buckets.remove(bucket);
    }
}