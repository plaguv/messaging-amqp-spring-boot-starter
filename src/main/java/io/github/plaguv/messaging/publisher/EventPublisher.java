package io.github.plaguv.messaging.publisher;

import io.github.plaguv.contracts.common.EventEnvelope;

public interface EventPublisher {
    void publish(EventEnvelope<?> eventEnvelope);
}