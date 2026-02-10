package io.github.plaguv.messaging.listener;

import io.github.plaguv.contract.envelope.payload.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AmqpEventListenerDiscoverer implements EventListenerDiscoverer, SmartInitializingSingleton {

    private static final Logger log = LoggerFactory.getLogger(AmqpEventListenerDiscoverer.class);

    private final ListableBeanFactory beanFactory;
    private final Map<Method, Class<?>> listeners = new ConcurrentHashMap<>();

    public AmqpEventListenerDiscoverer(ListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterSingletonsInstantiated() {
        Arrays.stream(beanFactory.getBeanNamesForType(Object.class))
                .map(beanFactory::getBean)
                .forEach(this::putIfAmqpListenerMethodPresent);
    }

    private void putIfAmqpListenerMethodPresent(Object bean) {
        for (Method method : bean.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(AmqpListener.class)) {
                continue;
            }
            if (method.getParameterCount() < 1) {
                log.atWarn().log("Method '{}' in bean '{}' was annotated with @AmqpListener but has no parameters.", method, bean);
                continue;
            }
            if (method.getParameterCount() > 1) {
                log.atWarn().log(
                        "Method '{}' in bean '{}' was annotated with @AmqpListener but has too many parameters.",
                        method, bean);
                continue;
            }

            Parameter parameter = method.getParameters()[0];
            Class<?> parameterType = method.getParameterTypes()[0];

            if (!parameterType.isAnnotationPresent(Event.class)) {
                log.atWarn().log("Method '{}' in bean '{}' was annotated with '@AmqpListener' but its parameter '{}' is not annotated with '@Event'.",
                        method, bean, parameter);
                 continue;
            }

            listeners.putIfAbsent(method, parameterType);
        }
    }

    @Override
    public Map<Method, Class<?>> getListeners() {
        return listeners;
    }
}