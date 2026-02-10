package io.github.plaguv.contract;

import io.github.plaguv.contract.envelope.EventEnvelope;
import io.github.plaguv.contract.envelope.payload.EventInstance;
import io.github.plaguv.contract.envelope.metadata.EventMetadata;
import io.github.plaguv.contract.envelope.metadata.EventVersion;
import io.github.plaguv.contract.envelope.routing.EventScope;
import io.github.plaguv.contract.event.pos.StoreOpenedEvent;
import io.github.plaguv.contract.envelope.routing.EventRouting;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

class EventEnvelopeTest {

    private EventMetadata metadata;
    private EventRouting routing;
    private EventInstance payload;

    @BeforeEach
    void beforeEach() {
        metadata = new EventMetadata(
                UUID.randomUUID(),
                new EventVersion(1),
                Instant.now(),
                EventEnvelopeTest.class
        );

        routing = new EventRouting(
          EventScope.BROADCAST
        );

        payload = new StoreOpenedEvent(
                5L)
        ;
    }

    @Test
    @DisplayName("Should throw if null parameter in constructor")
    void throwsOnNull() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new EventEnvelope(null, null, null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new EventEnvelope(metadata, routing, null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new EventEnvelope(metadata, null, payload));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new EventEnvelope(null, routing, payload));
    }

    @Test
    @DisplayName("Should throw if null parameter in builder")
    void builderThrowsOnNull() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> EventEnvelope.builder()
                        .build());
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> EventEnvelope.builder()
                        .ofMetadata(metadata)
                        .ofRouting(routing)
                        .ofPayload(null)
                        .build());
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> EventEnvelope.builder()
                        .ofMetadata(metadata)
                        .ofRouting(null)
                        .ofPayload(payload)
                        .build());
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> EventEnvelope.builder()
                        .ofMetadata(null)
                        .ofRouting(routing)
                        .ofPayload(payload)
                        .build());
    }

    @Test
    @DisplayName("Builder should keep given values")
    void builderConstructsWithGivenValues() {
        EventEnvelope eventEnvelope = EventEnvelope.builder()
                .ofMetadata(metadata)
                .ofRouting(routing)
                .ofPayload(payload)
                .build();

        Assertions.assertEquals(metadata, eventEnvelope.metadata());
        Assertions.assertEquals(routing, eventEnvelope.routing());
        Assertions.assertEquals(payload, eventEnvelope.payload());
    }

    @Test
    @DisplayName("Builder should construct with only method parameters")
    void builderConstructsWithMethodParametersOnly() {
        Assertions.assertDoesNotThrow(() ->
                EventEnvelope.builder()
                        .withEventVersion(new EventVersion(1))
                        .withProducer(EventEnvelopeTest.class)
                        .withEventDispatchType(EventScope.TARGET)
                        .ofPayload(payload)
                        .build());
    }

    @Test
    @DisplayName("Builder should construct with minimal parameters")
    void builderConstructsWithMinimalValues() {
        metadata = new EventMetadata( // As opposed to providing the full metadata, with attributes eventId and occurredAt specified
                EventVersion.valueOf(1),
                EventEnvelopeTest.class
        );

        Assertions.assertDoesNotThrow(() ->
                EventEnvelope.builder()
                        .ofMetadata(metadata)
                        .ofRouting(routing)
                        .ofPayload(payload)
                        .build());
    }
}