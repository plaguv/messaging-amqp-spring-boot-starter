package io.github.plaguv.messaging.utlity;

import io.github.plaguv.contract.envelope.EventEnvelope;
import jakarta.annotation.Nonnull;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;

import java.util.Set;

public interface TopologyDeclarer {

    Exchange declareExchangeIfAbsent(@Nonnull EventEnvelope eventEnvelope);

    Queue declareQueueIfAbsent(@Nonnull EventEnvelope eventEnvelope);

    Set<Binding> declareBindingIfAbsent(@Nonnull EventEnvelope eventEnvelope);
}