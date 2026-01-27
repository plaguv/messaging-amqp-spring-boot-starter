package io.github.plaguv.messaging.utlity;

import io.github.plaguv.contracts.common.EventEnvelope;
import jakarta.annotation.Nonnull;

public interface TopologyDeclarer {

    /**
     *
     * @param eventEnvelope e
     */
    void declareExchangeIfAbsent(@Nonnull EventEnvelope eventEnvelope);

    /**
     *
     * @param eventEnvelope e
     */
    void declareQueueIfAbsent(@Nonnull EventEnvelope eventEnvelope);

    /**
     *
     * @param eventEnvelope e
     */
    void declareBindingIfAbsent(@Nonnull EventEnvelope eventEnvelope);

    /**
     *
     * @param eventEnvelope e
     */
    void declareAllIfAbsent(@Nonnull EventEnvelope eventEnvelope);
}