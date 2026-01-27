package io.github.plaguv.messaging.utlity;

import io.github.plaguv.contracts.common.EventEnvelope;
import jakarta.annotation.Nonnull;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class AmqpTopologyDeclarer implements TopologyDeclarer {

    private static final Logger log = LoggerFactory.getLogger(AmqpTopologyDeclarer.class);

    private final RabbitAdmin rabbitAdmin;
    private final EventRouter eventRouter;

    private final ConcurrentMap<String, Exchange> declaredExchanges = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Queue> declaredQueues = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Binding> declaredBindings = new ConcurrentHashMap<>();

    public AmqpTopologyDeclarer(RabbitAdmin rabbitAdmin, EventRouter eventRouter) {
        this.rabbitAdmin = rabbitAdmin;
        this.eventRouter = eventRouter;
    }

    @Override
    public void declareExchangeIfAbsent(@Nonnull EventEnvelope eventEnvelope) {
        String exchangeName = eventRouter.resolveExchange(eventEnvelope);

        declaredExchanges.computeIfAbsent(exchangeName, name -> {
            Exchange exchange = switch (eventEnvelope.routing().eventDispatchType()) {
                case TOPIC     -> new TopicExchange (name, true, false);
                case DIRECT    -> new DirectExchange(name, true, false);
                case BROADCAST -> new FanoutExchange(name, true, false);
            };

            rabbitAdmin.declareExchange(exchange);
            log.info("Declared exchange '{}'", name);
            return exchange;
        });
    }

    @Override
    public void declareQueueIfAbsent(@Nonnull EventEnvelope eventEnvelope) {
        String queueName = eventRouter.resolveQueue(eventEnvelope);

        declaredQueues.computeIfAbsent(queueName, name -> {
            Queue queue = new Queue(name, true, false, false);

            rabbitAdmin.declareQueue(queue);
            log.info("Declared queue '{}'", name);

            return queue;
        });
    }

    @Override
    public void declareBindingIfAbsent(@Nonnull EventEnvelope eventEnvelope) {
        String bindingKey = eventRouter.resolveBinding(eventEnvelope);
        String exchangeName = eventRouter.resolveExchange(eventEnvelope);
        String queueName = eventRouter.resolveQueue(eventEnvelope);

        // Ensure required topology exists
        declareExchangeIfAbsent(eventEnvelope);
        declareQueueIfAbsent(eventEnvelope);

        declaredBindings.computeIfAbsent(bindingKey, key -> {
            Queue queue = declaredQueues.get(queueName);
            Exchange exchange = declaredExchanges.get(exchangeName);
            Binding binding = switch (exchange.getType()) {
                case ExchangeTypes.DIRECT -> BindingBuilder.bind(queue)
                        .to((DirectExchange) exchange)
                        .with(key);
                case ExchangeTypes.TOPIC -> BindingBuilder.bind(queue)
                        .to((TopicExchange) exchange)
                        .with(key);
                case ExchangeTypes.FANOUT -> BindingBuilder
                        .bind(queue)
                        .to((FanoutExchange) exchange);
                default -> throw new IllegalStateException("Unknown exchange type: " + exchange.getType());
            };

            rabbitAdmin.declareBinding(binding);
            log.info("Declared binding '{}' for exchange '{}' -> queue '{}'", key, exchangeName, queueName);

            return binding;
        });
    }

    @Override
    public void declareAllIfAbsent(@NonNull EventEnvelope eventEnvelope) {
        declareExchangeIfAbsent(eventEnvelope);
        declareQueueIfAbsent(eventEnvelope);
        declareBindingIfAbsent(eventEnvelope);
    }
}