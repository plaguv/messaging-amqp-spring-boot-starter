package io.github.plaguv.contract.envelope.metadata;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

class EventMetadataTest {

    private UUID eventId;
    private Instant occurredAt;
    private Class<?> producer;


    @BeforeEach
    void beforeEach() {
        eventId = UUID.randomUUID();
        occurredAt = Instant.now();
        producer = EventMetadataTest.class;
    }

    @Test
    @DisplayName("Constructor should throw if null parameter is present in constructor")
    void throwsOnNull() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new EventMetadata(null, null, null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new EventMetadata(eventId, null, producer));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new EventMetadata(null, occurredAt, producer));
    }

    @Test
    @DisplayName("Constructor should not throw on optional producer field")
    void optionalProducer() {
        Assertions.assertDoesNotThrow(
                () -> new EventMetadata(eventId, occurredAt, null)
        );

        Assertions.assertDoesNotThrow(
                EventMetadata::now);
    }

    @Test
    @DisplayName("Constructor constructs unique event")
    void constructorConstructsUniqueEvent() {
        EventMetadata first = EventMetadata.now();
        EventMetadata second = EventMetadata.now();

        Assertions.assertNotSame(first, second);
        Assertions.assertNotEquals(first, second);

        Assertions.assertNotSame(first.eventId(), second.eventId());
        Assertions.assertNotEquals(first.eventId(), second.eventId());

        Assertions.assertNotSame(first.occurredAt(), second.occurredAt());
    }
}