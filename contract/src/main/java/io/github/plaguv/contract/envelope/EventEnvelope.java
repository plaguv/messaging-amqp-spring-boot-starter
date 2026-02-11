package io.github.plaguv.contract.envelope;

import io.github.plaguv.contract.envelope.metadata.EventMetadata;
import io.github.plaguv.contract.envelope.payload.EventPayload;
import io.github.plaguv.contract.envelope.routing.EventScope;
import io.github.plaguv.contract.envelope.routing.EventRouting;

import java.time.Instant;
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

    public static Builder builderWithDefaults() {
        return new Builder()
                .ofEventMetadata(EventMetadata.now())
                .ofEventRouting(new EventRouting(
                        EventScope.BROADCAST,
                        null
                ));
    }

    public static final class Builder {

        // EventMetadata fields
        private UUID eventId;
        private Instant occurredAt;
        private Class<?> producer;

        // EventRouting fields
        private EventScope eventScope;
        private String eventWildcard;

        // EventPayload fields
        private Class<?> payloadType;
        private Object payload;

        private Builder() {
        }

        public Builder ofEventMetadata(EventMetadata eventMetadata) {
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

        public Builder ofEventRouting(EventRouting eventRouting) {
            if (eventRouting == null) {
                throw new IllegalArgumentException("Parameter 'eventRouting' cannot be null");
            }
            this.eventScope = eventRouting.scope();
            this.eventWildcard = eventRouting.wildcard();
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

        public Builder ofEventPayload(EventPayload eventPayload) {
            if (eventPayload == null) {
                throw new IllegalArgumentException("Parameter 'eventPayload' cannot be null");
            }
            this.payloadType = eventPayload.type();
            this.payload = eventPayload.payload();
            return this;
        }

        public Builder withPayload(Object payload) {
            this.payload = payload;
            return this;
        }

        public Builder withPayloadType(Class<?> payloadType) {
            this.payloadType = payloadType;
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
                    payloadType,
                    payload
            );

            return new EventEnvelope(
                    eventMetadata,
                    eventRouting,
                    eventPayload
            );
        }
    }
}