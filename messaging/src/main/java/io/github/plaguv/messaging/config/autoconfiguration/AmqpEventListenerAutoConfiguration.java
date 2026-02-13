package io.github.plaguv.messaging.config.autoconfiguration;

import io.github.plaguv.messaging.config.properties.AmqpProperties;
import io.github.plaguv.messaging.listener.discoverer.AmqpEventListenerDiscoverer;
import io.github.plaguv.messaging.listener.discoverer.EventListenerDiscoverer;
import io.github.plaguv.messaging.listener.registrar.AmqpEventListenerRegistrar;
import io.github.plaguv.messaging.listener.registrar.EventListenerRegistrar;
import io.github.plaguv.messaging.listener.topology.AmqpEventListenerTopology;
import io.github.plaguv.messaging.listener.topology.EventListenerTopology;
import io.github.plaguv.messaging.utlity.EventRouter;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

@AutoConfiguration(after = AmqpAutoConfiguration.class)
public class AmqpEventListenerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public EventListenerDiscoverer eventListenerDiscoverer(ListableBeanFactory listableBeanFactory) {
        return new AmqpEventListenerDiscoverer(listableBeanFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public EventListenerRegistrar eventListenerRegistrar(MessageHandlerMethodFactory factory, EventListenerDiscoverer discoverer, EventRouter router) {
        return new AmqpEventListenerRegistrar(factory, discoverer, router);
    }

    @Bean
    @ConditionalOnMissingBean
    public EventListenerTopology topologyDeclarer(AmqpProperties amqpProperties, EventRouter eventRouter) {
        return new AmqpEventListenerTopology(amqpProperties, eventRouter);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBooleanProperty(prefix = "amqp.skip", name = "register-listeners", havingValue = false, matchIfMissing = true)
    public Declarables declarables(EventListenerDiscoverer discoverer, EventListenerTopology eventListenerTopology) {
        return eventListenerTopology.getDeclarablesFromListeners(discoverer.getListeners());
    }
}