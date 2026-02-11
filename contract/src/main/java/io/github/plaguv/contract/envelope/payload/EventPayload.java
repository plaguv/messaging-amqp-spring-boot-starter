package io.github.plaguv.contract.envelope.payload;

import io.github.plaguv.contract.event.Event;

public record EventPayload(
        Class<?> type,
        Object payload
) {
    public EventPayload {
        if (type == null) {
            throw new IllegalArgumentException("EventPayload attribute 'type' cannot be null");
        }
        if (!type.isAnnotationPresent(Event.class)) {
            throw new IllegalArgumentException("EventPayload attribute 'type' is not annotated with @Event");
        }
        if (payload != null && !payload.getClass().isAnnotationPresent(Event.class)) {
            throw new IllegalArgumentException("EventPayload attribute 'payload' is not annotated with @Event");
        }
        if (payload != null && payload.getClass() != type) {
            throw new IllegalArgumentException("EventPayload attribute 'payload' is not of the type attribute 'type' specified");
        }
    }

    public static EventPayload empty(Class<?> payloadType) {
        return new EventPayload(payloadType, null);
    }

    public static EventPayload valueOf(Object payload) {
        if (payload == null) {
            throw new IllegalArgumentException("Parameter 'payload' cannot be null");
        }
        return new EventPayload(
                payload.getClass(),
                payload
        );
    }
}