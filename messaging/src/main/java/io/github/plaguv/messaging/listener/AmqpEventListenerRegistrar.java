package io.github.plaguv.messaging.listener;

import io.github.plaguv.contract.envelope.payload.EventInstance;
import io.github.plaguv.messaging.utlity.EventRouter;
import io.github.plaguv.messaging.utlity.TopologyDeclarer;
import jakarta.annotation.Nonnull;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;

public class AmqpEventListenerRegistrar implements EventListenerRegistrar, RabbitListenerConfigurer, ApplicationContextAware{

    private final EventRouter eventRouter;
    private final TopologyDeclarer topologyDeclarer;

    private ApplicationContext applicationContext;
    private RabbitListenerEndpointRegistrar endpointRegistrar;

    public AmqpEventListenerRegistrar(EventRouter eventRouter, TopologyDeclarer topologyDeclarer) {
        this.eventRouter = eventRouter;
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

    private Class<? extends EventInstance> resolveEventInstance(Method method) {
        Class<?>[] parameters = method.getParameterTypes();

        if (parameters.length != 1) {
            throw new IllegalStateException(
                    "@AmqpListener method '%s' must have exactly one parameter, but had '%d'".formatted(method.getName(), parameters.length)
            );
        }

        Class<?> parameter = parameters[0];

        if (!EventInstance.class.isAssignableFrom(parameter)) {
            throw new IllegalStateException(
                    "@AmqpListener method '%s' must have a parameter of type EventInstance, but had '%s'".formatted(method.getName(), parameter.getComponentType().getSimpleName())
            );
        }

        @SuppressWarnings("unchecked")
        Class<? extends EventInstance> eventClass = (Class<? extends EventInstance>) parameter;

        return eventClass;
    }

    private void registerListenerForEvent(Object bean, String beanName, Method method, Class<? extends EventInstance> event) {
        if (endpointRegistrar == null) {
            throw new IllegalStateException("RabbitListenerEndpointRegistrar not initialized yet");
        }

        System.out.println(bean.getClass().getName());
        System.out.println(beanName);
        System.out.println(method.getName());
        System.out.println(event.getSimpleName());

//        EventEnvelope envelope = null;
//        String queueName = eventRouter.resolveQueue(event);
//        topologyDeclarer.declareAllIfAbsent(envelope);
//
//        MethodRabbitListenerEndpoint endpoint = new MethodRabbitListenerEndpoint();
//        endpoint.setBean(bean);
//        endpoint.setMethod(method);
//        endpoint.setQueueNames(queueName);
//        endpoint.setId("%s#%s#%s".formatted(beanName, method.getName(), event.name()));
//        endpoint.setExclusive(false);
//
//        RabbitListenerContainerFactory<?> factory = applicationContext.getBean(RabbitListenerContainerFactory.class);
//
//        endpointRegistrar.registerEndpoint(endpoint, factory);
    }
}