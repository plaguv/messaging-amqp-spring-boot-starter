package io.github.plaguv.messaging.listener.topology;

import io.github.plaguv.contract.envelope.EventEnvelope;
import io.github.plaguv.contract.envelope.EventEnvelopeBuilder;
import io.github.plaguv.contract.envelope.payload.EventPayload;
import io.github.plaguv.messaging.config.properties.AmqpDeclarationProperties;
import io.github.plaguv.messaging.utlity.EventRouter;
import io.github.plaguv.messaging.utlity.helper.Listener;
import org.springframework.amqp.core.*;

import java.util.Collection;

public class AmqpEventListenerTopology implements EventListenerTopology {

    private final AmqpDeclarationProperties declarationProperties;
    private final EventRouter eventRouter;

    public AmqpEventListenerTopology(AmqpDeclarationProperties declarationProperties, EventRouter eventRouter) {
        this.declarationProperties = declarationProperties;
        this.eventRouter = eventRouter;
    }

    @Override
    public Declarables getDeclarablesFromListeners(Collection<Listener> listeners) {
        if (listeners == null) {
            throw new IllegalArgumentException("Parameter 'listeners' cannot be null");
        }

        Declarables declarables = new Declarables();

        for (Listener listener : listeners) {
            EventEnvelope envelope = EventEnvelopeBuilder.defaults()
                    .ofEventPayload(EventPayload.empty(listener.parameter().getType()))
                    .build();

            TopicExchange exchange = new TopicExchange(
                    eventRouter.resolveExchange(envelope),
                    declarationProperties.declareExchangeDurable(),
                    declarationProperties.declareExchangeDeletable()
            );
            declarables.getDeclarables().add(exchange);

            Queue queue = new Queue(
                    eventRouter.resolveQueue(envelope),
                    declarationProperties.declareQueueDurable(),
                    declarationProperties.declareQueueExclusive(),
                    declarationProperties.declareQueueDeletable()
            );
            declarables.getDeclarables().add(queue);


            for (String bindingKey : eventRouter.resolveBindingKey(envelope)) {
                Binding binding = BindingBuilder.bind(queue)
                        .to(exchange)
                        .with(bindingKey);
                declarables.getDeclarables().add(binding);
            }
        }

        return declarables;
    }
}