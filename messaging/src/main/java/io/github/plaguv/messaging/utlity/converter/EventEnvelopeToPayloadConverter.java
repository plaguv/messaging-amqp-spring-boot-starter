package io.github.plaguv.messaging.utlity.converter;

import io.github.plaguv.contract.envelope.EventEnvelope;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import tools.jackson.databind.ObjectMapper;

public class EventEnvelopeToPayloadConverter implements MessageConverter {

    private final ObjectMapper objectMapper;

    public EventEnvelopeToPayloadConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        try {
            byte[] body = objectMapper.writeValueAsBytes(object);
            return new Message(body, messageProperties);
        } catch (Exception e) {
            throw new MessageConversionException("Failed to serialize EventEnvelope", e);
        }
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        try {
            EventEnvelope envelope = objectMapper.readValue(message.getBody(), EventEnvelope.class);
            return envelope.payload().payload(); // <-- return only the payload
        } catch (Exception e) {
            throw new MessageConversionException("Failed to convert EventEnvelope", e);
        }
    }
}
