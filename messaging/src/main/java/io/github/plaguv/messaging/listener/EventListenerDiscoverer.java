package io.github.plaguv.messaging.listener;

import java.lang.reflect.Method;
import java.util.Map;

public interface EventListenerDiscoverer {
    Map<Method, Class<?>> getListeners();
}