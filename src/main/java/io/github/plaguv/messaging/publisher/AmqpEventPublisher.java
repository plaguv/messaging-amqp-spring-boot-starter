package io.github.plaguv.messaging.publisher;

import io.github.plaguv.contracts.common.EventEnvelope;
import io.github.plaguv.messaging.config.properties.AmqpProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.Date;

@Component
public class AmqpEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(AmqpEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final AmqpProperties amqpProperties;
    private final ObjectMapper objectMapper;

    public AmqpEventPublisher(RabbitTemplate rabbitTemplate, AmqpProperties amqpProperties, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.amqpProperties = amqpProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(EventEnvelope<?> eventEnvelope) {
        if (eventEnvelope == null) {
            throw new IllegalArgumentException("eventEnvelope cannot be null");
        }

        try {
            rabbitTemplate.send(
                    amqpProperties.getExchange(eventEnvelope.metadata().eventScope()),
                    eventEnvelope.metadata().eventType().eventRoutingKey(),
                    buildMessage(eventEnvelope)
            );
        } catch (Exception exception) {
            log.atError().log("Failed to publish event {}: {}", eventEnvelope.metadata().eventId(), exception.getMessage(), exception);
        }
    }

    private Message buildMessage(EventEnvelope<?> eventEnvelope) {
        // Header
        MessageProperties props = new MessageProperties();
        // Mandatory
        props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        props.setContentEncoding("utf-8");
        props.setMessageId(eventEnvelope.metadata().eventId().toString());
        props.setCorrelationId(eventEnvelope.metadata().eventId().toString());
        props.setTimestamp(Date.from(eventEnvelope.metadata().occurredAt()));
        // Optional
        props.setHeader(
                "x-event-type",
                eventEnvelope.metadata().eventType().name()
        );
        props.setHeader(
                "x-event-domain",
                eventEnvelope.metadata().eventType().eventDomain().name()
        );
        props.setHeader(
                "x-event-version",
                eventEnvelope.metadata().eventVersion().toString()
        );
        props.setHeader(
                "x-event-scope",
                eventEnvelope.metadata().eventScope().name()
        );
        props.setHeader(
                "x-producer",
                eventEnvelope.metadata().producer().getName()
        );
        try {
            return new Message(objectMapper.writeValueAsBytes(eventEnvelope), props);
        } catch (JacksonException jacksonException) {
            throw new IllegalStateException("Failed to serialize EventEnvelope: " + eventEnvelope.metadata().eventId(), jacksonException);
        }
    }
}