package com.eventsourcing.workshop.events;

import java.util.*;

// This is a bit nasty, but done to simplify the workshop
// This automagically registers all event types to a hash map so we can automatically deserialize it
public final class StoreEventDataTypesClassResolver {
    private StoreEventDataTypesClassResolver() {}

    private static final Map<String, Class<? extends StoreEventData>> TYPE_TO_CLASS = build();

    public static Class<? extends StoreEventData> classFor(String type) {
        Class<? extends StoreEventData> c = TYPE_TO_CLASS.get(type);
        if (c == null) throw new IllegalArgumentException("Unknown event type: " + type);
        return c;
    }

    private static Map<String, Class<? extends StoreEventData>> build() {
        Map<String, Class<? extends StoreEventData>> m = new HashMap<>();
        Deque<Class<?>> q = new ArrayDeque<>();
        q.add(StoreEventData.class);

        while (!q.isEmpty()) {
            Class<?> c = q.removeFirst();
            Class<?>[] permitted = c.getPermittedSubclasses();
            if (permitted == null || permitted.length == 0) {
                @SuppressWarnings("unchecked")
                Class<? extends StoreEventData> leaf = (Class<? extends StoreEventData>) c;

                String type = leaf.getSimpleName();
                if (type.endsWith("Event")) type = type.substring(0, type.length() - "Event".length());

                Class<? extends StoreEventData> prev = m.putIfAbsent(type, leaf);
                if (prev != null && prev != leaf) {
                    throw new IllegalStateException("Duplicate event type '" + type + "': " + prev + " vs " + leaf);
                }
            } else {
                Collections.addAll(q, permitted);
            }
        }
        return Map.copyOf(m);
    }
}
