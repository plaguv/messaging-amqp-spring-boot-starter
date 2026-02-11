package io.github.plaguv.messaging.utlity.helper;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public record Listener(
        Object bean,
        Method method,
        Parameter parameter
) {
    public Listener {
        if (bean == null) {
            throw new IllegalArgumentException("Listener attribute 'bean' cannot be null");
        }
        if (method == null) {
            throw new IllegalArgumentException("Listener attribute 'method' cannot be null");
        }
        if (parameter == null) {
            throw new IllegalArgumentException("Listener attribute 'parameter' cannot be null");
        }
    }
}