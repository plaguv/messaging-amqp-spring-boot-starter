package io.github.plaguv.messaging.utlity;

import io.github.plaguv.contracts.common.EventEnvelope;
import io.github.plaguv.messaging.config.properties.AmqpProperties;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

@Component
public class AmqpEventRouter implements EventRouter {

    private final AmqpProperties amqpProperties;

    public AmqpEventRouter(AmqpProperties amqpProperties) {
        this.amqpProperties = amqpProperties;
    }

    @Override
    public String resolveQueue(@Nonnull EventEnvelope eventEnvelope) {
        return "%s.%s.%s.queue".formatted(
                amqpProperties.exchange(),
                eventEnvelope.routing().eventType().getEventDomain().name().toLowerCase(),
                eventEnvelope.routing().eventType().name().toLowerCase()
        );
    }

    @Override
    public String resolveExchange(@Nonnull EventEnvelope eventEnvelope) {
        return "%s.%s.%s".formatted(
                amqpProperties.exchange(),
                eventEnvelope.routing().eventType().getEventDomain().name().toLowerCase(),
                eventEnvelope.routing().eventDispatchType().name().toLowerCase()
        );
    }

    @Override
    public String resolveRoutingKey(@Nonnull EventEnvelope eventEnvelope) {
        String domain = eventEnvelope.routing().eventType().getEventDomain().name().toLowerCase();
        String name = eventEnvelope.routing().eventType().name().toLowerCase();

        return switch (eventEnvelope.routing().eventDispatchType()) {
            case DIRECT -> "%s.%s".formatted(domain, name);
            case TOPIC -> "%s.%s".formatted(domain, name);
            case BROADCAST -> "";
        };
    }

    @Override
    public String resolveBinding(@Nonnull EventEnvelope eventEnvelope) {
        String domain = eventEnvelope.routing().eventType().getEventDomain().name().toLowerCase();
        String name = eventEnvelope.routing().eventType().name().toLowerCase();

        return switch (eventEnvelope.routing().eventDispatchType()) {
            case DIRECT -> "%s.%s".formatted(domain, name);
            case TOPIC -> "%s.%s.*".formatted(domain, name);
            case BROADCAST -> "";
        };
    }
}