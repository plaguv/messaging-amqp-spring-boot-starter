package io.github.plaguv.messaging.listener;

import io.github.plaguv.contracts.DomainEvent;
import io.github.plaguv.messaging.config.properties.AmqpProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public abstract class AmqpEventListener<T extends DomainEvent> implements EventListener<T> {

    private final RabbitTemplate rabbitTemplate;
    private final AmqpProperties amqpProperties;

    public AmqpEventListener(RabbitTemplate rabbitTemplate, AmqpProperties amqpProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.amqpProperties = amqpProperties;
    }

    @Override
    public void handleMessage(DomainEvent message) {

    }
}