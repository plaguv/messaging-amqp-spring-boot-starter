package io.github.plaguv.messaging.publisher;

import io.github.plaguv.contract.envelope.EventEnvelope;
import io.github.plaguv.messaging.utlity.helper.ClassNameExtractor;
import io.github.plaguv.messaging.utlity.EventRouter;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
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
    private final EventRouter eventRouter;
    private final ObjectMapper objectMapper;

    public AmqpEventPublisher(RabbitTemplate rabbitTemplate, EventRouter eventRouter, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.eventRouter = eventRouter;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishMessage(@Nonnull EventEnvelope eventEnvelope) {
        try {
            String exchange = eventRouter.resolveExchange(eventEnvelope);
            String routingKey = eventRouter.resolveRoutingKey(eventEnvelope);

            rabbitTemplate.convertAndSend(
                    exchange,
                    routingKey,
                    buildMessage(eventEnvelope)
            );
        } catch (AmqpException e) {
            log.atError().log("Failed to send AMQP message", e);
        }
    }

    private Message buildMessage(@Nonnull EventEnvelope eventEnvelope) {
        // Header
        MessageProperties props = new MessageProperties();
        // Mandatory Header Content
        props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        props.setContentEncoding("utf-8" );
        props.setMessageId(eventEnvelope.metadata().eventId().toString());
        props.setCorrelationId(eventEnvelope.metadata().eventId().toString());
        props.setTimestamp(Date.from(eventEnvelope.metadata().occurredAt()));
        // Optional Header Content
        props.setHeader(
                "x-event-type",
                ClassNameExtractor.extractUpperLower(eventEnvelope.payload().getClass())
        );
        props.setHeader(
                "x-event-domain",
                eventEnvelope.payload().getEventDomain().name().toLowerCase()
        );
        props.setHeader(
                "x-event-version",
                eventEnvelope.metadata().eventVersion().toString()
        );
        props.setHeader(
                "x-producer",
                ClassNameExtractor.extractUpperLower(eventEnvelope.metadata().producer())
        );
        try {
            return new Message(
                    objectMapper.writeValueAsBytes(eventEnvelope), // Write EventInstance into ByteStream as body
                    props // header
            );
        } catch (JacksonException jacksonException) {
            throw new IllegalStateException("Failed to serialize message", jacksonException);
        }
    }
}