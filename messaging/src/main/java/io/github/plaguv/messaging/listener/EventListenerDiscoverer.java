package io.github.plaguv.messaging.listener;

import io.github.plaguv.contract.envelope.payload.EventInstance;
import jakarta.annotation.Nonnull;

import java.lang.reflect.Method;
import java.util.Map;

public interface EventListenerDiscoverer {
    @Nonnull Map<Method, Class<? extends EventInstance>> getListenerMethods();
}