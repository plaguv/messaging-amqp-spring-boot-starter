package io.github.plaguv.contract.envelope.payload;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.plaguv.contract.event.Event;

public record EventPayload(
        Class<?> contentType,

        @JsonTypeInfo(
                use = JsonTypeInfo.Id.CLASS,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "contentType"
        )
        Object content
) {
    public EventPayload {
        if (contentType == null) {
            throw new IllegalArgumentException("EventPayload attribute 'contentType' cannot be null");
        }
        if (!contentType.isAnnotationPresent(Event.class)) {
            throw new IllegalArgumentException("EventPayload attribute 'contentType' is not annotated with @Event");
        }
        if (content != null && !content.getClass().isAnnotationPresent(Event.class)) {
            throw new IllegalArgumentException("EventPayload attribute 'content' is not annotated with @Event");
        }
        if (content != null && content.getClass() != contentType) {
            throw new IllegalArgumentException("EventPayload attribute 'content' is not of the contentType attribute 'contentType' specified");
        }
    }

    public static EventPayload empty(Class<?> payloadType) {
        return new EventPayload(payloadType, null);
    }

    public static EventPayload valueOf(Object payload) {
        if (payload == null) {
            throw new IllegalArgumentException("Parameter 'content' cannot be null");
        }
        return new EventPayload(
                payload.getClass(),
                payload
        );
    }
}