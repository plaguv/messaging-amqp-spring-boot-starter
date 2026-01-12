package io.github.plaguv.messaging.listener;

import io.github.plaguv.contracts.DomainEvent;

public interface EventListener<T extends DomainEvent> {
    void handleMessage(T message);
}