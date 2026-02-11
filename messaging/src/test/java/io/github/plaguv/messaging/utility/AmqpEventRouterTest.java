package io.github.plaguv.messaging.utility;

import io.github.plaguv.contract.envelope.EventEnvelope;
import io.github.plaguv.contract.envelope.payload.EventPayload;
import io.github.plaguv.contract.event.pos.StoreOpenedEvent;
import io.github.plaguv.contract.envelope.routing.EventScope;
import io.github.plaguv.messaging.config.properties.AmqpProperties;
import io.github.plaguv.messaging.utlity.AmqpEventRouter;
import io.github.plaguv.messaging.utlity.EventRouter;
import org.junit.jupiter.api.*;

import java.util.Set;

class AmqpEventRouterTest {

    private EventRouter eventRouter;

    private StoreOpenedEvent storeOpenedEvent;
    private EventEnvelope eventEnvelope;

    @BeforeEach
    void beforeEach() {
        eventRouter = new AmqpEventRouter(
                new AmqpProperties(
                        "central",
                        "starter",
                        null,
                        null,
                        null,
                        null,
                        null
                ));

        storeOpenedEvent = new StoreOpenedEvent(5L);

        eventEnvelope = EventEnvelope.builderWithDefaults()
                .ofEventPayload(EventPayload.valueOf(storeOpenedEvent))
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
        eventEnvelope = EventEnvelope.builderWithDefaults()
                .ofEventPayload(EventPayload.valueOf(storeOpenedEvent))
                .build();
        Assertions.assertEquals(
                "store.store_opened_event",
                eventRouter.resolveRoutingKey(eventEnvelope)
        );

        eventEnvelope = EventEnvelope.builderWithDefaults()
                .withEventScope(EventScope.GROUP)
                .withWildcard("cashier")
                .ofEventPayload(EventPayload.valueOf(storeOpenedEvent))
                .build();
        Assertions.assertEquals(
                "store.store_opened_event.group.cashier",
                eventRouter.resolveRoutingKey(eventEnvelope)
        );

        eventEnvelope = EventEnvelope.builderWithDefaults()
                .withEventScope(EventScope.TARGET)
                .withWildcard("cashier")
                .ofEventPayload(EventPayload.valueOf(storeOpenedEvent))
                .build();
        Assertions.assertEquals(
                "store.store_opened_event.target.cashier",
                eventRouter.resolveRoutingKey(eventEnvelope)
        );
    }

    @Test
    @DisplayName("Should resolve the binding keys based on the event envelope")
    void resolveBindingKey() {
        eventEnvelope = EventEnvelope.builderWithDefaults()
                .withWildcard("starter")
                .ofEventPayload(EventPayload.valueOf(storeOpenedEvent))
                .build();

        Set<String> expectedBindings = Set.of(
                "store.store_opened_event",                 // Scope: Broadcast
                "store.store_opened_event.group.starter",   // Scope: Group
                "store.store_opened_event.target.starter"   // Scope: Target
        );
        Set<String> actualBindings = eventRouter.resolveBindingKey(eventEnvelope);

        Assertions.assertEquals(expectedBindings.size(), actualBindings.size());
        Assertions.assertArrayEquals(
                expectedBindings.stream().sorted().toArray(),
                actualBindings.stream().sorted().toArray()
        );
    }
}