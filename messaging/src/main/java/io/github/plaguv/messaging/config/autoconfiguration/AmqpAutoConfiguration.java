package io.github.plaguv.messaging.config.autoconfiguration;

import io.github.plaguv.messaging.config.properties.AmqpProperties;
import io.github.plaguv.messaging.config.properties.AmqpStartupProperties;
import io.github.plaguv.messaging.utlity.AmqpEventRouter;
import io.github.plaguv.messaging.utlity.EventRouter;
import io.github.plaguv.messaging.utlity.converter.EventEnvelopeAmqpConverter;
import io.github.plaguv.messaging.utlity.converter.EventPayloadArgumentResolver;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import java.util.List;

@AutoConfiguration(after = RabbitAutoConfiguration.class)
@EnableConfigurationProperties({AmqpProperties.class, AmqpStartupProperties.class})
@EnableRabbit
public class AmqpAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("io.github.plaguv")
                .build();

        return JsonMapper.builder()
                .polymorphicTypeValidator(ptv)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public EventRouter eventRouter(AmqpProperties amqpProperties) {
        return new AmqpEventRouter(amqpProperties);
    }

    @Bean
    @ConditionalOnMissingBean(name = "rabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();

        factory.setMessageConverter(new EventEnvelopeAmqpConverter(objectMapper));
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setChannelTransacted(false);
        factory.setAdviceChain(RetryInterceptorBuilder.stateless()
                .maxRetries(5)
                .backOffOptions(1000, 5.0, 100_000)
                .build());

        return factory;
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageHandlerMethodFactory messageHandlerMethodFactory(ObjectMapper objectMapper) {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();

        factory.setCustomArgumentResolvers(List.of(
                new EventPayloadArgumentResolver(objectMapper)
        ));
        factory.afterPropertiesSet();

        return factory;
    }
}