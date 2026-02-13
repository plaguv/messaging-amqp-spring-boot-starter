package io.github.plaguv.messaging.utlity.converter;

import io.github.plaguv.contract.envelope.payload.EventPayload;
import io.github.plaguv.contract.event.Event;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import tools.jackson.databind.ObjectMapper;

public class EventPayloadArgumentResolver implements HandlerMethodArgumentResolver {

    private final ObjectMapper objectMapper;

    public EventPayloadArgumentResolver(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Event.class) || parameter.getParameterType().isAnnotationPresent(Event.class);
    }

    @Override
    public @Nullable Object resolveArgument(@NonNull MethodParameter parameter, @NonNull Message<?> message) throws MessageConversionException {
        try {
                return objectMapper.readValue((byte[]) message.getPayload(), EventPayload.class);
        } catch (MessageConversionException e) {
            throw new MessageConversionException("Could not convert payload to EventPayload", e);
        }
    }
}