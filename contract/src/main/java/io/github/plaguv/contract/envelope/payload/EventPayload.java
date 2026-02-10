package io.github.plaguv.contract.envelope.payload;

import io.github.plaguv.contract.event.Event;

import java.util.Optional;

public record EventPayload(
        Class<?> type,
        Optional<?> payload
) {
    public EventPayload {
        if (type == null) {
            throw new IllegalArgumentException("EventPayload attribute 'type' cannot be null");
        }
        if (!type.isAnnotationPresent(Event.class)) {
            throw new IllegalArgumentException("EventPayload attribute 'type' is not annotated with @Event");
        }
        if (payload == null) {
            throw new IllegalArgumentException("EventPayload attribute 'payload' cannot be null");
        }
        if (payload.isPresent() && !payload.get().getClass().isAnnotationPresent(Event.class)) {
            throw new IllegalArgumentException("EventPayload attribute 'payload' is not annotated with @Event");
        }
        if (payload.isPresent() && type != payload.get().getClass()) {
            throw new IllegalArgumentException("EventPayload attribute 'payload' is not of the type attribute 'type' specified");
        }
    }

    public static EventPayload empty(Class<?> payloadType) {
        return new EventPayload(payloadType, Optional.empty());
    }

    public static EventPayload valueOf(Object payload) {
        if (payload == null) {
            throw new IllegalArgumentException("Parameter 'payload' cannot be null");
        }
        return new EventPayload(payload.getClass(), Optional.of(payload));
    }
}