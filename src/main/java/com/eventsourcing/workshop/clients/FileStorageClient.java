package com.eventsourcing.workshop.clients;

import com.eventsourcing.workshop.models.Bucket;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
@ConditionalOnProperty(name = "workshop.storage.type", havingValue = "file")
public final class FileStorageClient implements StorageClient {

    private final ObjectMapper objectMapper;
    private final Path storageFile;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private Map<String, Map<String, JsonNode>> data;

    public FileStorageClient(
            ObjectMapper objectMapper,
            @Value("${workshop.storage.file:data/workshop-storage.json}") String storageFile) {
        this.objectMapper = objectMapper;
        this.storageFile = Path.of(storageFile).toAbsolutePath().normalize();
        this.data = load();
    }

    private Map<String, Map<String, JsonNode>> load() {
        lock.writeLock().lock();
        try {
            if (!Files.isRegularFile(storageFile)) {
                return new LinkedHashMap<>();
            }
            try {
                Map<String, Map<String, JsonNode>> parsed = objectMapper.readValue(
                        storageFile.toFile(),
                        new TypeReference<>() {
                        });
                return parsed != null ? new LinkedHashMap<>(parsed) : new LinkedHashMap<>();
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to read storage file: " + storageFile, e);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void persistLocked() {
        try {
            Path parent = storageFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(storageFile.toFile(), data);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write storage file: " + storageFile, e);
        }
    }

    private JavaType valueType(Bucket<?> bucket) {
        return objectMapper.constructType(bucket.getType());
    }

    @Override
    public <T> void put(Bucket<T> bucket, String id, T value) {
        JsonNode node = objectMapper.valueToTree(value);
        lock.writeLock().lock();
        try {
            data.computeIfAbsent(bucket.getKey(), b -> new LinkedHashMap<>()).put(id, node);
            persistLocked();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(Bucket<T> bucket, String id) {
        lock.readLock().lock();
        try {
            Map<String, JsonNode> bucketMap = data.get(bucket.getKey());
            if (bucketMap == null) {
                return Optional.empty();
            }
            JsonNode node = bucketMap.get(id);
            if (node == null || node.isNull()) {
                return Optional.empty();
            }
            Object converted = objectMapper.convertValue(node, valueType(bucket));
            return Optional.of((T) converted);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> delete(Bucket<T> bucket, String id) {
        lock.writeLock().lock();
        try {
            Map<String, JsonNode> bucketMap = data.get(bucket.getKey());
            if (bucketMap == null) {
                return Optional.empty();
            }
            JsonNode removed = bucketMap.remove(id);
            if (removed == null) {
                return Optional.empty();
            }
            if (bucketMap.isEmpty()) {
                data.remove(bucket.getKey());
            }
            persistLocked();
            Object converted = objectMapper.convertValue(removed, valueType(bucket));
            return Optional.of((T) converted);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Collection<T> list(Bucket<T> bucket) {
        lock.readLock().lock();
        try {
            Map<String, JsonNode> bucketMap = data.get(bucket.getKey());
            if (bucketMap == null || bucketMap.isEmpty()) {
                return List.of();
            }
            JavaType type = valueType(bucket);
            List<T> out = new ArrayList<>(bucketMap.size());
            for (JsonNode node : bucketMap.values()) {
                out.add((T) objectMapper.convertValue(node, type));
            }
            return Collections.unmodifiableList(out);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void clearBucket(Bucket<?> bucket) {
        lock.writeLock().lock();
        try {
            data.remove(bucket.getKey());
        } finally {
            lock.writeLock().unlock();
        }
    }
}
