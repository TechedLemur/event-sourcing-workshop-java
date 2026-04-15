package com.eventsourcing.workshop.clients;

import java.util.Collection;
import java.util.Optional;

import com.eventsourcing.workshop.models.Bucket;

public interface StorageClient {
    <T> void put(Bucket<T> bucket, String id, T value);

    <T> Optional<T> get(Bucket<T> bucket, String id);

    <T> Optional<T> delete(Bucket<T> bucket, String id);

    <T> Collection<T> list(Bucket<T> bucket);

    void clearBucket(Bucket<?> bucket);
}