package io.github.plaguv.messaging.config.autoconfiguration;

import io.github.plaguv.messaging.config.properties.AmqpProperties;
import io.github.plaguv.messaging.publisher.*;
import io.github.plaguv.messaging.utlity.AmqpEventRouter;
import io.github.plaguv.messaging.utlity.AmqpTopologyDeclarer;
import io.github.plaguv.messaging.utlity.EventRouter;
import io.github.plaguv.messaging.utlity.TopologyDeclarer;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.ObjectMapper;

@AutoConfiguration(after = AmqpAutoConfiguration.class)
@EnableConfigurationProperties(AmqpProperties.class)
public class AmqpEventPublisherAutoConfiguration {

    public AmqpEventPublisherAutoConfiguration() {}

    @Bean
    @ConditionalOnMissingBean(EventPublisher.class)
    public EventPublisher eventPublisher(RabbitTemplate rabbitTemplate, TopologyDeclarer topologyDeclarer, EventRouter eventRouter, ObjectMapper objectMapper) {
        return new AmqpEventPublisher(rabbitTemplate, topologyDeclarer, eventRouter, objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean(EventRouter.class)
    public EventRouter eventRouter(AmqpProperties amqpProperties) {
        return new AmqpEventRouter(amqpProperties);
    }

    @Bean
    @ConditionalOnMissingBean(TopologyDeclarer.class)
    public TopologyDeclarer topologyDeclarer(RabbitAdmin rabbitAdmin, EventRouter eventRouter) {
        return new AmqpTopologyDeclarer(rabbitAdmin, eventRouter);
    }
}