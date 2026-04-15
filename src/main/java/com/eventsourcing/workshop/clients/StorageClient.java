package com.eventsourcing.workshop.clients;

import java.util.Collection;
import java.util.Optional;

public interface StorageClient {
    <T> void put(String bucket, String id, T value);

    <T> Optional<T> get(String bucket, String id);

    <T> Optional<T> delete(String bucket, String id);

    <T> Collection<T> list(String bucket);

    void clearBucket(String bucket);
}