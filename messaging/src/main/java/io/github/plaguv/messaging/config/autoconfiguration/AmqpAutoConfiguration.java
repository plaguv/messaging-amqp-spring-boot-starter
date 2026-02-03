package io.github.plaguv.messaging.config.autoconfiguration;

import io.github.plaguv.messaging.config.properties.AmqpProperties;
import io.github.plaguv.messaging.utlity.AmqpEventRouter;
import io.github.plaguv.messaging.utlity.AmqpTopologyDeclarer;
import io.github.plaguv.messaging.utlity.EventRouter;
import io.github.plaguv.messaging.utlity.TopologyDeclarer;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(after = RabbitAutoConfiguration.class)
@EnableConfigurationProperties(AmqpProperties.class)
public class AmqpAutoConfiguration {

    public AmqpAutoConfiguration() {}

    @Bean
    @ConditionalOnMissingBean
    public EventRouter eventRouter(AmqpProperties amqpProperties) {
        return new AmqpEventRouter(amqpProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public TopologyDeclarer topologyDeclarer(RabbitAdmin rabbitAdmin, EventRouter eventRouter) {
        return new AmqpTopologyDeclarer(rabbitAdmin, eventRouter);
    }
}