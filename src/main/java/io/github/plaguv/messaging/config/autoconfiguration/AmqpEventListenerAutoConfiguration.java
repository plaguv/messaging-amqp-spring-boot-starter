package io.github.plaguv.messaging.config.autoconfiguration;

import io.github.plaguv.messaging.config.properties.AmqpProperties;
import io.github.plaguv.messaging.listener.AmqpEventListenerRegistrar;
import io.github.plaguv.messaging.listener.EventListenerRegistrar;
import io.github.plaguv.messaging.utlity.EventRouter;
import io.github.plaguv.messaging.utlity.TopologyDeclarer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(after = AmqpAutoConfiguration.class)
@EnableConfigurationProperties(AmqpProperties.class)
public class AmqpEventListenerAutoConfiguration {

    public AmqpEventListenerAutoConfiguration() {}

    @Bean
    @ConditionalOnMissingBean(EventListenerRegistrar.class)
    public EventListenerRegistrar eventListenerRegistrar(EventRouter eventRouter, TopologyDeclarer topologyDeclarer) {
        return new AmqpEventListenerRegistrar(eventRouter, topologyDeclarer);
    }
}