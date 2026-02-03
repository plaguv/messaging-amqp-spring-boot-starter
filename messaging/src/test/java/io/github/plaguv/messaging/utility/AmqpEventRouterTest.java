package io.github.plaguv.messaging.utility;

import io.github.plaguv.contract.envelope.EventEnvelope;
import io.github.plaguv.contract.envelope.payload.pos.StoreOpenedEvent;
import io.github.plaguv.messaging.config.properties.AmqpProperties;
import io.github.plaguv.messaging.utlity.AmqpEventRouter;
import io.github.plaguv.messaging.utlity.EventRouter;
import org.junit.jupiter.api.*;

class AmqpEventRouterTest {

    private AmqpProperties amqpProperties;
    private EventRouter eventRouter;
    private EventEnvelope eventEnvelope;

    @BeforeEach
    void beforeEach() {
        amqpProperties = new AmqpProperties(
                "integrationsprojekt",
                "starter"
        );
        eventRouter = new AmqpEventRouter(
                amqpProperties
        );
        eventEnvelope = EventEnvelope.builder()
                .withProducer(AmqpEventRouter.class)
                .ofPayload(new StoreOpenedEvent(5L))
                .build();
    }

    @Test
    @DisplayName("Should resolve the correct naming scheme based on the envelopes contents")
    void resolveNamingScheme() {
        Assertions.assertEquals("integrationsprojekt.starter.store.store_opened_event.queue", eventRouter.resolveQueue(eventEnvelope));
        Assertions.assertEquals("integrationsprojekt.starter.store.store_opened_event.fanout", eventRouter.resolveExchange(eventEnvelope));
        Assertions.assertEquals("store.store_opened_event.fanout", eventRouter.resolveRoutingKey(eventEnvelope));
        Assertions.assertEquals("store.store_opened_event.fanout", eventRouter.resolveBinding(eventEnvelope));
    }
}