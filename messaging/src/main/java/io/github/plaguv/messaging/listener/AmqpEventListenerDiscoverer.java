package io.github.plaguv.messaging.listener;

import io.github.plaguv.contract.envelope.payload.EventInstance;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AmqpEventListenerDiscoverer implements EventListenerDiscoverer, BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(AmqpEventListenerDiscoverer.class);

    private final Map<Method, Class<? extends EventInstance>> listenerMethods = new ConcurrentHashMap<>();

    public AmqpEventListenerDiscoverer() {
    }

    @Override
    public @Nullable Object postProcessAfterInitialization(Object bean, @Nonnull String beanName) throws BeansException {
        for (Method method : bean.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(AmqpListener.class)) {
                continue;
            }
            if (method.getParameterCount() < 1) {
                log.atWarn().log("Method {} was annotated with @AmqpListener, but had no parameter.", method.getName());
                continue;
            }
            if (method.getParameterCount() > 1) {
                log.atWarn().log("Method {} was annotated with @AmqpListener, but had too many parameters.", method.getName());
                continue;
            }
            if (!EventInstance.class.isAssignableFrom(method.getParameterTypes()[0])) {
                log.atWarn().log("Method {} was annotated with @AmqpListener, but implemented a parameter that doesn't extend 'EventInstance'.", method.getName());
            } else {
                listenerMethods.putIfAbsent(method, (Class<? extends EventInstance>) method.getParameterTypes()[0]);
            }
        }
        return bean;
    }

    @Override
    public @Nonnull Map<Method, Class<? extends EventInstance>> getListenerMethods() {
        return listenerMethods;
    }
}