package io.github.plaguv.messaging.utlity;

import io.github.plaguv.contract.envelope.EventEnvelope;
import io.github.plaguv.contract.event.Event;
import io.github.plaguv.contract.envelope.routing.EventScope;
import io.github.plaguv.messaging.config.properties.AmqpProperties;
import io.github.plaguv.messaging.utlity.helper.ClassNameExtractor;
import jakarta.annotation.Nonnull;

import java.util.HashSet;
import java.util.Set;

public class AmqpEventRouter implements EventRouter {

    private final AmqpProperties amqpProperties;

    public AmqpEventRouter(AmqpProperties amqpProperties) {
        this.amqpProperties = amqpProperties;
    }

    @Override
    public @Nonnull String resolveQueue(@Nonnull EventEnvelope eventEnvelope) {
        return "%s.%s.%s.queue".formatted(
                amqpProperties.centralApplication(),
                eventEnvelope.payload().contentType().getAnnotation(Event.class).domain().name().toLowerCase(),
                ClassNameExtractor.extractUpperLower(eventEnvelope.payload().contentType())
        );
    }

    @Override
    public @Nonnull String resolveExchange(@Nonnull EventEnvelope eventEnvelope) {
        return "%s.events".formatted(
                amqpProperties.centralExchange()
        );
    }

    @Override
    public @Nonnull String resolveRoutingKey(@Nonnull EventEnvelope eventEnvelope) {
        return "%s.%s".formatted(
                eventEnvelope.payload().contentType().getAnnotation(Event.class).domain().name().toLowerCase(),
                ClassNameExtractor.extractUpperLower(eventEnvelope.payload().contentType())
        ).concat(createScopingSuffix(eventEnvelope));
    }

    @Override
    public @Nonnull Set<String> resolveBindingKey(@Nonnull EventEnvelope eventEnvelope) {
        Set<String> bindings = new HashSet<>();
        for (EventScope eventScope : EventScope.values()) {
            bindings.add(
                    "%s.%s".formatted(
                            eventEnvelope.payload().contentType().getAnnotation(Event.class).domain().name().toLowerCase(),
                            ClassNameExtractor.extractUpperLower(eventEnvelope.payload().contentType())
                    ).concat(createScopingSuffix(eventScope, amqpProperties.centralApplication())));
        }
        return bindings;
    }

    private String createScopingSuffix(EventScope eventScope, String eventWildcard) {
        return switch (eventScope) {
            case TARGET, GROUP -> ".%s.%s".formatted(
                    eventScope.name().toLowerCase(),
                    eventWildcard
            );
            case BROADCAST -> "";
        };
    }

    private String createScopingSuffix(EventEnvelope eventEnvelope) {
        return createScopingSuffix(
                eventEnvelope.routing().scope(),
                eventEnvelope.routing().wildcard()
        );
    }
}