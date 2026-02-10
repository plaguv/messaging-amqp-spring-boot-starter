package io.github.plaguv.messaging.utility;

import io.github.plaguv.contract.envelope.EventEnvelope;
import io.github.plaguv.contract.envelope.payload.pos.StoreOpenedEvent;
import io.github.plaguv.contract.envelope.routing.EventScope;
import io.github.plaguv.messaging.config.properties.AmqpProperties;
import io.github.plaguv.messaging.utlity.AmqpEventRouter;
import io.github.plaguv.messaging.utlity.EventRouter;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.Set;

class AmqpEventRouterTest {

    private EventRouter eventRouter;

    private StoreOpenedEvent storeOpenedEvent;
    private EventEnvelope eventEnvelope;

    @BeforeEach
    void beforeEach() {
        AmqpProperties amqpProperties = new AmqpProperties(
                "central",
                "starter",
                null,
                null,
                null,
                null,
                null
        );

        eventRouter = new AmqpEventRouter(
                amqpProperties
        );

        storeOpenedEvent = new StoreOpenedEvent(5L);

        eventEnvelope = EventEnvelope.builder()
                .ofPayload(storeOpenedEvent)
                .build();
    }

    @Test
    @DisplayName("Should resolve the queues name based on the event envelope")
    void resolveQueue() {
        Assertions.assertEquals(
                "starter.store.store_opened_event.queue",
                eventRouter.resolveQueue(eventEnvelope)
        );
    }

    @Test
    @DisplayName("Should resolve the exchanges name based on the event envelope")
    void resolveExchange() {
        Assertions.assertEquals(
                "central.events",
                eventRouter.resolveExchange(eventEnvelope)
        );
    }

    @Test
    @DisplayName("Should resolve the routing key based on the event envelope")
    void resolveRoutingKeyWith() {
        eventEnvelope = EventEnvelope.builder()
                .withEventScope(EventScope.BROADCAST)
                .ofPayload(storeOpenedEvent)
                .build();
        Assertions.assertEquals(
                "store.store_opened_event",
                eventRouter.resolveRoutingKey(eventEnvelope)
        );

        eventEnvelope = EventEnvelope.builder()
                .withEventScope(EventScope.GROUP)
                .withWildcard("cashier")
                .ofPayload(storeOpenedEvent)
                .build();
        Assertions.assertEquals(
                "store.store_opened_event.group.cashier",
                eventRouter.resolveRoutingKey(eventEnvelope)
        );

        eventEnvelope = EventEnvelope.builder()
                .withEventScope(EventScope.TARGET)
                .withWildcard("cashier")
                .ofPayload(storeOpenedEvent)
                .build();
        Assertions.assertEquals(
                "store.store_opened_event.target.cashier",
                eventRouter.resolveRoutingKey(eventEnvelope)
        );
    }

    @Test
    @DisplayName("Should resolve the binding keys based on the event envelope")
    void resolveBindingKey() {
        eventEnvelope = EventEnvelope.builder()
                .withWildcard("cashier")
                .ofPayload(storeOpenedEvent)
                .build();

        Set<String> expectedBindings = Set.of(
                "store.store_opened_event",                 // Scope: Broadcast
                "store.store_opened_event.group.cashier",   // Scope: Group
                "store.store_opened_event.target.cashier"   // Scope: Target
        );
        Set<String> actualBindings = eventRouter.resolveBindingKey(eventEnvelope);

        Assertions.assertEquals(expectedBindings.size(), actualBindings.size());
        Assertions.assertArrayEquals(
                Arrays.stream(expectedBindings.toArray()).sorted().toArray(),
                Arrays.stream(actualBindings.toArray()).sorted().toArray()
        );
    }
}