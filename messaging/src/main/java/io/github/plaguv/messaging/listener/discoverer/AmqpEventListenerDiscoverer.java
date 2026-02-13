package io.github.plaguv.messaging.listener.discoverer;

import io.github.plaguv.contract.event.Event;
import io.github.plaguv.messaging.listener.AmqpListener;
import io.github.plaguv.messaging.utlity.helper.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AmqpEventListenerDiscoverer implements EventListenerDiscoverer {

    private static final Logger log = LoggerFactory.getLogger(AmqpEventListenerDiscoverer.class);

    private final ListableBeanFactory factory;

    public AmqpEventListenerDiscoverer(ListableBeanFactory factory) {
        this.factory = factory;
    }

    @Override
    public List<Listener> getListeners() {
        List<Listener> listeners = new ArrayList<>();

        for (String beanName : factory.getBeanDefinitionNames()) {
            if (!factory.containsBeanDefinition(beanName)) {
                continue;
            }

            Class<?> beanClass = factory.getType(beanName);
            if (beanClass == null) {
                continue;
            }
            beanClass = ClassUtils.getUserClass(beanClass);

            Map<Method, AmqpListener> annotatedMethods = MethodIntrospector.selectMethods(
                    beanClass,
                    (MethodIntrospector.MetadataLookup<AmqpListener>) method ->
                            AnnotatedElementUtils.findMergedAnnotation(method, AmqpListener.class)
            );

            if (!annotatedMethods.isEmpty()) {
                Object beanInstance = factory.getBean(beanName);

                annotatedMethods.keySet().stream()
                        .filter(this::isValidListenerMethod)
                        .map(method -> new Listener(beanInstance, method, method.getParameters()[0]))
                        .forEach(listeners::add);
            }
        }

        return listeners;
    }

    private boolean isValidListenerMethod(Method method) {
        int parameterCount = method.getParameterCount();

        if (parameterCount < 1) {
            log.atWarn().log("Method '{}' in class '{}' was annotated with @AmqpListener but has no parameters.", method, method.getDeclaringClass());
            return false;
        }

        if (parameterCount > 1) {
            log.atWarn().log(
                    "Method '{}' in class '{}' was annotated with @AmqpListener but has too many parameters.",
                    method, method.getDeclaringClass());
            return false;
        }

        Parameter parameter = method.getParameters()[0];

        if (!AnnotatedElementUtils.hasAnnotation(parameter.getType(), Event.class)) {
            log.atWarn().log("Method '{}' in class '{}' was annotated with '@AmqpListener' but its parameter '{}' is not annotated with '@Event'.",
                    method, method.getDeclaringClass(), parameter);
            return false;
        }
        return true;
    }
}