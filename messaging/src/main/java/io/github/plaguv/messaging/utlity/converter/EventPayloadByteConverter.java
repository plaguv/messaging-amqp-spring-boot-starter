package io.github.plaguv.messaging.utlity.converter;

import io.github.plaguv.contract.envelope.payload.EventPayload;
import org.jspecify.annotations.NullMarked;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import tools.jackson.databind.ObjectMapper;

public class EventPayloadByteConverter implements MessageConverter {

    private final ObjectMapper objectMapper;

    public EventPayloadByteConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    @NullMarked
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        try {
            byte[] body = objectMapper.writeValueAsBytes(object);
            return new Message(body, messageProperties);
        } catch (Exception e) {
            throw new MessageConversionException("Failed to serialize EventEnvelope", e);
        }
    }

    @Override
    @NullMarked
    public Object fromMessage(Message message) throws MessageConversionException {
        try {
            return objectMapper.readValue(message.getBody(), EventPayload.class);
        } catch (Exception e) {
            throw new MessageConversionException("Failed to convert EventEnvelope", e);
        }
    }
}