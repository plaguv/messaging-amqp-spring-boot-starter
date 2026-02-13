package io.github.plaguv.contract.envelope.payload;

import io.github.plaguv.contract.event.pos.StoreClosedEvent;
import io.github.plaguv.contract.event.pos.StoreOpenedEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EventPayloadTest {

    private Class<?> payloadType;
    private Object payload;

    @BeforeEach
    void beforeEach() {
        payloadType = StoreOpenedEvent.class;
        payload = new StoreOpenedEvent(5L);
    }

    @Test
    @DisplayName("Constructor should throw if null parameter is present in constructor")
    void throwsOnNull() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new EventPayload(null, null));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new EventPayload((Object) null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> EventPayload.empty(null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new EventPayload(null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> EventPayload.valueOf(null));
    }

    @Test
    @DisplayName("Constructor accepts payloads with no type specified")
    void constructorInfersContentTypeFromPayload() {
        Assertions.assertDoesNotThrow(
                () -> new EventPayload(null, payload));
        Assertions.assertDoesNotThrow(
                () -> new EventPayload(payload));
        Assertions.assertDoesNotThrow(
                () -> EventPayload.valueOf(payload));
    }


    @Test
    @DisplayName("Constructor accepts empty payloads, that specify a type")
    void constructorAcceptsEmptyPayloads() {
        Assertions.assertDoesNotThrow(
                () -> new EventPayload(payloadType, null));
        Assertions.assertDoesNotThrow(
                () -> new EventPayload(payloadType));
        Assertions.assertDoesNotThrow(
                () -> EventPayload.empty(payloadType));
    }

    @Test
    @DisplayName("Constructor should only accept classes that implement @Event")
    void constructorAcceptsOnlyEventContent() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> EventPayload.empty(Object.class));
        Assertions.assertDoesNotThrow(
                () -> EventPayload.empty(StoreOpenedEvent.class));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> EventPayload.valueOf(new Object()));
        Assertions.assertDoesNotThrow(
                () -> EventPayload.valueOf(payload));
    }

    @Test
    @DisplayName("Constructor should only accept parameters that point towards the same event (contentType/content)")
    void constructorAcceptsOnlyMatchingEventContent() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new EventPayload(StoreClosedEvent.class, payload));

        Assertions.assertDoesNotThrow(
                () -> new EventPayload(payloadType, payload));
    }
}