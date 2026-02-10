package io.github.plaguv.contract.envelope;

import io.github.plaguv.contract.envelope.metadata.EventMetadata;
import io.github.plaguv.contract.envelope.payload.EventPayload;
import io.github.plaguv.contract.envelope.routing.EventScope;
import io.github.plaguv.contract.envelope.routing.EventRouting;
import jakarta.annotation.Nonnull;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public record EventEnvelope(
        EventMetadata metadata,
        EventRouting routing,
        EventPayload payload
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
        private String eventWildcard;

        private Class<?> type;
        private Optional<?> payload = Optional.empty();

        private Builder() {}

        public Builder ofMetadata(EventMetadata eventMetadata) {
            if (eventMetadata == null) {
                throw new IllegalArgumentException("Parameter 'eventMetadata' cannot be null");
            }
            this.eventId = eventMetadata.eventId();
            this.occurredAt = eventMetadata.occurredAt();
            this.producer = eventMetadata.producer();
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
                throw new IllegalArgumentException("Parameter 'eventRouting' cannot be null");
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

        public Builder ofPayload(EventPayload eventPayload) {
            if (eventPayload == null) {
                throw new IllegalArgumentException("Parameter 'eventPayload' cannot be null");
            }
            this.type = eventPayload.type();
            this.payload = eventPayload.payload();
            return this;
        }

        public Builder withPayload(Object payload) {
            this.payload = Optional.of(payload);
            return this;
        }

        public Builder withPayloadType(Class<?> payloadType) {
            this.type = payloadType;
            return this;
        }

        public EventEnvelope build() {
            EventMetadata eventMetadata = new EventMetadata(
                    eventId,
                    occurredAt,
                    producer
            );

            EventRouting eventRouting = new EventRouting(
                    eventScope,
                    eventWildcard
            );

            EventPayload eventPayload = new EventPayload(
                    type,
                    payload
            );

            return new EventEnvelope(
                    eventMetadata,
                    eventRouting,
                    eventPayload
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