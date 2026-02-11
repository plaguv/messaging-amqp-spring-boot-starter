package io.github.plaguv.messaging.config.autoconfiguration;

import io.github.plaguv.messaging.config.properties.AmqpProperties;
import io.github.plaguv.messaging.listener.discoverer.AmqpEventListenerDiscoverer;
import io.github.plaguv.messaging.listener.discoverer.EventListenerDiscoverer;
import io.github.plaguv.messaging.listener.topology.AmqpEventListenerTopology;
import io.github.plaguv.messaging.listener.topology.EventListenerTopology;
import io.github.plaguv.messaging.utlity.converter.EventEnvelopeToPayloadConverter;
import io.github.plaguv.messaging.utlity.EventRouter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.ObjectMapper;

@AutoConfiguration(after = AmqpAutoConfiguration.class)
public class AmqpEventListenerAutoConfiguration {

    public AmqpEventListenerAutoConfiguration() {}

    @Bean
    @ConditionalOnMissingBean
    public EventListenerDiscoverer eventListenerDiscoverer(ListableBeanFactory listableBeanFactory) {
        return new AmqpEventListenerDiscoverer(listableBeanFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public EventListenerTopology topologyDeclarer(AmqpProperties amqpProperties, EventRouter eventRouter) {
        return new AmqpEventListenerTopology(amqpProperties, eventRouter);
    }

    @Bean
    @ConditionalOnMissingBean
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(ConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new EventEnvelopeToPayloadConverter(objectMapper));
        return factory;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBooleanProperty(prefix = "amqp.skip", name = "register-listeners", havingValue = false, matchIfMissing = true)
    public Declarables declarables(EventListenerDiscoverer discoverer, EventListenerTopology eventListenerTopology) {
        Declarables declarables = new Declarables();
        eventListenerTopology.getDeclarablesFromListeners(discoverer.getListeners()).getDeclarables().forEach(System.out::println);
        return declarables;
    }
}