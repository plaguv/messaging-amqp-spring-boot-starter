package io.github.plaguv.contract.envelope.metadata;

import java.time.Instant;
import java.util.UUID;

public record EventMetadata(
        UUID eventId,
        Instant occurredAt,
        Class<?> producer
) {
    public EventMetadata {
        if (eventId == null) {
            throw new IllegalArgumentException("EventMetadata attribute 'eventId' cannot be null");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("EventMetadata attribute 'occurredAt' cannot be null");
        }
    }

    public static EventMetadata now() {
        return new EventMetadata(
                UUID.randomUUID(),
                Instant.now(),
                null
        );
    }
}