package io.github.plaguv.contract.envelope;

import io.github.plaguv.contract.envelope.metadata.EventMetadata;
import io.github.plaguv.contract.envelope.payload.EventPayload;
import io.github.plaguv.contract.envelope.routing.EventScope;
import io.github.plaguv.contract.event.pos.StoreOpenedEvent;
import io.github.plaguv.contract.envelope.routing.EventRouting;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EventEnvelopeTest {

    private EventMetadata eventMetadata;
    private EventRouting eventRouting;
    private EventPayload eventPayload;

    @BeforeEach
    void beforeEach() {
        eventMetadata = EventMetadata.now();

        eventRouting = new EventRouting(
                EventScope.BROADCAST,
                null
        );

        eventPayload = EventPayload.valueOf(new StoreOpenedEvent(5L));
    }

    @Test
    @DisplayName("Constructor should throw if null parameter in constructor")
    void throwsOnNull() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new EventEnvelope(null, null, null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new EventEnvelope(eventMetadata, eventRouting, null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new EventEnvelope(eventMetadata, null, eventPayload));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new EventEnvelope(null, eventRouting, eventPayload));
    }

    @Test
    @DisplayName("Builder instances should differ")
    void builderInstanceShouldDiffer() {
        EventEnvelope.Builder empty = EventEnvelope.builder();
        EventEnvelope.Builder withDefaults = EventEnvelope.builderWithDefaults();

        Assertions.assertNotEquals(empty, withDefaults);
        Assertions.assertNotSame(empty, withDefaults);
    }

    @Test
    @DisplayName("Builder should throw if null parameter gets parsed to constructor")
    void throwsOnNullWithBuilder() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> EventEnvelope.builder()
                        .build());
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> EventEnvelope.builderWithDefaults()
                        .build());
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> EventEnvelope.builder()
                        .ofEventMetadata(null)
                        .ofEventRouting(null)
                        .ofEventPayload(null)
                        .build());
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> EventEnvelope.builder()
                        .ofEventMetadata(eventMetadata)
                        .ofEventRouting(eventRouting)
                        .ofEventPayload(null)
                        .build());
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> EventEnvelope.builder()
                        .ofEventMetadata(eventMetadata)
                        .ofEventRouting(null)
                        .ofEventPayload(eventPayload)
                        .build());
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> EventEnvelope.builder()
                        .ofEventMetadata(null)
                        .ofEventRouting(eventRouting)
                        .ofEventPayload(eventPayload)
                        .build());
    }

    @Test
    @DisplayName("Builder should keep values it gets handed in built EventEnvelope")
    void builderKeepsValues() {
        EventEnvelope eventEnvelope = EventEnvelope.builder()
                .ofEventMetadata(eventMetadata)
                .ofEventRouting(eventRouting)
                .ofEventPayload(eventPayload)
                .build();

        Assertions.assertEquals(eventMetadata, eventEnvelope.metadata());
        Assertions.assertEquals(eventRouting, eventEnvelope.routing());
        Assertions.assertEquals(eventPayload, eventEnvelope.payload());

        eventEnvelope = EventEnvelope.builder()
                .withEventId(eventMetadata.eventId())
                .withOccurredAt(eventMetadata.occurredAt())
                .withProducer(eventMetadata.producer())
                .withEventScope(eventRouting.scope())
                .withWildcard(eventRouting.wildcard())
                .withPayloadType(eventPayload.type())
                .withPayload(eventPayload.payload())
                .build();

        Assertions.assertEquals(eventMetadata, eventEnvelope.metadata());
        Assertions.assertEquals(eventRouting, eventEnvelope.routing());
        Assertions.assertEquals(eventPayload, eventEnvelope.payload());
    }
}