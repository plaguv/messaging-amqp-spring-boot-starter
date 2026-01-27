package io.github.plaguv.messaging.listener;

import io.github.plaguv.contracts.common.EventEnvelope;
import io.github.plaguv.contracts.common.routing.EventType;
import io.github.plaguv.messaging.utlity.EventRouter;
import io.github.plaguv.messaging.utlity.TopologyDeclarer;
import org.jspecify.annotations.NonNull;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.listener.MethodRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class AmqpEventListenerRegistrar implements
        EventListenerRegistrar,
        RabbitListenerConfigurer,
        ApplicationContextAware,
        BeanPostProcessor {

    private final EventRouter eventRouter;
    private final TopologyDeclarer topologyDeclarer;

    private ApplicationContext applicationContext;
    private RabbitListenerEndpointRegistrar endpointRegistrar;

    public AmqpEventListenerRegistrar(EventRouter eventRouter, TopologyDeclarer topologyDeclarer) {
        this.eventRouter = eventRouter;
        this.topologyDeclarer = topologyDeclarer;
    }

    @Override
    public void configureRabbitListeners(@NonNull RabbitListenerEndpointRegistrar registrar) {
        this.endpointRegistrar = registrar;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, @NonNull String beanName)
            throws BeansException {

        for (Method method : bean.getClass().getDeclaredMethods()) {

            AmqpListener listener = method.getAnnotation(AmqpListener.class);
            if (listener == null) continue;

            EventType event = listener.event();
            registerListenerForEvent(bean, beanName, method, event);
        }
        return bean;
    }

    private void registerListenerForEvent(Object bean, String beanName, Method method, EventType event) {
        if (endpointRegistrar == null) {
            throw new IllegalStateException("RabbitListenerEndpointRegistrar not initialized yet");
        }

        // TODO replace with actual envelope creation
        EventEnvelope envelope = null;
        String queueName = eventRouter.resolveQueue(envelope);
        topologyDeclarer.declareAllIfAbsent(envelope);

        MethodRabbitListenerEndpoint endpoint = new MethodRabbitListenerEndpoint();
        endpoint.setBean(bean);
        endpoint.setMethod(method);
        endpoint.setQueueNames(queueName);
        endpoint.setId("%s#%s#%s".formatted(beanName, method.getName(), event.name()));
        endpoint.setExclusive(false);

        RabbitListenerContainerFactory<?> factory =
                applicationContext.getBean(RabbitListenerContainerFactory.class);

        endpointRegistrar.registerEndpoint(endpoint, factory);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }
}