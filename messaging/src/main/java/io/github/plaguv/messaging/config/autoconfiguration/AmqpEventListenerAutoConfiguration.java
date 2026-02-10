package io.github.plaguv.messaging.config.autoconfiguration;

import io.github.plaguv.contract.envelope.EventEnvelope;
import io.github.plaguv.contract.event.Event;
import io.github.plaguv.contract.envelope.payload.EventPayload;
import io.github.plaguv.messaging.config.properties.AmqpProperties;
import io.github.plaguv.messaging.listener.AmqpListener;
import io.github.plaguv.messaging.utlity.EventRouter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Method;
import java.util.*;

//TODO: better formating
@AutoConfiguration(after = AmqpAutoConfiguration.class)
public class AmqpEventListenerAutoConfiguration {

    public AmqpEventListenerAutoConfiguration() {}

    @Bean
    public Declarables dynamicTopology(ListableBeanFactory beanFactory, EventRouter eventRouter, AmqpProperties props) {

        List<Declarable> adminObjects = new ArrayList<>();

        Map<String, Exchange> exchanges = new HashMap<>();
        Map<String, Queue> queues = new HashMap<>();
        Set<Binding> bindings = new HashSet<>();

        String[] beanNames = beanFactory.getBeanNamesForType(Object.class);

        for (String beanName : beanNames) {
            Class<?> type = beanFactory.getType(beanName);
            if (type == null) continue;

            for (Method method : type.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(AmqpListener.class)) continue;

                if (method.getParameterCount() != 1) continue;

                Class<?> parameterType = method.getParameterTypes()[0];

                if (!parameterType.isAnnotationPresent(Event.class)) continue;

                EventEnvelope envelope = EventEnvelope.builder()
                        .ofPayload(EventPayload.empty(parameterType))
                        .build();


                String exchangeName = eventRouter.resolveExchange(envelope);
                String queueName = eventRouter.resolveQueue(envelope);
                Set<String> bindingKeys = eventRouter.resolveBindingKey(envelope);

                Exchange exchange = exchanges.computeIfAbsent(exchangeName, name ->
                        new TopicExchange(name, props.declareExchangeDurable(), props.declareExchangeDeletable())
                );

                Queue queue = queues.computeIfAbsent(queueName, name ->
                        new Queue(name, props.declareQueueDurable(), props.declareQueueExclusive(), props.declareQueueDeletable())
                );

                for (String key : bindingKeys) {
                    bindings.add(BindingBuilder.bind(queue).to((TopicExchange) exchange).with(key));
                }
            }
        }

        adminObjects.addAll(exchanges.values());
        adminObjects.addAll(queues.values());
        adminObjects.addAll(bindings);

        adminObjects.forEach(System.out::println);

        return null;
    }
}