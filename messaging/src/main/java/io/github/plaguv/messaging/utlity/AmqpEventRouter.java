package io.github.plaguv.messaging.utlity;

import io.github.plaguv.contract.envelope.EventEnvelope;
import io.github.plaguv.messaging.config.properties.AmqpProperties;
import io.github.plaguv.messaging.utlity.helper.ClassNameExtractor;
import jakarta.annotation.Nonnull;

public class AmqpEventRouter implements EventRouter {

    private final String BASE_URI;

    public AmqpEventRouter(AmqpProperties amqpProperties) {
        this.BASE_URI = "%s.%s".formatted(
                amqpProperties.centralExchange(),
                amqpProperties.centralApplication()
        );
    }

    @Override
    public @Nonnull String resolveQueue(@Nonnull EventEnvelope eventEnvelope) {
        return "%s.%s.%s.queue".formatted(
                BASE_URI,
                eventEnvelope.payload().getEventDomain().name().toLowerCase(),
                ClassNameExtractor.extractUpperLower(eventEnvelope.payload().getClass())
        );
    }

    @Override
    public @Nonnull String resolveExchange(@Nonnull EventEnvelope eventEnvelope) {
        return "%s.%s.%s.%s".formatted(
                BASE_URI,
                eventEnvelope.payload().getEventDomain().name().toLowerCase(),
                ClassNameExtractor.extractUpperLower(eventEnvelope.payload().getClass()),
                eventEnvelope.routing().eventDispatchType().name().toLowerCase()
        );
    }

    @Override
    public @Nonnull String resolveRoutingKey(@Nonnull EventEnvelope eventEnvelope) {
        return "%s.%s.%s".formatted(
                eventEnvelope.payload().getEventDomain().name().toLowerCase(),
                ClassNameExtractor.extractUpperLower(eventEnvelope.payload().getClass()),
                eventEnvelope.routing().eventDispatchType().name().toLowerCase()
        );
    }

    @Override
    public @Nonnull String resolveBinding(@Nonnull EventEnvelope eventEnvelope) {
        return "%s.%s.%s".formatted(
                eventEnvelope.payload().getEventDomain().name().toLowerCase(),
                ClassNameExtractor.extractUpperLower(eventEnvelope.payload().getClass()),
                eventEnvelope.routing().eventDispatchType().name().toLowerCase()
        );
    }
}