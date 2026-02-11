package io.github.plaguv.messaging.publisher;

import io.github.plaguv.contract.envelope.EventEnvelope;
import io.github.plaguv.contract.event.Event;
import io.github.plaguv.messaging.utlity.helper.ClassNameExtractor;
import io.github.plaguv.messaging.utlity.EventRouter;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.aop.support.AopUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.Date;

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
            rabbitTemplate.convertAndSend(
                    eventRouter.resolveExchange(eventEnvelope),
                    eventRouter.resolveRoutingKey(eventEnvelope),
                    buildMessage(eventEnvelope)
            );
            log.atInfo().log("Successfully send message");
        } catch (Exception e) {
            log.atError().log("Failed to send AMQP message", e);
        }
    }

    private Message buildMessage(EventEnvelope eventEnvelope) {
        // Header
        MessageProperties props = new MessageProperties();
        // Mandatory Header Content
        props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        props.setContentEncoding("utf-8");
        props.setMessageId(eventEnvelope.metadata().eventId().toString());
        props.setCorrelationId(eventEnvelope.metadata().eventId().toString());
        props.setTimestamp(Date.from(eventEnvelope.metadata().occurredAt()));
        // Optional Header Content
        props.setHeader(
                "x-event-type",
                ClassNameExtractor.extractUpperLower(eventEnvelope.payload().type())
        );
        props.setHeader(
                "x-event-domain",
                eventEnvelope.payload().getClass().getAnnotation(Event.class).domain().name().toLowerCase()
        );
        props.setHeader(
                "x-event-version",
                eventEnvelope.payload().getClass().getAnnotation(Event.class).version()
        );
        props.setHeader(
                "x-producer",
                eventEnvelope.metadata().producer() != null ?
                        ClassNameExtractor.extractUpperLower(eventEnvelope.metadata().producer()) :
                        ClassNameExtractor.extractUpperLower(AopUtils.getTargetClass(this)) // TODO: this should be a configurable option
        );
        try {
            return new Message(
                    objectMapper.writeValueAsBytes(eventEnvelope),
                    props // header
            );
        } catch (JacksonException jacksonException) {
            throw new IllegalStateException("Failed to serialize message", jacksonException);
        }
    }
}