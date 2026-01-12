package io.github.plaguv.messaging.config.autoconfiguration;

import io.github.plaguv.messaging.config.properties.AmqpProperties;
import io.github.plaguv.messaging.publisher.AmqpEventPublisher;
import io.github.plaguv.messaging.publisher.EventPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.ObjectMapper;

@AutoConfiguration(after = AmqpAutoConfiguration.class)
@EnableConfigurationProperties(AmqpProperties.class)
public class AmqpPublisherAutoConfiguration {

    public AmqpPublisherAutoConfiguration() {}

    @Bean
    @ConditionalOnMissingBean(EventPublisher.class)
    public EventPublisher eventPublisher(RabbitTemplate rabbitTemplate, AmqpProperties amqpProperties, ObjectMapper objectMapper) {
        return new AmqpEventPublisher(rabbitTemplate, amqpProperties, objectMapper);
    }
}