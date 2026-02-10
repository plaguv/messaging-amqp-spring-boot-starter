package io.github.plaguv.messaging.utlity;

import io.github.plaguv.contract.envelope.EventEnvelope;
import io.github.plaguv.messaging.config.properties.AmqpProperties;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AmqpTopologyDeclarer implements TopologyDeclarer {

    private static final Logger log = LoggerFactory.getLogger(AmqpTopologyDeclarer.class);

    private final RabbitAdmin rabbitAdmin;
    private final EventRouter eventRouter;
    private final AmqpProperties amqpProperties;

    private final ConcurrentMap<String, Exchange> declaredExchanges = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Queue> declaredQueues = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Binding> declaredBindings = new ConcurrentHashMap<>();

    public AmqpTopologyDeclarer(RabbitAdmin rabbitAdmin, EventRouter eventRouter, AmqpProperties amqpProperties) {
        this.rabbitAdmin = rabbitAdmin;
        this.eventRouter = eventRouter;
        this.amqpProperties = amqpProperties;
    }

    @Override
    public Exchange declareExchangeIfAbsent(@Nonnull EventEnvelope eventEnvelope) {
        String exchangeName = eventRouter.resolveExchange(eventEnvelope);
        return declaredExchanges.computeIfAbsent(exchangeName, name -> {
            Exchange exchange = new TopicExchange(
                    name,
                    amqpProperties.declareExchangeDurable(),
                    amqpProperties.declareExchangeDeletable()
            );

            rabbitAdmin.declareExchange(exchange);
            log.atInfo().log("Declared centralExchange '{}'", name);

            return exchange;
        });
    }

    @Override
    public Queue declareQueueIfAbsent(@Nonnull EventEnvelope eventEnvelope) {
        String queueName = eventRouter.resolveQueue(eventEnvelope);

        return declaredQueues.computeIfAbsent(queueName, name -> {
            Queue queue = new Queue(
                    name,
                    amqpProperties.declareQueueDurable(),
                    amqpProperties.declareQueueExclusive(),
                    amqpProperties.declareQueueDeletable()
            );

            rabbitAdmin.declareQueue(queue);
            log.atInfo().log("Declared queue '{}'", name);

            return queue;
        });
    }

    @Override
    public Set<Binding> declareBindingIfAbsent(@Nonnull EventEnvelope eventEnvelope) {
        Set<String> bindingKeys = eventRouter.resolveBindingKey(eventEnvelope);
        String exchangeName = eventRouter.resolveExchange(eventEnvelope);
        String queueName = eventRouter.resolveQueue(eventEnvelope);

        // Ensure required topology exists
        declareExchangeIfAbsent(eventEnvelope);
        declareQueueIfAbsent(eventEnvelope);

        Set<Binding> setOfBindings = new HashSet<>();

        Queue queue = declaredQueues.get(queueName);
        Exchange exchange = declaredExchanges.get(exchangeName);

        for (String binding : bindingKeys) {

        }

//        return declaredBindings.computeIfAbsent(bindingKeys, key -> {
//
//            Binding binding = switch (exchange.getType()) {
//                case ExchangeTypes.DIRECT -> BindingBuilder.bind(queue)
//                        .to((DirectExchange) exchange)
//                        .with(key);
//                case ExchangeTypes.TOPIC -> BindingBuilder.bind(queue)
//                        .to((TopicExchange) exchange)
//                        .with(key);
//                case ExchangeTypes.FANOUT -> BindingBuilder
//                        .bind(queue)
//                        .to((FanoutExchange) exchange);
//                default -> throw new IllegalStateException("Unknown centralExchange type: " + exchange.getType());
//            };
//
//            rabbitAdmin.declareBinding(binding);
//            log.atInfo().log("Declared binding '{}' for centralExchange '{}' -> queue '{}'", key, exchangeName, queueName);
//
//            return binding;
//        });
        return null;
    }
}