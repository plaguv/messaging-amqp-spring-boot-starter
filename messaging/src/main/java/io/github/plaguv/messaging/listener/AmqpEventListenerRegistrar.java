package io.github.plaguv.messaging.listener;

import io.github.plaguv.contract.envelope.EventEnvelope;
import io.github.plaguv.messaging.utlity.EventRouter;
import io.github.plaguv.messaging.utlity.TopologyDeclarer;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.listener.MethodRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.Arrays;

public class AmqpEventListenerRegistrar implements EventListenerRegistrar, RabbitListenerConfigurer, ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(AmqpEventListenerRegistrar.class);
    private final EventRouter eventRouter;
    private final EventListenerDiscoverer discoverer;
    private final TopologyDeclarer topologyDeclarer;

    private ApplicationContext applicationContext;
    private RabbitListenerEndpointRegistrar endpointRegistrar;

    public AmqpEventListenerRegistrar(EventRouter eventRouter, EventListenerDiscoverer discoverer, TopologyDeclarer topologyDeclarer) {
        this.eventRouter = eventRouter;
        this.discoverer = discoverer;
        this.topologyDeclarer = topologyDeclarer;
    }

    @Override
    public void configureRabbitListeners(@Nonnull RabbitListenerEndpointRegistrar registrar) {
        this.endpointRegistrar = registrar;
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void registerListener(@Nonnull Object bean, @Nonnull Method method) {
        if (!Arrays.stream(bean.getClass().getDeclaredMethods()).toList().contains(method)) {
            throw new IllegalArgumentException("Parameter 'method' '%s' is not contained in parameter 'bean' '%s'."
                    .formatted(method, bean));
        }
        if (!discoverer.getListeners().containsKey(method)) {
            throw new IllegalStateException("Parameter 'method' '%s' has not been registered yet."
                    .formatted(method));
        }

        Class<?> eventClass = discoverer.getListeners().get(method);
        EventEnvelope eventEnvelope = EventEnvelope.builder().ofPayload(eventClass).build();

        topologyDeclarer.declareQueueIfAbsent(eventEnvelope);
        topologyDeclarer.declareExchangeIfAbsent(eventEnvelope);
        topologyDeclarer.declareBindingIfAbsent(eventEnvelope);

        String queueName = eventRouter.resolveQueue(eventEnvelope);

        MethodRabbitListenerEndpoint endpoint = new MethodRabbitListenerEndpoint();
        endpoint.setBean(bean);
        endpoint.setMethod(method);
        endpoint.setQueueNames(queueName);

        RabbitListenerContainerFactory<?> factory = applicationContext.getBean(RabbitListenerContainerFactory.class);

        endpointRegistrar.registerEndpoint(
                endpoint,
                factory
        );

        log.atInfo().log("Registered listener for '{}'", method.getName());
    }
}