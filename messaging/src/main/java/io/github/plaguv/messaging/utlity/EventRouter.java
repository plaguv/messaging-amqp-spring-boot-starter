package io.github.plaguv.messaging.utlity;

import io.github.plaguv.contract.envelope.EventEnvelope;
import jakarta.annotation.Nonnull;

public interface EventRouter {

    @Nonnull String resolveQueue(@Nonnull EventEnvelope eventEnvelope);

    @Nonnull String resolveExchange(@Nonnull EventEnvelope eventEnvelope);

    @Nonnull String resolveRoutingKey(@Nonnull EventEnvelope eventEnvelope);

    @Nonnull String resolveBinding(@Nonnull EventEnvelope eventEnvelope);
}