package io.github.plaguv.messaging.utlity;

import io.github.plaguv.contract.envelope.EventEnvelope;
import jakarta.annotation.Nonnull;

public interface TopologyDeclarer {

    void declareExchangeIfAbsent(@Nonnull EventEnvelope eventEnvelope);

    void declareQueueIfAbsent(@Nonnull EventEnvelope eventEnvelope);

    void declareBindingIfAbsent(@Nonnull EventEnvelope eventEnvelope);

    void declareAllIfAbsent(@Nonnull EventEnvelope eventEnvelope);
}