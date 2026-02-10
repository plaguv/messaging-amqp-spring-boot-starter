package io.github.plaguv.messaging.utlity;

import io.github.plaguv.contract.envelope.EventEnvelope;
import io.github.plaguv.contract.envelope.payload.Event;
import io.github.plaguv.contract.envelope.routing.EventRouting;
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
                eventEnvelope.payload().getClass().getAnnotation(Event.class).domain().name().toLowerCase(),
                ClassNameExtractor.extractUpperLower(eventEnvelope.payload().getClass())
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
                eventEnvelope.payload().getClass().getAnnotation(Event.class).domain().name().toLowerCase(),
                ClassNameExtractor.extractUpperLower(eventEnvelope.payload().getClass())
        ).concat(createScopingBranchKey(eventEnvelope));
    }

    @Override
    public @Nonnull Set<String> resolveBindingKey(@Nonnull EventEnvelope eventEnvelope) {
        Set<String> bindings = new HashSet<>();
        for (EventScope eventScope : EventScope.values()) {
            bindings.add(
                    resolveRoutingKey(
                            copyEventEnvelopeWithScope(eventEnvelope, eventScope)));
        }
        return bindings;
    }

    private String createScopingBranchKey(EventEnvelope eventEnvelope) {
        return switch (eventEnvelope.routing().eventScope()) {
            case TARGET, GROUP -> ".%s.%s".formatted(
                    eventEnvelope.routing().eventScope().name().toLowerCase(),
                    eventEnvelope.routing().eventWildcard()
            );
            case BROADCAST -> "";
        };
    }

    private EventEnvelope copyEventEnvelopeWithScope(EventEnvelope eventEnvelope, EventScope eventScope) {
        return new EventEnvelope(
                eventEnvelope.metadata(),
                new EventRouting(
                        eventScope,
                        eventEnvelope.routing().eventWildcard()
                ),
                eventEnvelope.payload()
        );
    }
}