package io.github.plaguv.contract.envelope;

import io.github.plaguv.contract.envelope.payload.Event;
import io.github.plaguv.contract.envelope.metadata.EventMetadata;
import io.github.plaguv.contract.envelope.routing.EventScope;
import io.github.plaguv.contract.envelope.routing.EventRouting;
import jakarta.annotation.Nonnull;

import java.time.Instant;
import java.util.UUID;

public record EventEnvelope(
        EventMetadata metadata,
        EventRouting routing,
        Object payload
) {
    public EventEnvelope {
        if (metadata == null) {
            throw new IllegalArgumentException("EventEnvelope attribute 'metadata' cannot be null");
        }
        if (routing == null) {
            throw new IllegalArgumentException("EventEnvelope attribute 'routing' cannot be null");
        }
        if (payload == null) {
            throw new IllegalArgumentException("EventEnvelope attribute 'payload' cannot be null");
        }
        if (!payload.getClass().isAnnotationPresent(Event.class)) {
            throw new IllegalArgumentException("EventEnvelope attribute 'payload' must be annotated with @Event. Received '%s'.".formatted(payload.getClass()));
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        // Default values
        private UUID eventId = UUID.randomUUID();
        private Instant occurredAt = Instant.now();
        private Object producer;

        private EventScope eventScope = EventScope.BROADCAST;
        private String eventWildcard = "";

        private Object payload;

        private Builder() {
        }

        public Builder ofMetadata(EventMetadata metadata) {
            if (metadata == null) {
                throw new IllegalArgumentException("EventMetadata attribute 'metadata' cannot be null");
            }
            this.eventId = metadata.eventId();
            this.occurredAt = metadata.occurredAt();
            this.producer = metadata.producer();
            return this;
        }

        public Builder withEventId(UUID eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder withOccurredAt(Instant occurredAt) {
            this.occurredAt = occurredAt;
            return this;
        }

        public Builder withProducer(Class<?> producer) {
            this.producer = producer;
            return this;
        }

        public Builder ofRouting(EventRouting eventRouting) {
            if (eventRouting == null) {
                throw new IllegalArgumentException("EventRouting attribute 'routing' cannot be null");
            }
            this.eventScope = eventRouting.eventScope();
            this.eventWildcard = eventRouting.eventWildcard();
            return this;
        }

        public Builder withEventScope(EventScope eventScope) {
            this.eventScope = eventScope;
            return this;
        }

        public Builder withWildcard(String eventWildcard) {
            this.eventWildcard = eventWildcard;
            return this;
        }

        public Builder ofPayload(Object payload) {
            if (payload == null) {
                throw new IllegalArgumentException("EventPayload attribute 'payload' cannot be null");
            }
            this.payload = payload;
            return this;
        }

        public EventEnvelope build() {
            EventMetadata metadata = new EventMetadata(
                    eventId,
                    occurredAt,
                    producer
            );

            EventRouting routing = new EventRouting(
                    eventScope,
                    eventWildcard
            );

            return new EventEnvelope(
                    metadata,
                    routing,
                    payload
            );
        }
    }

    @Override
    public @Nonnull String toString() {
        return "EventEnvelope{" +
                "metadata=" + metadata +
                ", routing=" + routing +
                ", payload=" + payload +
                '}';
    }
}