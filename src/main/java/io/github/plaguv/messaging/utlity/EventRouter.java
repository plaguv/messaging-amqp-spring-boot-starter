package io.github.plaguv.messaging.utlity;

import io.github.plaguv.contracts.common.EventEnvelope;
import jakarta.annotation.Nonnull;

public interface EventRouter {

    /**
     * @param eventEnvelope the instance of a message from which the queue should be derived from
     * @return the queue of the event as a {@link String} in the form of: <br>
     * {@code <application>.<domain>.<eventName>.queue} or <br>
     * {@code project.store.store_close.queue}
     */
    String resolveQueue(@Nonnull EventEnvelope eventEnvelope);

    /**
     * @param eventEnvelope the instance of a message from which the exchange should be derived from
     * @return the exchange of the event as a {@link String} in the form of: <br>
     * {@code <application>.<domain>.<dispatchType>} or <br>
     * {@code project.store.direct}
     */
    String resolveExchange(@Nonnull EventEnvelope eventEnvelope);

    /**
     * @param eventEnvelope the instance of a message from which the routing key should be derived from
     * @return the routing key of the event as a {@link String} in the form of: <br>
     * {@code <domain>.<eventName>} or <br>
     * {@code store.store_close}
     */
    String resolveRoutingKey(@Nonnull EventEnvelope eventEnvelope);

    /**
     * @param eventEnvelope the instance of a message from which the binding should be derived from
     * @return the binding of the event as a {@link String} in the form of: <br>
     * {@code <domain>.<eventName>} or <br>
     * {@code store.store_close}
     */
    String resolveBinding(@Nonnull EventEnvelope eventEnvelope);
}