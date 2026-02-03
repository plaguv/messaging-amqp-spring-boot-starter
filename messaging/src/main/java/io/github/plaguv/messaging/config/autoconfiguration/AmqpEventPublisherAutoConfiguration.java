package io.github.plaguv.messaging.config.autoconfiguration;

import io.github.plaguv.messaging.publisher.*;
import io.github.plaguv.messaging.utlity.EventRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration;
import org.springframework.boot.amqp.autoconfigure.RabbitTemplateCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.ObjectMapper;

@AutoConfiguration(after = {RabbitAutoConfiguration.class, AmqpAutoConfiguration.class})
public class AmqpEventPublisherAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(AmqpEventPublisherAutoConfiguration.class);

    public AmqpEventPublisherAutoConfiguration() {}

    @Bean
    @ConditionalOnMissingBean
    public EventPublisher eventPublisher(RabbitTemplate rabbitTemplate, EventRouter eventRouter, ObjectMapper objectMapper) {
        return new AmqpEventPublisher(rabbitTemplate, eventRouter, objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public RabbitTemplateCustomizer amqpMandatoryPublisherCustomizer() {
        return rabbitTemplate -> {
            rabbitTemplate.setMandatory(true);
            rabbitTemplate.setReturnsCallback(returned ->
                    log.atError().log(
                            "Unroutable message. exchange={}, routingKey={}",
                            returned.getExchange(),
                            returned.getRoutingKey()
                    )
            );
        };
    }
}